package net.caixukun.galmc.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.caixukun.galmc.Galmc_api;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class GalResourceManger {
    public static List<String> text = new ArrayList<>();
    public static List<String> cg = new ArrayList<>();
    public static Path zipFile = null;
    public static String cg_background = null;
    public static void handleResourcePack() {
        try {
            // 源目录：.minecraft/config/yourmod/resourcepacks/
            Path srcDir = FMLPaths.CONFIGDIR.get().resolve(Galmc_api.MODID).resolve("resourcepacks");
            if (!Files.exists(srcDir)) return ;

            // 找到第一个zip文件
            try (Stream<Path> stream = Files.list(srcDir)) {
                Optional<Path> firstZip = stream.filter(p -> p.toString().endsWith(".zip")).findFirst();
                if (firstZip.isEmpty()) return ;

                Path zipPath = firstZip.get();
                // 目标目录：.minecraft/resourcepacks/
                Path targetDir = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
                if (!Files.exists(targetDir)) Files.createDirectories(targetDir);
                Path targetPath = targetDir.resolve(zipPath.getFileName());

                // 复制文件（覆盖已存在）
                Files.copy(zipPath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                GalResourceManger.zipFile = zipPath;

            }
        } catch (Exception e) {
            // 使用你的日志系统记录错误
            LogManager.getLogger().error("Failed to handle resource pack", e);

        }
    }

    public static JsonObject get_sound(){
        try (ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                // 匹配 assets/<namespace>/sounds.json
                if (name.startsWith("assets/") && name.endsWith("/sounds.json")) {
                    String[] parts = name.split("/");
                    if (parts.length >= 3) {
                        String namespace = parts[1]; // 获取命名空间
                        try (InputStream is = zipFile.getInputStream(entry);
                             InputStreamReader reader = new InputStreamReader(is)) {
                            return JsonParser.parseReader(reader).getAsJsonObject();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        } catch (ZipException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static List<String> getText(){
        if(GalResourceManger.text.isEmpty()){
            try (ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // 匹配 assets/<namespace>/sounds.json
                    if (name.startsWith("assets/galmc_api/data/text/") && name.endsWith(".json")) {
                        name = name.replace("assets/galmc_api/","");
                        GalResourceManger.text.add(name);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return GalResourceManger.text;
    }
    public static List<String> getCCg(){
        if(GalResourceManger.cg.isEmpty()){
            try (ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // 匹配 assets/<namespace>/sounds.json
                    if (name.startsWith("assets/galmc_api/data/cg/") && name.endsWith(".json")) {
                        name = name.replace("assets/galmc_api/","");
                        GalResourceManger.cg.add(name);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return GalResourceManger.cg;
    }
    public static String getCgUI(){
        if(GalResourceManger.cg_background==null){
            try (ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile())) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // 匹配 assets/<namespace>/sounds.json
                    if (name.startsWith("assets/galmc_api/texture/gui/") && name.endsWith("cg_background.png")) {
                        name = name.replace("assets/galmc_api/","");
                        GalResourceManger.cg_background = name;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return GalResourceManger.cg_background;
    }
}
