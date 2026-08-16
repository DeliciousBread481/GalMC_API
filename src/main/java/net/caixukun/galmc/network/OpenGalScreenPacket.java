package net.caixukun.galmc.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public class OpenGalScreenPacket {
   private final String path;
   private final boolean cg;

   public OpenGalScreenPacket(String path, boolean cg) {
      this.path = path;
      this.cg = cg;
   }

   public static void encode(OpenGalScreenPacket msg, FriendlyByteBuf buf) {
      buf.writeBoolean(msg.cg);
      buf.writeUtf(msg.path == null ? "" : msg.path);
   }

   public static OpenGalScreenPacket decode(FriendlyByteBuf buf) {
      boolean cg = buf.readBoolean();
      String path = buf.readUtf();
      return new OpenGalScreenPacket(path, cg);
   }

   public static void handle(OpenGalScreenPacket msg, Supplier<NetworkEvent.Context> ctx) {
      ctx.get().enqueueWork(() ->
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandler.open(msg.path, msg.cg))
      );
      ctx.get().setPacketHandled(true);
   }
}