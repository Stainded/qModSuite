package net.frozenorb.qmodsuite.command;

import net.frozenorb.qlib.command.Command;
import net.frozenorb.qlib.command.Param;
import net.frozenorb.qmodsuite.utils.ModUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ModSuiteCommand {
   @Command(
      names = {"modsuite", "mm", "modmode", "v", "h", "mod"},
      permission = "qmodsuite.use"
   )
   public static void modSuite(Player sender, @Param(name = "player",defaultValue = "self") Player target) {
      if (!sender.equals(target) && !sender.hasPermission("qmodsuite.use.other")) {
         sender.sendMessage(ChatColor.RED + "You do not have permission to put other players into mod mode.");
      } else {
         boolean modMode = ModUtils.isModMode(target);
         if (target != sender) {
            sender.sendMessage(ChatColor.GRAY + "Toggled mod mode " + ChatColor.YELLOW + (modMode ? "§coff" : "§aon") + ChatColor.GRAY + " for " + target.getDisplayName() + ChatColor.GRAY + ".");
         }

         ModUtils.setModMode(!modMode, target);
      }
   }
}
