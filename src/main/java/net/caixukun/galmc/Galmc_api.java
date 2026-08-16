package net.caixukun.galmc;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.caixukun.galmc.event.ExecuteEvent;
import net.caixukun.galmc.event.OpenUIEvent;
import net.caixukun.galmc.init.CommandInit;
import net.caixukun.galmc.init.ItemInit;
import net.caixukun.galmc.init.SoundInit;
import net.caixukun.galmc.resource.GalResourceManger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("galmc_api")
public class Galmc_api {
   public static final String MODID = "galmc_api";
   public static final Logger LOGGER = LogUtils.getLogger();

   public Galmc_api(FMLJavaModLoadingContext context) {
      IEventBus modEventBus = context.getModEventBus();
      GalResourceManger.handleResourcePack();
      GalResourceManger.getCCg();
      GalResourceManger.getText();
      GalResourceManger.getCgUI();
      ItemInit.ITEMS.register(modEventBus);
      SoundInit.SOUND_EVENTS.register(modEventBus);
      SoundInit.initSounds();
      MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
      modEventBus.addListener(this::commonSetup);
      MinecraftForge.EVENT_BUS.register(new ExecuteEvent());
      MinecraftForge.EVENT_BUS.register(new OpenUIEvent());
      modEventBus.addListener(this::addCreative);
   }

   private List<String> parsePackList(String jsonArray) {
      if (jsonArray.startsWith("[") && jsonArray.endsWith("]")) {
         String content = jsonArray.substring(1, jsonArray.length() - 1).trim();
         return (List<String>)(content.isEmpty() ? new ArrayList() : (List)Arrays.stream(content.split(",")).map((s) -> s.trim().replaceAll("^\"|\"$", "")).collect(Collectors.toList()));
      } else {
         return new ArrayList();
      }
   }

   private String toJsonArray(List<String> list) {
      Stream var10000 = list.stream().map((s) -> "\"" + s + "\"");
      return "[" + (String)var10000.collect(Collectors.joining(",")) + "]";
   }

   private void commonSetup(FMLCommonSetupEvent event) {
   }

   private void registerCommands(RegisterCommandsEvent event) {
      CommandInit.register(event.getDispatcher());
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.f_256869_) {
         event.accept(ItemInit.GUI_TEST);
      }

   }

   @SubscribeEvent
   public void onServerStarting(ServerStartingEvent event) {
      LOGGER.info("HELLO from server starting");
   }

   @EventBusSubscriber(
      modid = "galmc_api",
      bus = Bus.MOD,
      value = {Dist.CLIENT}
   )
   public static class ClientModEvents {
      @SubscribeEvent
      public static void onClientSetup(FMLClientSetupEvent event) {
         Galmc_api.LOGGER.info("HELLO FROM CLIENT SETUP");
         Galmc_api.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.m_91087_().m_91094_().m_92546_());
      }
   }
}
