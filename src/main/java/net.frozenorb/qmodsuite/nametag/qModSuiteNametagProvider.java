package net.frozenorb.qmodsuite.nametag;

import com.cheatbreaker.api.CheatBreakerAPI;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.qlib.nametag.NametagInfo;
import net.frozenorb.qlib.nametag.NametagProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class qModSuiteNametagProvider extends NametagProvider {
   public qModSuiteNametagProvider() {
      super("qmodsuite Provider", 5);
   }

   public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
      List<String> modMode = new ArrayList();
      modMode.add(ChatColor.GRAY.toString() + "[Mod Mode]");
      modMode.add(ChatColor.GRAY + toRefresh.getName());
      if (toRefresh.hasMetadata("modmode")) {
         CheatBreakerAPI.getInstance().overrideNametag(toRefresh, modMode, refreshFor);
      }

      if (!toRefresh.hasMetadata("modmode")) {
         CheatBreakerAPI.getInstance().resetNametag(toRefresh, refreshFor);
      }

      return null;
   }
}
