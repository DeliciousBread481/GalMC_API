package net.caixukun.galmc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.caixukun.galmc.resource.GalResourceManger;
import net.caixukun.galmc.ui.character.Character;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class CGGalleryScreen extends Screen {
   private static final int PAGE_SIZE = 6;
   private static final int THUMBNAIL_SPACING = 10;
   private static final float THUMBNAIL_ASPECT_RATIO = 1.7777778F;
   private int thumbnailWidth;
   private int thumbnailHeight;
   private List<Character> cgList = new ArrayList();
   private int currentPage = 0;
   private int totalPages;
   private List<Character> currentPageCGs;
   private UUID uuid;
   private Button prevPageButton;
   private Button nextPageButton;
   private List<CGThumbnailButton> thumbnailButtons = new ArrayList();

   public CGGalleryScreen(UUID uuid) {
      super(Component.m_237113_("CG鉴赏"));

      for(String path : GalResourceManger.getCCg()) {
         Character c = new Character(path);
         if (c.init()) {
            this.cgList.add(c);
         }
      }

      this.uuid = uuid;
      this.totalPages = (int)Math.ceil((double)this.cgList.size() / (double)6.0F);
      if (this.totalPages == 0) {
         this.totalPages = 1;
      }

      this.updateCurrentPageCGs();
   }

   protected void m_7856_() {
      super.m_7856_();
      int maxThumbnailWidth = (this.f_96543_ - 40) / 3;
      this.thumbnailWidth = Math.min(200, maxThumbnailWidth);
      this.thumbnailHeight = (int)((float)this.thumbnailWidth / 1.7777778F);
      int gridWidth = 3 * this.thumbnailWidth + 20;
      int startX = (this.f_96543_ - gridWidth) / 2;
      int startY = 60;
      this.thumbnailButtons.clear();

      for(int i = 0; i < 6; ++i) {
         int row = i / 3;
         int col = i % 3;
         int x = startX + col * (this.thumbnailWidth + 10);
         int y = startY + row * (this.thumbnailHeight + 10);
         CGThumbnailButton button = new CGThumbnailButton(x, y, this.thumbnailWidth, this.thumbnailHeight, (Character)null, (btn) -> {
            CGThumbnailButton clickedButton = (CGThumbnailButton)btn;
            if (clickedButton.getCharacter() != null) {
               Minecraft.m_91087_().m_91152_(new GalScreen(clickedButton.Character.id, this.uuid, this));
            }

         });
         this.m_142416_(button);
         this.thumbnailButtons.add(button);
      }

      this.prevPageButton = Button.m_253074_(Component.m_237113_("<"), (btn) -> this.turnPage(-1)).m_252987_(this.getX(60), this.getY(40), 40, 20).m_253136_();
      this.m_142416_(this.prevPageButton);
      this.nextPageButton = Button.m_253074_(Component.m_237113_(">"), (btn) -> this.turnPage(1)).m_252987_(this.getX(1800), this.getY(40), 40, 20).m_253136_();
      this.m_142416_(this.nextPageButton);
      this.updatePageDisplay();
   }

   private void turnPage(int delta) {
      int newPage = this.currentPage + delta;
      if (newPage >= 0 && newPage < this.totalPages) {
         this.currentPage = newPage;
         this.updateCurrentPageCGs();
         this.updatePageDisplay();
      }

   }

   private void updateCurrentPageCGs() {
      int fromIndex = this.currentPage * 6;
      int toIndex = Math.min(fromIndex + 6, this.cgList.size());
      this.currentPageCGs = this.cgList.subList(fromIndex, toIndex);
   }

   private void updatePageDisplay() {
      for(int i = 0; i < 6; ++i) {
         CGThumbnailButton btn = (CGThumbnailButton)this.thumbnailButtons.get(i);
         if (i < this.currentPageCGs.size()) {
            btn.setCharacter((Character)this.currentPageCGs.get(i));
            btn.f_93624_ = true;
         } else {
            btn.setCharacter((Character)null);
            btn.f_93624_ = false;
         }
      }

      this.prevPageButton.f_93623_ = this.currentPage > 0;
      this.nextPageButton.f_93623_ = this.currentPage < this.totalPages - 1;
   }

   public void m_88315_(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (GalResourceManger.cg_background != null) {
         this.renderContain(guiGraphics, this.f_96543_, this.f_96544_, ResourceLocation.fromNamespaceAndPath("galmc_api", GalResourceManger.cg_background));
      } else {
         this.m_280273_(guiGraphics);
      }

      guiGraphics.m_280653_(this.f_96547_, this.f_96539_, this.f_96543_ / 2, 20, 16777215);
      int var10000 = this.currentPage + 1;
      String pageText = var10000 + " / " + this.totalPages;
      guiGraphics.m_280137_(this.f_96547_, pageText, this.f_96543_ / 2, this.f_96544_ - 35, 11184810);
      super.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
   }

   public boolean m_7043_() {
      return false;
   }

   public void renderContain(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ResourceLocation BACKGROUND_TEXTURE) {
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

   public int getX(int x) {
      return (int)((double)this.f_96543_ * (double)1.0F * ((double)x * (double)1.0F / (double)1920.0F));
   }

   public int getY(int y) {
      return (int)((double)this.f_96544_ * (double)1.0F * ((double)y * (double)1.0F / (double)1080.0F));
   }

   private static class CGThumbnailButton extends Button {
      private Character Character;

      public CGThumbnailButton(int x, int y, int width, int height, Character entry, Button.OnPress onPress) {
         super(x, y, width, height, Component.m_237119_(), onPress, f_252438_);
         this.Character = entry;
      }

      public void setCharacter(Character entry) {
         this.Character = entry;
      }

      public Character getCharacter() {
         return this.Character;
      }

      protected void m_87963_(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
         if (this.Character != null) {
            guiGraphics.m_280509_(this.m_252754_(), this.m_252907_(), this.m_252754_() + this.f_93618_, this.m_252907_() + this.f_93619_, Integer.MIN_VALUE);
            ResourceLocation texture = this.Character.get().background;
            if (texture != null) {
               int imgWidth = this.Character.get().ix;
               int imgHeight = this.Character.get().iy;
               float scale = Math.min((float)this.f_93618_ / (float)imgWidth, (float)this.f_93619_ / (float)imgHeight);
               int scaledWidth = (int)((float)imgWidth * scale);
               int scaledHeight = (int)((float)imgHeight * scale);
               int offsetX = this.m_252754_() + (this.f_93618_ - scaledWidth) / 2;
               int offsetY = this.m_252907_() + (this.f_93619_ - scaledHeight) / 2;
               guiGraphics.m_280411_(texture, offsetX, offsetY, scaledWidth, scaledHeight, 0.0F, 0.0F, imgWidth, imgHeight, imgWidth, imgHeight);
            }

         }
      }
   }
}
