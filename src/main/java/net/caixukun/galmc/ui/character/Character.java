package net.caixukun.galmc.ui.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.caixukun.galmc.resource.GalResourceManger;
import net.caixukun.galmc.ui.GalScreen;
import net.caixukun.your_wife.render.character_render.CharacterMethods;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;

public class Character {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final List<TextEntry> TEXTS = new ArrayList();
   public boolean disabled = false;
   public String id;
   private static final Gson GSON = (new GsonBuilder()).create();
   public int pointer = 0;
   public int max = 0;
   public String type;
   public String render_execute = null;
   public NextText next = null;
   public JsonObject music = null;
   public List<ResourceLocation> resources = new ArrayList();
   public End_Execute execute;
   public CharacterMethods characterMethods = new CharacterMethods();

   public Character(String resource) {
      System.out.println(resource);
      if (this.no_fuck(resource)) {
         try {
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath("galmc_api", resource);
            this.id = resource;
            this.readJson(Minecraft.getInstance().getResourceManager(), resourceLocation);
         } catch (ResourceLocationException e) {
            LOGGER.error("资源路径不对且不符合格式", e);
            this.disabled = true;
         }
      } else {
         LOGGER.error("资源路径不对且不符合格式{}", resource);
         this.disabled = true;
      }

   }

   public boolean init() {
      if (this.TEXTS.isEmpty()) {
         return false;
      } else {
         return !this.disabled;
      }
   }

   private boolean no_fuck(String s) {
      for(String a : GalResourceManger.getText()) {
         if (Objects.equals(a, s)) {
            return true;
         }
      }

      for(String a : GalResourceManger.getCCg()) {
         if (Objects.equals(a, s)) {
            return true;
         }
      }

      return false;
   }

   private void readJson(ResourceManager resourceManager, ResourceLocation location) {
      resourceManager.getResource(location).ifPresent((resource) -> {
         try {
            InputStream stream = resource.open();

            try {
               JsonElement json = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
               JsonObject jsonObject = (JsonObject)GSON.fromJson(json, JsonObject.class);
               this.type = jsonObject.get("type").getAsString();
               this.render_execute = jsonObject.get("render_execute").getAsString();
               this.music = jsonObject.getAsJsonObject("start_music");
               this.next = new NextText(jsonObject.getAsJsonObject("next"));
               this.execute = new End_Execute(jsonObject.getAsJsonObject("execute"));
               this.max = jsonObject.getAsJsonArray("data").size();
               if (!this.is_cg()) {
                  for(JsonElement product : jsonObject.getAsJsonArray("data")) {
                     JsonObject item = product.getAsJsonObject();
                     this.TEXTS.add(new TextEntry(item.get("text").getAsString(), item.getAsJsonObject("character").get("image").getAsString(), item.get("background").getAsString(), item.get("sound").getAsString(), item.getAsJsonObject("character").get("x").getAsInt(), item.getAsJsonObject("character").get("y").getAsInt(), item.getAsJsonObject("character").get("image_x").getAsInt(), item.getAsJsonObject("character").get("image_y").getAsInt(), item.getAsJsonObject("render_execute")));
                  }
               } else {
                  for(JsonElement product : jsonObject.getAsJsonArray("data")) {
                     JsonObject item = product.getAsJsonObject();
                     this.TEXTS.add(new TextEntry((String)null, "null", item.get("background").getAsString(), item.get("sound").getAsString(), 0, 0, item.getAsJsonObject("character").get("image_x").getAsInt(), item.getAsJsonObject("character").get("image_y").getAsInt(), item.getAsJsonObject("render_execute")));
                  }
               }

               for(JsonElement product : jsonObject.getAsJsonArray("resources")) {
                  this.resources.add(ResourceLocation.fromNamespaceAndPath("galmc_api", product.getAsString()));
               }
            } catch (Throwable var9) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable var8) {
                     var9.addSuppressed(var8);
                  }
               }

               throw var9;
            }

            if (stream != null) {
               stream.close();
            }
         } catch (IOException e) {
            this.disabled = true;
            LOGGER.error("初始化错误", e);
         } catch (NullPointerException e) {
            this.disabled = true;
            LOGGER.error("初始化错误", e);
         }

      });
   }

   public void render(GuiGraphics guiGraphics, GalScreen galScreen) {
      if (!this.disabled && Objects.equals(this.render_execute, "null")) {
         if (this.is_cg()) {
            int screenWidth = galScreen.width;
            int screenHeight = galScreen.height;
            galScreen.renderContain(guiGraphics, screenWidth, screenHeight, ((TextEntry)this.TEXTS.get(this.pointer)).background);
         } else {
            ((TextEntry)this.TEXTS.get(this.pointer)).render(guiGraphics, galScreen);
         }
      }

   }

   public boolean is_cg() {
      return Objects.equals(this.type, "cg");
   }

   public NextText next() {
      if (this.render_execute == null) {
         this.render_execute = "null";
      }

      if (Objects.equals(this.render_execute, "null")) {
         if (this.pointer == this.max - 1) {
            return this.next;
         } else {
            ++this.pointer;
            return null;
         }
      } else {
         return !this.characterMethods.next() ? this.next : null;
      }
   }

   public boolean before() {
      if (this.pointer == 0) {
         return false;
      } else {
         --this.pointer;
         return true;
      }
   }

   public String getSound() {
      return ((TextEntry)this.TEXTS.get(this.pointer)).sound;
   }

   public boolean is_sound() {
      return ((TextEntry)this.TEXTS.get(this.pointer)).is_sound();
   }

   public TextEntry get() {
      return (TextEntry)this.TEXTS.get(this.pointer);
   }
}
