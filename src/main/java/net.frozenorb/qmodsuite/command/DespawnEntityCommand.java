package net.frozenorb.qmodsuite.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.util.EntityUtils;
import net.frozenorb.qmodsuite.listeners.GeneralListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public final class DespawnEntityCommand {
   @Command(
      names = {"despawnentity"},
      permission = "basic.staff"
   )
   public static void despawnentity(Player sender) {
      if (!GeneralListener.getDespawn().containsKey(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.RED + "No entity to despawn.");
      } else {
         Entity entity = (Entity)GeneralListener.getDespawn().get(sender.getUniqueId());
         GeneralListener.getDespawn().remove(sender.getUniqueId());
         entity.remove();
         sender.sendMessage(ChatColor.GRAY + "Successfully despawned the " + ChatColor.AQUA + EntityUtils.getName(entity.getType()) + ChatColor.GRAY + ".");
      }
   }
}
