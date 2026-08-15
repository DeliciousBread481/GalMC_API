package net.caixukun.galmc.network;

import net.caixukun.galmc.event.OpenUIEvent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class OpenGalScreenPacket {
    private final String path;
    private final boolean cg;
    private final UUID uuid;

    public OpenGalScreenPacket(String path, boolean cg, UUID uuid) {
        this.path = path;
        this.cg = cg;
        this.uuid = uuid;
    }

    public static void encode(OpenGalScreenPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.cg);
        buf.writeBoolean(msg.path != null);
        if (msg.path != null) {
            buf.writeUtf(msg.path);
        }
        buf.writeUUID(msg.uuid);
    }

    public static OpenGalScreenPacket decode(FriendlyByteBuf buf) {
        boolean cg = buf.readBoolean();
        String path = buf.readBoolean() ? buf.readUtf() : null;
        UUID uuid = buf.readUUID();
        return new OpenGalScreenPacket(path, cg, uuid);
    }

    public static void handle(OpenGalScreenPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (msg.cg) {
                        OpenUIEvent.openCG(msg.uuid);
                    } else {
                        OpenUIEvent.openUI(msg.path, msg.uuid);
                    }
                })
        );
        context.setPacketHandled(true);
    }
}