package net.frozenorb.qmodsuite.command;

import java.util.concurrent.TimeUnit;
import net.frozenorb.basic.commands.event.PlayerReportEvent;
import net.frozenorb.basic.commands.event.PlayerRequestReportEvent;
import net.frozenorb.qlib.qLib;
import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qlib.xpacket.FrozenXPacketHandler;
import net.frozenorb.qmodsuite.qModSuite;
import net.frozenorb.qmodsuite.packet.ReportPacket;
import net.frozenorb.qmodsuite.packet.StaffRequestPacket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

public final class RequestCommands {
   private static boolean requestsEnabled = true;

   @Command(
      names = {"togglerequests", "togglereports"},
      permission = "op",
      description = "Toggle if requests/reports are allowed"
   )
   public static void togglerequests(Player sender, @Param(name = "enabled") boolean enabled) {
      if (requestsEnabled != enabled) {
         requestsEnabled = enabled;
         sender.sendMessage(ChatColor.GRAY + "Requests are now " + (requestsEnabled ? "§aenabled" : "§cdisabled") + "§7.");
      } else {
         sender.sendMessage(ChatColor.GRAY + "Requests are already " + (requestsEnabled ? "§aenabled" : "§cdisabled") + "§7.");
      }

   }

   @Command(
      names = {"request", "helpop"},
      permission = "",
      description = "Request staff help"
   )
   public static void request(Player sender, @Param(name = "reason",wildcard = true) String reason) {
      if (!requestsEnabled) {
         sender.sendMessage(ChatColor.GRAY + "Requests are currently disabled.");
      } else {
         Bukkit.getScheduler().runTaskAsynchronously(qModSuite.getInstance(), () -> {
            if (hasReportCooldown(sender)) {
               sender.sendMessage(ChatColor.GRAY + "You can only make one staff request every 2 minutes.");
            } else {
               Bukkit.getScheduler().runTask(qModSuite.getInstance(), () -> {
                  PlayerRequestReportEvent event = new PlayerRequestReportEvent(sender);
                  Bukkit.getPluginManager().callEvent(event);
                  if (event.isCancelled()) {
                     if (event.getCancelledMessage() != null) {
                        sender.sendMessage(event.getCancelledMessage());
                     } else {
                        sender.sendMessage(ChatColor.GRAY + "Request cancelled by another plugin.");
                     }

                  } else {
                     Bukkit.getScheduler().runTaskAsynchronously(qModSuite.getInstance(), () -> {
                        addReportCooldown(sender, 2L, TimeUnit.MINUTES);
                     });
                     FrozenXPacketHandler.sendToAll(new StaffRequestPacket(sender, reason));
                     sender.sendMessage(ChatColor.GRAY + "We have received your request.");
                  }
               });
            }
         });
      }
   }

   @Command(
      names = {"report"},
      permission = "",
      description = "Report a player for breaking the rules"
   )
   public static void report(Player sender, @Param(name = "player") Player target, @Param(name = "reason",wildcard = true) String reason) {
      if (!requestsEnabled) {
         sender.sendMessage(ChatColor.GRAY + "Reports are currently disabled.");
      } else {
         Bukkit.getScheduler().runTaskAsynchronously(qModSuite.getInstance(), () -> {
            if (hasReportCooldown(sender)) {
               sender.sendMessage(ChatColor.GRAY + "You can only make one staff request every 2 minutes.");
            } else {
               int reportCount = getReportCount(target);
               Bukkit.getScheduler().runTask(qModSuite.getInstance(), () -> {
                  PlayerRequestReportEvent event = new PlayerRequestReportEvent(sender);
                  Bukkit.getPluginManager().callEvent(event);
                  if (event.isCancelled()) {
                     if (event.getCancelledMessage() != null) {
                        sender.sendMessage(event.getCancelledMessage());
                     } else {
                        sender.sendMessage(ChatColor.GRAY + "Report cancelled by another plugin.");
                     }

                  } else {
                     Bukkit.getPluginManager().callEvent(new PlayerReportEvent(sender, target));
                     Bukkit.getScheduler().runTaskAsynchronously(qModSuite.getInstance(), () -> {
                        addReportCooldown(sender, 2L, TimeUnit.MINUTES);
                        incrementReportCount(target);
                     });
                     FrozenXPacketHandler.sendToAll(new ReportPacket(sender, target, reason, reportCount + 1));
                     sender.sendMessage(ChatColor.GRAY + "We have received your report.");
                  }
               });
            }
         });
      }
   }

   private static boolean hasReportCooldown(Player player) {
      String key = "report:" + player.getUniqueId() + ":cooldown";
      Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource();
      Throwable var3 = null;

      boolean var5;
      try {
         boolean bl = jedis.exists(key);
         var5 = bl;
      } catch (Throwable var14) {
         var3 = var14;
         throw var14;
      } finally {
         if (jedis != null) {
            if (var3 != null) {
               try {
                  jedis.close();
               } catch (Throwable var13) {
                  var3.addSuppressed(var13);
               }
            } else {
               jedis.close();
            }
         }

      }

      return var5;
   }

   private static void addReportCooldown(Player player, long time, TimeUnit unit) {
      String key = "report:" + player.getUniqueId() + ":cooldown";
      Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource();
      Throwable var6 = null;

      try {
         jedis.setex(key, (int)unit.toSeconds(time), "");
      } catch (Throwable var15) {
         var6 = var15;
         throw var15;
      } finally {
         if (jedis != null) {
            if (var6 != null) {
               try {
                  jedis.close();
               } catch (Throwable var14) {
                  var6.addSuppressed(var14);
               }
            } else {
               jedis.close();
            }
         }

      }

   }

   private static int getReportCount(Player target) {
      String key = "report:" + target.getUniqueId() + ":reports";
      long time = System.currentTimeMillis();
      Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource();
      Throwable var5 = null;

      int var7;
      try {
         int n = jedis.zcount(key, (double)(time - TimeUnit.MINUTES.toMillis(15L)), (double)time).intValue();
         var7 = n;
      } catch (Throwable var16) {
         var5 = var16;
         throw var16;
      } finally {
         if (jedis != null) {
            if (var5 != null) {
               try {
                  jedis.close();
               } catch (Throwable var15) {
                  var5.addSuppressed(var15);
               }
            } else {
               jedis.close();
            }
         }

      }

      return var7;
   }

   private static void incrementReportCount(Player target) {
      String key = "report:" + target.getUniqueId() + ":reports";
      long time = System.currentTimeMillis();
      Jedis jedis = qLib.getInstance().getBackboneJedisPool().getResource();
      Throwable var5 = null;

      try {
         jedis.zadd(key, (double)time, String.valueOf(time));
         jedis.expire(key, (int)TimeUnit.MINUTES.toMillis(15L));
      } catch (Throwable var14) {
         var5 = var14;
         throw var14;
      } finally {
         if (jedis != null) {
            if (var5 != null) {
               try {
                  jedis.close();
               } catch (Throwable var13) {
                  var5.addSuppressed(var13);
               }
            } else {
               jedis.close();
            }
         }

      }

   }
}
