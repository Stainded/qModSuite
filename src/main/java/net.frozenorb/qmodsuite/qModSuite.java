package net.frozenorb.qmodsuite;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.qlib.command.FrozenCommandHandler;
import net.frozenorb.qlib.nametag.FrozenNametagHandler;
import net.frozenorb.qlib.visibility.FrozenVisibilityHandler;
import net.frozenorb.qlib.visibility.OverrideAction;
import net.frozenorb.qlib.visibility.VisibilityAction;
import net.frozenorb.qmodsuite.listeners.GeneralListener;
import net.frozenorb.qmodsuite.listeners.PreventionListener;
import net.frozenorb.qmodsuite.nametag.qModSuiteNametagProvider;
import net.frozenorb.qmodsuite.utils.ModUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class qModSuite extends JavaPlugin {
   public static final String MOD_MODE_METADATA = "modmode";
   public static final String INVIS_METADATA = "invisible";
   public static final String PERMISSION_USE = "qmodsuite.Use";
   private static qModSuite instance;
   private boolean showOtherStaff;
   private boolean enableOnJoin;
   private final Set<UUID> silencedStaffMembers = new HashSet();

   public void onEnable() {
      instance = this;
      this.saveDefaultConfig();
      this.showOtherStaff = this.getConfig().getBoolean("ShowOtherStaff", true);
      this.enableOnJoin = this.getConfig().getBoolean("EnableOnJoin", false);
      FrozenNametagHandler.registerProvider(new qModSuiteNametagProvider());
      FrozenCommandHandler.registerPackage(this, "net.frozenorb.qmodsuite.command");
      this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);
      this.getServer().getPluginManager().registerEvents(new PreventionListener(), this);
      FrozenVisibilityHandler.registerOverride("qModSuite Override Handler", (target, viewer) -> {
         return ModUtils.isModMode(target) && !ModUtils.isInvis(target) ? OverrideAction.SHOW : OverrideAction.NEUTRAL;
      });
      FrozenVisibilityHandler.registerHandler("qModSuite Visibility Handler", (target, viewer) -> {
         if (!ModUtils.isInvis(target)) {
            return VisibilityAction.NEUTRAL;
         } else {
            return this.isShowOtherStaff() && viewer.hasPermission("qmodsuite.use") && !ModUtils.hideStaff.contains(viewer.getUniqueId()) && ModUtils.isModMode(viewer) ? VisibilityAction.NEUTRAL : VisibilityAction.HIDE;
         }
      });
      Iterator var1 = this.getServer().getOnlinePlayers().iterator();

      while(var1.hasNext()) {
         Player onlinePlayer = (Player)var1.next();
         if (onlinePlayer.hasPermission("qmodsuite.use")) {
            ModUtils.enableModMode(onlinePlayer);
         }
      }

   }

   public void onDisable() {
      Iterator var1 = this.getServer().getOnlinePlayers().iterator();

      while(var1.hasNext()) {
         Player onlinePlayer = (Player)var1.next();
         if (ModUtils.isModMode(onlinePlayer)) {
            ModUtils.disableModMode(onlinePlayer);
         }
      }

      instance = null;
   }

   public static qModSuite getInstance() {
      return instance;
   }

   public boolean isShowOtherStaff() {
      return this.showOtherStaff;
   }

   public boolean isEnableOnJoin() {
      return this.enableOnJoin;
   }

   public Set<UUID> getSilencedStaffMembers() {
      return this.silencedStaffMembers;
   }
}
