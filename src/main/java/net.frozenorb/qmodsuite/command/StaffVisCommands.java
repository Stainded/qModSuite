package net.frozenorb.qmodsuite.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qmodsuite.utils.ModUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class StaffVisCommands {
   @Command(
      names = {"hidestaff"},
      permission = "qmodsuite.use"
   )
   public static void hideStaff(Player player) {
      ModUtils.hideStaff(player);
      player.sendMessage(ChatColor.GRAY + "Staff members are now hidden.");
   }

   @Command(
      names = {"showstaff"},
      permission = "qmodsuite.use"
   )
   public static void showStaff(Player player) {
      ModUtils.showStaff(player);
      player.sendMessage(ChatColor.GRAY + "You can now see staff members.");
   }
}
