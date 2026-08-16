package net.caixukun.galmc.ui.character;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Objects;
import net.caixukun.galmc.ui.GalScreen;
import net.caixukun.your_wife.render.text_render.TextMethods;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class TextEntry {
   public static final Logger LOGGER = LogUtils.getLogger();
   public int id;
   public String text;
   public ResourceLocation character;
   public ResourceLocation background;
   public String sound;
   public int x;
   public int y;
   public int ix;
   public int iy;
   public float circle;
   public JsonObject render_execute;
   private int startX;
   private int startY;
   private int currentTime = 0;
   public TextMethods textMethods = new TextMethods();
   int tick = 0;
   String rs = "";
   int p = 0;
   int p1 = 30;

   public TextEntry(String t, String c, String b, String s, int x, int y, int ix, int iy, JsonObject render_execute) {
      this.text = t;
      if (!Objects.equals(b, "null")) {
         this.background = ResourceLocation.fromNamespaceAndPath("galmc_api", b);
      } else {
         this.background = ResourceLocation.fromNamespaceAndPath("galmc_api", "texture/character/air.png");
      }

      if (!Objects.equals(c, "null")) {
         this.character = ResourceLocation.fromNamespaceAndPath("galmc_api", c);
      } else {
         this.character = ResourceLocation.fromNamespaceAndPath("galmc_api", "texture/character/air.png");
      }

      this.sound = s;
      this.x = x;
      this.y = y;
      this.ix = ix;
      this.iy = iy;
      this.render_execute = render_execute;
   }

   public void render(GuiGraphics guiGraphics, GalScreen galScreen) {
      try {
         if (Objects.equals(this.render_execute.get("type").getAsString(), "java")) {
            this.textMethods.execute(guiGraphics, this.render_execute.get("data").getAsString(), this, galScreen);
         } else if (Objects.equals(this.render_execute.get("type").getAsString(), "normal")) {
            int screenWidth = galScreen.width;
            int screenHeight = galScreen.height;
            galScreen.renderContain(guiGraphics, screenWidth, screenHeight, this.background);
            this.render_character(guiGraphics, galScreen);
            if (galScreen.rendtext) {
               this.render_text(guiGraphics, galScreen);
            }
         } else if (Objects.equals(this.render_execute.get("type").getAsString(), "pingyi")) {
            int screenWidth = galScreen.width;
            int screenHeight = galScreen.height;
            galScreen.renderContain(guiGraphics, screenWidth, screenHeight, this.background);
            this.render_character(guiGraphics, galScreen);
            this.move(this.render_execute.get("data").getAsJsonObject().get("new_x").getAsInt(), this.render_execute.get("data").getAsJsonObject().get("new_y").getAsInt(), this.render_execute.get("data").getAsJsonObject().get("time").getAsInt());
            galScreen.renderUI(guiGraphics);
            if (galScreen.rendtext) {
               this.render_text(guiGraphics, galScreen);
            }
         } else if (Objects.equals(this.render_execute.get("type").getAsString(), "two_people")) {
            int screenWidth = galScreen.width;
            int screenHeight = galScreen.height;
            galScreen.renderContain(guiGraphics, screenWidth, screenHeight, this.background);
            this.render_character(guiGraphics, galScreen);
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("galmc_api", this.render_execute.get("data").getAsJsonObject().get("image").getAsString());
            int n_x = this.render_execute.get("data").getAsJsonObject().get("x").getAsInt();
            int n_y = this.render_execute.get("data").getAsJsonObject().get("y").getAsInt();
            int n_ix = this.render_execute.get("data").getAsJsonObject().get("image_x").getAsInt();
            int n_iy = this.render_execute.get("data").getAsJsonObject().get("image_y").getAsInt();
            guiGraphics.blit(resourceLocation, galScreen.getX(n_x, true), galScreen.getY(n_y, true), 0.0F, 0.0F, galScreen.getX(n_ix, false), galScreen.getY(n_iy, false), galScreen.getX(n_ix, false), galScreen.getY(n_iy, false));
            galScreen.renderUI(guiGraphics);
            if (galScreen.rendtext) {
               this.render_text(guiGraphics, galScreen);
            }
         }
      } catch (NullPointerException e) {
         LOGGER.error(this.text + ":特殊渲染失败或者资源文件格式有误", e);
         int screenWidth = galScreen.width;
         int screenHeight = galScreen.height;
         galScreen.renderContain(guiGraphics, screenWidth, screenHeight, this.background);
         this.render_character(guiGraphics, galScreen);
         if (galScreen.rendtext) {
            this.render_text(guiGraphics, galScreen);
         }
      }

   }

   private void render_text(GuiGraphics guiGraphics, GalScreen galScreen) {
      if (!Objects.equals(this.text, "null")) {
         galScreen.renderUI(guiGraphics);
         String[] parts = this.text.replace("：", ":").split(":", 2);
         String a = "";
         String b = "";

         try {
            b = parts[1];
            if (this.p == galScreen.text_speed) {
               if (this.rs.length() < parts[1].length()) {
                  this.rs = this.rs + parts[1].charAt(this.rs.length());
               }

               this.p = 0;
            }

            ++this.p;
            a = parts[0];
         } catch (ArrayIndexOutOfBoundsException var7) {
            this.rs = this.text;
            a = "";
         }

         if (galScreen.fonta != null) {
            guiGraphics.drawString(galScreen.fonta, Component.literal(this.rs), galScreen.getX(120, true), galScreen.getY(900, true), 16777215, true);
            if (Objects.equals(this.rs, b)) {
               --this.p1;
               if (galScreen.auto && this.p1 <= 0) {
                  galScreen.next();
               }
            }
         } else {
            LOGGER.error("字体加载错误，请重试");
         }

         if (Objects.equals(a, "旁白")) {
            a = "";
         }

         guiGraphics.drawString(galScreen.fonta, Component.literal(a), galScreen.getX(75, true), galScreen.getY(750, true), 16777215, true);
      }

   }

   private void render_character(GuiGraphics guiGraphics, GalScreen galScreen) {
      guiGraphics.blit(this.character, galScreen.getX(this.x, true), galScreen.getY(this.y, true), 0.0F, 0.0F, galScreen.getX(this.ix, false), galScreen.getY(this.iy, false), galScreen.getX(this.ix, false), galScreen.getY(this.iy, false));
   }

   public boolean is_sound() {
      return !Objects.equals(this.sound, "null");
   }

   private void move(int nx, int ny, int time) {
      if ((nx != this.x || ny != this.y) && this.currentTime <= time) {
         this.startX = this.x;
         this.startY = this.y;
      }

      if (this.currentTime < time) {
         ++this.currentTime;
         float progress = (float)this.currentTime / (float)time;
         float smoothProgress = 1.0F - (float)Math.pow((double)(1.0F - progress), (double)2.0F);
         this.x = (int)((float)this.startX + (float)(nx - this.startX) * smoothProgress);
         this.y = (int)((float)this.startY + (float)(ny - this.startY) * smoothProgress);
      }

   }
}
