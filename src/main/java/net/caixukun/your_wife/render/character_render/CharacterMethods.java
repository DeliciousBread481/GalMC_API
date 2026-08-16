package net.caixukun.your_wife.render.character_render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.caixukun.galmc.ui.GalScreen;
import net.caixukun.galmc.ui.character.TextEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public class CharacterMethods {
   public int pointer = 0;
   public int max = 0;
   int var1;
   int var2;
   int var3;
   int var4 = 0;

   public void execute(GuiGraphics guiGraphics, String render_id, List<TextEntry> data, GalScreen galScreen) {
      switch (render_id) {
         case "b_cg":
            this.e1(guiGraphics, data, galScreen);
         default:
      }
   }

   private void e1(GuiGraphics guiGraphics, List<TextEntry> data, GalScreen galScreen) {
   }

   public boolean next() {
      if (this.pointer == this.max) {
         this.pointer = 0;
         this.max = 0;
         this.var1 = 0;
         this.var2 = 0;
         this.var3 = 0;
         this.var4 = 0;
         return false;
      } else {
         ++this.pointer;
         return true;
      }
   }

   static class Tools {
      public static void renderContain(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ResourceLocation BACKGROUND_TEXTURE) {
         guiGraphics.m_280509_(0, 0, screenWidth, screenHeight, -16777216);
         float scaleX = (float)screenWidth / 1920.0F;
         float scaleY = (float)screenHeight / 1080.0F;
         float scale = Math.min(scaleX, scaleY);
         int renderWidth = (int)(1920.0F * scale);
         int renderHeight = (int)(1080.0F * scale);
         int renderX = (screenWidth - renderWidth) / 2;
         int renderY = (screenHeight - renderHeight) / 2;
         RenderSystem.setShader(GameRenderer::m_172817_);
         RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
         guiGraphics.m_280411_(BACKGROUND_TEXTURE, renderX, renderY, renderWidth, renderHeight, 0.0F, 0.0F, 1920, 1080, 1920, 1080);
      }

      private static int getX(int x, int width) {
         return (int)((double)width * (double)1.0F * ((double)x * (double)1.0F / (double)1920.0F));
      }

      private static int getY(int y, int height) {
         return (int)((double)height * (double)1.0F * ((double)y * (double)1.0F / (double)1080.0F));
      }
   }
}
