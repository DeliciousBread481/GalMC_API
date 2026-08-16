package net.caixukun.galmc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.UUID;
import net.caixukun.galmc.event.OpenUIEvent;
import net.caixukun.galmc.init.SoundInit;
import net.caixukun.galmc.ui.character.Character;
import net.caixukun.galmc.ui.character.NextText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

public class GalScreen extends Screen {
   public static final Logger LOGGER = LogUtils.getLogger();
   public Font fonta;
   ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath("galmc_api", "texture/gui/text.png");
   Character core;
   public boolean auto = false;
   public boolean skip = false;
   public boolean rendtext = true;
   public int text_speed = 2;
   private Button button1;
   private Button button2;
   private Button button3;
   private Button button4;
   NextText choice = null;
   public String old_music = null;
   private int music_tick;
   private int music_ticks = 0;
   private boolean cg;
   private CGGalleryScreen cgGalleryScreen = null;
   public UUID player;
   SoundInstance old_soundEvent = null;

   public GalScreen(String start, UUID player) {
      super(Component.m_237113_("启动！"));
      this.core = new Character(start);
      this.player = player;
      this.cg = false;
   }

   public GalScreen(String start, UUID player, CGGalleryScreen cgGalleryScreen) {
      super(Component.m_237113_("启动！"));
      this.core = new Character(start);
      this.player = player;
      this.cg = true;
      this.cgGalleryScreen = cgGalleryScreen;
   }

   public void m_88315_(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
      if (this.core.disabled) {
         this.m_7379_();
      } else {
         super.m_88315_(p_281549_, p_281550_, p_282878_, p_282465_);
         this.fonta = this.f_96547_;
         if (this.skip) {
            this.next();
         }

         this.core.render(p_281549_, this);

         for(Renderable renderable : this.f_169369_) {
            renderable.m_88315_(p_281549_, p_281550_, p_282878_, p_282465_);
         }

      }
   }

   protected void m_7856_() {
      if (this.core.init()) {
         assert this.f_96541_ != null;

         if (this.core.music != null) {
            if (!this.core.music.get("music").getAsString().equals(this.old_music)) {
               this.stopSound();

               try {
                  this.playSound((SoundEvent)((RegistryObject)SoundInit.REG_SOUNDS.get(this.core.music.get("music").getAsString())).get(), 1.0F, 1.0F);
               } catch (NullPointerException exception) {
                  LOGGER.error("音频播放失败：可能音频路径不正确", exception);
               }
            }
         } else {
            this.stopSound();
         }

         try {
            this.old_music = this.core.music.get("music").getAsString();
            this.music_tick = this.core.music.get("length").getAsInt();
         } catch (NullPointerException e) {
            LOGGER.error("获取音乐信息失败：可能音乐信息未填写", e);
         }

         if (this.music_tick == -1) {
            this.music_tick = 6000;
         }

         super.m_7856_();
      } else {
         super.m_7856_();
         LOGGER.error("初始化" + this.core.id + "GUI失败：请检查资源文件(json)格式完整性或者检查路径是否正确");
      }

   }

   public void m_86600_() {
      this.music();
   }

