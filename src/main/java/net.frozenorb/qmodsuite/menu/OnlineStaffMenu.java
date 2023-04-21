package net.frozenorb.qmodsuite.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.frozenorb.qlib.menu.Button;
import net.frozenorb.qlib.menu.Menu;
import net.frozenorb.qmodsuite.qModSuite;
import net.frozenorb.qmodsuite.button.OnlineStaffButton;
import org.bukkit.entity.Player;

public class OnlineStaffMenu extends Menu {
   public OnlineStaffMenu() {
      this.setAutoUpdate(true);
   }

   public String getTitle(Player player) {
      return "§8Online staff";
   }

   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap();
      int index = 0;
      Iterator var4 = qModSuite.getInstance().getServer().getOnlinePlayers().iterator();

      while(var4.hasNext()) {
         Player staffMember = (Player)var4.next();
         if (staffMember != player && staffMember.hasPermission("qmodsuite.Use")) {
            buttons.put(index++, new OnlineStaffButton(staffMember.getUniqueId()));
         }
      }

      return buttons;
   }
}
