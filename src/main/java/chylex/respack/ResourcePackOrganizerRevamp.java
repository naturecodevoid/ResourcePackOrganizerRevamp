package chylex.respack;

import chylex.respack.ConfigHandler.DisplayPosition;
import chylex.respack.gui.GuiCustomResourcePacks;
import chylex.respack.render.RenderPackListOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@Mod(modid = ResourcePackOrganizerRevamp.MODID, version = ResourcePackOrganizerRevamp.VERSION, name = ResourcePackOrganizerRevamp.NAME, clientSideOnly = true, acceptedMinecraftVersions = "@MOD_ACCEPTED@", useMetadata = true, guiFactory = "chylex.respack.gui.GuiModConfig")
public final class ResourcePackOrganizerRevamp {
    public static final String MODID = "@MOD_ID@";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "@MOD_NAME@";

    private static ConfigHandler config;

    public static ConfigHandler getConfig() {
        return config;
    }

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e) {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            FMLLog.bigWarning(NAME + " cannot be installed on a server!");
            FMLCommonHandler.instance().exitJava(1, false);
        }

        config = new ConfigHandler(e.getSuggestedConfigurationFile());
        onConfigLoaded();

        MinecraftForge.EVENT_BUS.register(this);

        RenderPackListOverlay.refreshPackNames();
    }

    @NetworkCheckHandler
    public boolean onNetworkCheck(Map<String, String> versions, Side side) {
        return true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    @SideOnly(Side.CLIENT)
    public void onGuiOpen(GuiOpenEvent e) {
        if (e.gui != null && e.gui.getClass() == GuiScreenResourcePacks.class) {
            e.gui = new GuiCustomResourcePacks(Minecraft.getMinecraft().currentScreen);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onConfigChanged(OnConfigChangedEvent e) {
        if (e.modID.equals(MODID)) {
            config.reload();
            onConfigLoaded();
        }
    }

    private void onConfigLoaded() {
        if (config.options.getDisplayPosition() == DisplayPosition.DISABLED) {
            RenderPackListOverlay.unregister();
        } else {
            RenderPackListOverlay.register();
        }
    }
}
