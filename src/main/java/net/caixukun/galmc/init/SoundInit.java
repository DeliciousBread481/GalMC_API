package net.caixukun.galmc.init;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.caixukun.galmc.Galmc_api;
import net.caixukun.galmc.resource.GalResourceManger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Galmc_api.MODID);
    public static Map<String, RegistryObject<SoundEvent>> REG_SOUNDS = new HashMap<>();
    private static final String jsonStr = "";

    public static void initSounds() {

        JsonObject root = GalResourceManger.get_sound();
        if (root == null){
            Logger LOGGER = LogUtils.getLogger();
            LOGGER.error("加载音频失败，启动就崩溃");
            return;
        }
        for (String key : root.keySet()) {
            REG_SOUNDS.put(key,SOUND_EVENTS.register(key,()->SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Galmc_api.MODID,key))));
        }

    }


}
