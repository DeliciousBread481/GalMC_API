package net.caixukun.galmc.network;

import net.caixukun.galmc.event.OpenUIEvent;
import net.caixukun.galmc.ui.CGGalleryScreen;
import net.caixukun.galmc.ui.GalScreen;
import net.minecraft.client.Minecraft;

public class ClientPacketHandler {
   public static void open(String path, boolean cg) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null) {
         return;
      }

      OpenUIEvent.circle = (Integer)mc.options.guiScale().get();
      mc.options.guiScale().set(4);
      mc.options.save();
      mc.resizeDisplay();
      if (cg) {
         mc.setScreen(new CGGalleryScreen(mc.player.getUUID()));
      } else {
         mc.setScreen(new GalScreen(path, mc.player.getUUID()));
      }
   }
}