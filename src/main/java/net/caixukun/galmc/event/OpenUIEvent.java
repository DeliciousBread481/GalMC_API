package net.caixukun.galmc.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.caixukun.galmc.ui.CGGalleryScreen;
import net.caixukun.galmc.ui.GalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class OpenUIEvent {
   public static String path = null;
   public static UUID uuid = null;
   public static boolean cg = false;
   public static Map<Screen, Screen> screenScreenMap = new HashMap();
   public static int circle = -1;

   @SubscribeEvent
   public void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.side == LogicalSide.CLIENT) {
         if (cg) {
            circle = (Integer)Minecraft.m_91087_().f_91066_.m_231928_().m_231551_();
            Minecraft.m_91087_().f_91066_.m_231928_().m_231514_(4);
            Minecraft.m_91087_().f_91066_.m_92169_();
            Minecraft.m_91087_().m_5741_();
            if (event.player.m_20148_() == uuid) {
               Minecraft.m_91087_().m_91152_(new CGGalleryScreen(uuid));
               uuid = null;
               cg = false;
            }
         } else if (path != null && uuid != null && event.player.m_20148_() == uuid) {
            circle = (Integer)Minecraft.m_91087_().f_91066_.m_231928_().m_231551_();
            Minecraft.m_91087_().f_91066_.m_231928_().m_231514_(4);
            Minecraft.m_91087_().f_91066_.m_92169_();
            Minecraft.m_91087_().m_5741_();
            Minecraft.m_91087_().m_91152_(new GalScreen(path, uuid));
            path = null;
            uuid = null;
         }
      }

   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void onScreenClosing(ScreenEvent.Closing event) {
      if (circle != -1) {
         Minecraft.m_91087_().f_91066_.m_231928_().m_231514_(circle);
         Minecraft.m_91087_().f_91066_.m_92169_();
         Minecraft.m_91087_().m_5741_();
         circle = -1;
      }

      Screen currentScreen = event.getScreen();

      try {
         Screen parentScreen = (Screen)screenScreenMap.get(currentScreen);
         if (parentScreen != null) {
            Minecraft.m_91087_().m_91152_(parentScreen);
            event.setCanceled(true);
            screenScreenMap.clear();
         }
      } catch (Exception var3) {
      }

   }

   @SubscribeEvent
   public static void onKeyPressed1(ScreenEvent.Opening event) {
   }

   public static void openUI(String path, UUID uuid) {
      OpenUIEvent.uuid = uuid;
      OpenUIEvent.path = path;
   }

   public static void openCG(UUID uuid) {
      OpenUIEvent.uuid = uuid;
      cg = true;
   }

   public static void addScreen(Screen screen1, Screen screen2) {
      screenScreenMap.put(screen1, screen2);
   }

   public static void on_close_guiScale(int i) {
      circle = i;
   }
}
