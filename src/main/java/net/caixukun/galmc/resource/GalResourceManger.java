package net.caixukun.galmc.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;

public class GalResourceManger {
   public static List<String> text = new ArrayList();
   public static List<String> cg = new ArrayList();
   public static Path zipFile = null;
   public static String cg_background = null;
   public static final Logger LOGGER = LogUtils.getLogger();
   public static String TARGET_PACK_ID = null;

   public static void handleResourcePack() {
      try {
         Path srcDir = FMLPaths.CONFIGDIR.get().resolve("galmc_api").resolve("resourcepacks");
         if (!Files.exists(srcDir, new LinkOption[0])) {
            Files.createDirectories(srcDir);
         }

         Stream<Path> stream = Files.list(srcDir);

         label65: {
            try {
               Optional<Path> firstZip = stream.filter((p) -> p.toString().endsWith(".zip")).findFirst();
               if (firstZip.isEmpty()) {
                  break label65;
               }

               Path zipPath = (Path)firstZip.get();
               Path targetDir = FMLPaths.GAMEDIR.get().resolve("resourcepacks");
               if (!Files.exists(targetDir, new LinkOption[0])) {
                  Files.createDirectories(targetDir);
               }

               Path targetPath = targetDir.resolve(zipPath.getFileName());
               TARGET_PACK_ID = Paths.get(targetPath.toString()).getFileName().toString();
               Files.copy(zipPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
               zipFile = zipPath;
            } catch (Throwable var7) {
               if (stream != null) {
                  try {
                     stream.close();
                  } catch (Throwable var6) {
                     var7.addSuppressed(var6);
                  }
               }

               throw var7;
            }

            if (stream != null) {
               stream.close();
            }

            return;
         }

         if (stream != null) {
            stream.close();
         }

      } catch (Exception e) {
         LogManager.getLogger().error("Failed to handle resource pack", e);
      }
   }

   public static JsonObject get_sound() {
      try {
         try {
            ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile());

            label90: {
               JsonObject var8;
               try {
                  Enumeration<? extends ZipEntry> entries = zipFile.entries();

                  while(true) {
                     if (!entries.hasMoreElements()) {
                        break label90;
                     }

                     ZipEntry entry = (ZipEntry)entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith("assets/") && name.endsWith("/sounds.json")) {
                        String[] parts = name.split("/");
                        if (parts.length >= 3) {
                           String namespace = parts[1];

                           try {
                              InputStream is = zipFile.getInputStream(entry);

                              try {
                                 InputStreamReader reader = new InputStreamReader(is);

                                 try {
                                    var8 = JsonParser.parseReader(reader).getAsJsonObject();
                                 } catch (Throwable var13) {
                                    try {
                                       reader.close();
                                    } catch (Throwable var12) {
                                       var13.addSuppressed(var12);
                                    }

                                    throw var13;
                                 }

                                 reader.close();
                              } catch (Throwable var14) {
                                 if (is != null) {
                                    try {
                                       is.close();
                                    } catch (Throwable var11) {
                                       var14.addSuppressed(var11);
                                    }
                                 }

                                 throw var14;
                              }

                              if (is != null) {
                                 is.close();
                              }
                              break;
                           } catch (IOException e) {
                              throw new RuntimeException(e);
                           }
                        }
                     }
                  }
               } catch (Throwable var16) {
                  try {
                     zipFile.close();
                  } catch (Throwable var10) {
                     var16.addSuppressed(var10);
                  }

                  throw var16;
               }

               zipFile.close();
               return var8;
            }

            zipFile.close();
         } catch (IOException e) {
            LogManager.getLogger().error("Failed to handle resource pack", e);
         }
      } catch (NullPointerException e) {
         LogManager.getLogger().warn("没有资源包加载", e);
      }

      return null;
   }

   public static List<String> getText() {
      if (text.isEmpty()) {
         try {
            try {
               ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile());

               try {
                  Enumeration<? extends ZipEntry> entries = zipFile.entries();

                  while(entries.hasMoreElements()) {
                     ZipEntry entry = (ZipEntry)entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith("assets/galmc_api/data/text/") && name.endsWith(".json")) {
                        name = name.replace("assets/galmc_api/", "");
                        text.add(name);
                     }
                  }
               } catch (Throwable var5) {
                  try {
                     zipFile.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }

                  throw var5;
               }

               zipFile.close();
            } catch (IOException e) {
               LogManager.getLogger().error("Failed to handle resource pack", e);
            }
         } catch (NullPointerException e) {
            LogManager.getLogger().warn("没有资源包加载", e);
         }
      }

      return text;
   }

   public static List<String> getCCg() {
      if (cg.isEmpty()) {
         try {
            try {
               ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile());

               try {
                  Enumeration<? extends ZipEntry> entries = zipFile.entries();

                  while(entries.hasMoreElements()) {
                     ZipEntry entry = (ZipEntry)entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith("assets/galmc_api/data/cg/") && name.endsWith(".json")) {
                        name = name.replace("assets/galmc_api/", "");
                        cg.add(name);
                     }
                  }
               } catch (Throwable var5) {
                  try {
                     zipFile.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }

                  throw var5;
               }

               zipFile.close();
            } catch (Exception e) {
               LogManager.getLogger().error("Failed to handle resource pack", e);
            }
         } catch (NullPointerException e) {
            LogManager.getLogger().warn("没有资源包加载", e);
         }
      }

      return cg;
   }

   public static String getCgUI() {
      if (cg_background == null) {
         try {
            try {
               ZipFile zipFile = new ZipFile(GalResourceManger.zipFile.toFile());

               try {
                  Enumeration<? extends ZipEntry> entries = zipFile.entries();

                  while(entries.hasMoreElements()) {
                     ZipEntry entry = (ZipEntry)entries.nextElement();
                     String name = entry.getName();
                     if (name.startsWith("assets/galmc_api/texture/gui/") && name.endsWith("cg_background.png")) {
                        name = name.replace("assets/galmc_api/", "");
                        cg_background = name;
                     }
                  }
               } catch (Throwable var5) {
                  try {
                     zipFile.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }

                  throw var5;
               }

               zipFile.close();
            } catch (Exception e) {
               LogManager.getLogger().error("Failed to handle resource pack", e);
            }
         } catch (NullPointerException e) {
            LogManager.getLogger().warn("没有资源包加载", e);
         }
      }

      return cg_background;
   }
}
