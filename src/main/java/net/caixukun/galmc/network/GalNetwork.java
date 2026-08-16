package net.caixukun.galmc.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GalNetwork {
   private static final String PROTOCOL_VERSION = "1";
   public static SimpleChannel CHANNEL;

   public static void register() {
      CHANNEL = NetworkRegistry.newSimpleChannel(
         ResourceLocation.fromNamespaceAndPath("galmc_api", "main"),
         () -> PROTOCOL_VERSION,
         PROTOCOL_VERSION::equals,
         PROTOCOL_VERSION::equals
      );
      CHANNEL.registerMessage(
         0,
         OpenGalScreenPacket.class,
         OpenGalScreenPacket::encode,
         OpenGalScreenPacket::decode,
         OpenGalScreenPacket::handle
      );
   }
}