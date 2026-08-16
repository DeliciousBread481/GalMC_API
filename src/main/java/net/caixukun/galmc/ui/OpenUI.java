package net.caixukun.galmc.ui;

import net.caixukun.galmc.event.OpenUIEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OpenUI extends Item {
   public OpenUI() {
      super((new Item.Properties()).m_41487_(1));
   }

   public @NotNull InteractionResultHolder<ItemStack> m_7203_(@NotNull Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
      OpenUIEvent.openCG(p_41433_.m_20148_());
      return super.m_7203_(p_41432_, p_41433_, p_41434_);
   }
}