   private void music() {
      ++this.music_ticks;
      if (this.music_ticks == this.music_tick) {
         try {
            this.playSound((SoundEvent)((RegistryObject)SoundInit.REG_SOUNDS.get(this.core.music.get("music").getAsString())).get(), 1.0F, 1.0F);
         } catch (NullPointerException exception) {
            LOGGER.error("音频播放失败：可能音频路径不正确", exception);
         }

         this.music_ticks = 0;
      }

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

   public void renderImage(GuiGraphics guiGraphics, ResourceLocation TEXTURE, int ix, int iy, int wx, int wy) {
      double x = (double)this.f_96543_ * (double)1.0F;
      double y = (double)this.f_96544_ * (double)1.0F;
      if (x / y <= 1.7777777777777777) {
         double ny = x * (double)0.5625F;
         double ay = (y - ny) / (double)2.0F;
         wx = (int)((double)(wx / 1920) * x);
         wy = (int)(ay + (double)(wy / 1080) * ny);
         ix = (int)((double)(ix / 1920) * x);
         iy = (int)((double)(iy / 1920) * ny);
      } else {
         double nx = y * 1.7777777777777777;
         double ax = (x - nx) / (double)2.0F;
         wy = (int)((double)(wy / 1920) * y);
         wx = (int)(ax + (double)(wx / 1080) * y);
         iy = (int)((double)(iy / 1920) * y);
         ix = (int)((double)(ix / 1920) * nx);
      }

      guiGraphics.m_280163_(TEXTURE, wx, wy, 0.0F, 0.0F, ix, iy, ix, iy);
   }

   public void renderUI(GuiGraphics guiGraphics) {
      if (this.rendtext) {
         guiGraphics.m_280163_(this.GUI, this.getX(0, true), this.getY(740, true), 0.0F, 0.0F, this.getX(1920, false), this.getY(356, false), this.getX(1920, false), this.getY(356, false));
      }

   }

   private boolean isMouseOverArea(int mouseX, int mouseY, int x, int y, int width, int height) {
      return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
   }

   public boolean m_6375_(double mouseX, double mouseY, int buttona) {
      if (buttona == 0 && this.choice == null) {
         if (this.isMouseOverArea((int)mouseX, (int)mouseY, this.getX(1496, true), this.getY(1047, true), this.getX(100, true), this.getY(49, true))) {
            this.auto = !this.auto;
         } else if (this.isMouseOverArea((int)mouseX, (int)mouseY, this.getX(1608, true), this.getY(1047, true), this.getX(100, true), this.getY(49, true))) {
            this.skip = !this.skip;
         } else if (this.isMouseOverArea((int)mouseX, (int)mouseY, this.getX(1832, true), this.getY(1047, true), this.getX(100, true), this.getY(49, true))) {
            this.m_7379_();
         } else if (this.isMouseOverArea((int)mouseX, (int)mouseY, 0, this.getY(740, true), this.getX(1920, true), this.getY(356, true)) && this.rendtext) {
            this.next();
         }
      } else if (buttona == 1) {
         this.rendtext = !this.rendtext;
      }

      return super.m_6375_(mouseX, mouseY, buttona);
   }

   private void new_text(String path) {
      if (this.button1 != null) {
         this.button1.f_93624_ = false;
         this.button1.f_93623_ = false;
      }

      if (this.button2 != null) {
         this.button2.f_93624_ = false;
         this.button2.f_93623_ = false;
      }

      if (this.button3 != null) {
         this.button3.f_93624_ = false;
         this.button3.f_93623_ = false;
      }

      if (this.button4 != null) {
         this.button4.f_93624_ = false;
         this.button4.f_93623_ = false;
      }

      this.f_169369_.clear();
      this.core = new Character(path);
      this.button1 = null;
      this.button2 = null;
      this.choice = null;
      this.button3 = null;
      this.music_tick = 0;
      this.music_ticks = 0;
      this.skip = false;
      this.m_7856_();
   }

   public boolean next() {
      NextText nextText = this.core.next();
      if (nextText == null) {
         if (this.core.is_sound()) {
            try {
               this.playVoiceSound((SoundEvent)((RegistryObject)SoundInit.REG_SOUNDS.get(this.core.getSound())).get(), 1.0F, 1.0F);
            } catch (NullPointerException exception) {
               LOGGER.error("音频播放失败：可能音频路径不正确", exception);
            }
         }

         return true;
      } else {
         if (this.cg) {
            this.m_7379_();
            OpenUIEvent.openCG(this.player);
         } else if (Objects.equals(nextText.type, "end")) {
            this.core.execute.execute(this.player);
            this.m_7379_();
         } else {
            if (Objects.equals(nextText.type, "next")) {
               this.new_text((String)nextText.refer.get(0));
               return true;
            }

            if (Objects.equals(nextText.type, "choice")) {
               this.choice = nextText;
               int totalHeight = 100;
               int startY = (this.f_96544_ - totalHeight) / 2;

               try {
                  this.button1 = Button.m_253074_(Component.m_237113_((String)nextText.text.get(0)), (button) -> this.new_text((String)nextText.refer.get(0))).m_252987_((this.f_96543_ - 200) / 2, startY, 200, 20).m_252778_((button) -> Component.m_237113_((String)nextText.text.get(0))).m_253136_();
                  this.button2 = Button.m_253074_(Component.m_237113_((String)nextText.text.get(1)), (button) -> this.new_text((String)nextText.refer.get(1))).m_252987_((this.f_96543_ - 200) / 2, startY + 20 + 20, 200, 20).m_253136_();
                  this.m_142416_(this.button1);
                  this.m_142416_(this.button2);
                  if (nextText.refer.size() >= 3) {
                     this.button3 = Button.m_253074_(Component.m_237113_((String)nextText.text.get(2)), (button) -> this.new_text((String)nextText.refer.get(2))).m_252987_((this.f_96543_ - 200) / 2, startY + 80, 200, 20).m_253136_();
                     this.m_142416_(this.button3);
                  }

                  if (nextText.refer.size() == 4) {
                     this.button4 = Button.m_253074_(Component.m_237113_((String)nextText.text.get(3)), (button) -> this.new_text((String)nextText.refer.get(3))).m_252987_((this.f_96543_ - 200) / 2, startY + 80, 200, 20).m_253136_();
                     this.m_142416_(this.button4);
                  }
               } catch (IndexOutOfBoundsException e) {
                  LOGGER.error("按钮创建失败", e);
               }
            }
         }

         return true;
      }
   }

   public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 256) {
         return false;
      } else {
         if (keyCode == 265) {
            this.core.before();
         }

         if (keyCode == 298) {
            this.m_7379_();
            return true;
         } else {
            if (keyCode == 341) {
               this.skip = true;
            }

            return super.m_7933_(keyCode, scanCode, modifiers);
         }
      }
   }

   public boolean m_7920_(int p_94715_, int p_94716_, int p_94717_) {
      this.skip = false;
      return super.m_7920_(p_94715_, p_94716_, p_94717_);
   }

   public int getX(int x) {
      return (int)((double)this.f_96543_ * (double)1.0F * ((double)x * (double)1.0F / (double)1920.0F));
   }

   public int getX(int wx, boolean isCoordinate) {
      double screenWidth = (double)this.f_96543_;
      double screenHeight = (double)this.f_96544_;
      double aspectRatio = screenWidth / screenHeight;
      double targetRatio = 1.7777777777777777;
      double offset = (double)0.0F;
      double scaleFactor;
      if (aspectRatio <= targetRatio) {
         scaleFactor = screenWidth / (double)1920.0F;
      } else {
         scaleFactor = screenHeight / (double)1080.0F;
         if (isCoordinate) {
            double effectiveWidth = screenHeight * 1.7777777777777777;
            offset = (screenWidth - effectiveWidth) / (double)2.0F;
         }
      }

      double result = (double)wx * scaleFactor + (isCoordinate ? offset : (double)0.0F);
      return (int)Math.round(result);
   }

   public int getY(int y) {
      return (int)((double)this.f_96544_ * (double)1.0F * ((double)y * (double)1.0F / (double)1080.0F));
   }

   public int getY(int wy, boolean isCoordinate) {
      double screenWidth = (double)this.f_96543_;
      double screenHeight = (double)this.f_96544_;
      double aspectRatio = screenWidth / screenHeight;
      double targetRatio = 1.7777777777777777;
      double offset = (double)0.0F;
      double scaleFactor;
      if (aspectRatio <= targetRatio) {
         scaleFactor = screenWidth / (double)1920.0F;
         if (isCoordinate) {
            double effectiveHeight = screenWidth * (double)0.5625F;
            offset = (screenHeight - effectiveHeight) / (double)2.0F;
         }
      } else {
         scaleFactor = screenHeight / (double)1080.0F;
         offset = (double)0.0F;
      }

      double scaledValue = (double)wy * scaleFactor;
      return (int)(scaledValue + (isCoordinate ? offset : (double)0.0F));
   }

   public void m_7379_() {
      if (this.cg && this.cgGalleryScreen != null) {
         Minecraft.m_91087_().m_91152_(this.cgGalleryScreen);
         this.stopSound();
         super.m_7379_();
      }

      if (OpenUIEvent.circle != -1) {
         Minecraft.m_91087_().f_91066_.m_231928_().m_231514_(OpenUIEvent.circle);
         Minecraft.m_91087_().f_91066_.m_92169_();
         Minecraft.m_91087_().m_5741_();
         OpenUIEvent.circle = -1;
      }

      this.stopSound();
      super.m_7379_();
   }

   private void playSound(SoundEvent soundEvent, float volume, float pitch) {
      if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
         this.f_96541_.m_91106_().m_120367_(SimpleSoundInstance.m_119755_(soundEvent, pitch, volume));
      }

   }

   private void playVoiceSound(SoundEvent soundEvent, float volume, float pitch) {
      if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
         if (this.old_soundEvent != null) {
            this.f_96541_.m_91106_().m_120399_(this.old_soundEvent);
         }

         this.old_soundEvent = SimpleSoundInstance.m_119755_(soundEvent, pitch, volume);
         this.f_96541_.m_91106_().m_120367_(this.old_soundEvent);
      } else {
         System.out.println("fuck");
      }

   }

   private void stopSound() {
      if (this.f_96541_ != null && this.f_96541_.f_91074_ != null) {
         this.f_96541_.m_91106_().m_120405_();
      } else {
         System.out.println("fuck");
      }

   }
}
