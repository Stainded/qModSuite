package net.frozenorb.qmodsuite.packet;

import com.google.common.base.Preconditions;
import java.util.Iterator;
import net.frozenorb.qlib.xpacket.XPacket;
import net.frozenorb.qmodsuite.qModSuite;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class StaffRequestPacket implements XPacket {
   private static final String MESSAGE_FORMAT = ChatColor.translateAlternateColorCodes('&', "&9[Request] &7[%s] &b%s &7requested assistance\n     &9Reason: &7%s");
   private String server = Bukkit.getServerName();
   private String scope;
   private String sender;
   private String reason;

   public StaffRequestPacket(Player sender, String reason) {
      try {
         this.scope = Bukkit.getServerGroup();
      } catch (NoSuchMethodError var4) {
      }

      this.sender = sender.getName();
      this.reason = (String)Preconditions.checkNotNull(reason, "reason");
   }

   public void onReceive() {
      String finalMessage = String.format(MESSAGE_FORMAT, this.server, this.sender, this.reason);
      Iterator var2 = Bukkit.getOnlinePlayers().iterator();

      while(var2.hasNext()) {
         Player player = (Player)var2.next();
         if (player.hasPermission("basic.staff") && !qModSuite.getInstance().getSilencedStaffMembers().contains(player.getUniqueId())) {
            player.sendMessage(finalMessage);
         }
      }

      qModSuite.getInstance().getLogger().info("[Request] " + this.sender + ": " + this.reason);
   }

   public String getServer() {
      return this.server;
   }

   public String getScope() {
      return this.scope;
   }

   public String getSender() {
      return this.sender;
   }

   public String getReason() {
      return this.reason;
   }
}
