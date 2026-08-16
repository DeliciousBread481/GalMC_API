package net.caixukun.galmc.init;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import net.caixukun.galmc.resource.GalResourceManger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

public class SoundInit {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS;
   public static Map<String, RegistryObject<SoundEvent>> REG_SOUNDS;
   private static final String jsonStr = "";

   public static void initSounds() {
      JsonObject root = GalResourceManger.get_sound();
      if (root == null) {
         Logger LOGGER = LogUtils.getLogger();
         LOGGER.error("加载音频失败，请检查资源包");
      } else {
         for(String key : root.keySet()) {
            REG_SOUNDS.put(key, SOUND_EVENTS.register(key, () -> SoundEvent.m_262824_(ResourceLocation.fromNamespaceAndPath("galmc_api", key))));
         }

      }
   }

   static {
      SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "galmc_api");
      REG_SOUNDS = new HashMap();
   }
}
