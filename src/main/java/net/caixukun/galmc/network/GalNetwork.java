package net.caixukun.galmc.network;

import net.caixukun.galmc.Galmc_api;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GalNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel CHANNEL;
    private static int id = 0;

    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                ResourceLocation.fromNamespaceAndPath(Galmc_api.MODID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );
        CHANNEL.registerMessage(id++, OpenGalScreenPacket.class,
                OpenGalScreenPacket::encode,
                OpenGalScreenPacket::decode,
                OpenGalScreenPacket::handle);
    }
}