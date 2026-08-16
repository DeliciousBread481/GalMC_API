package net.caixukun.galmc.ui;

import net.caixukun.galmc.network.GalNetwork;
import net.caixukun.galmc.network.OpenGalScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class OpenUI extends Item {
   public OpenUI() {
      super((new Item.Properties()).stacksTo(1));
   }

   public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
      if (!p_41432_.isClientSide && p_41433_ instanceof ServerPlayer serverPlayer) {
         GalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
            new OpenGalScreenPacket("", true));
      }

      return super.use(p_41432_, p_41433_, p_41434_);
   }
}
