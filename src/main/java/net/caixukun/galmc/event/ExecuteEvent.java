package net.caixukun.galmc.event;

import net.caixukun.galmc.Galmc_api;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
public class ExecuteEvent {
    public static ArrayList<String> commands = new ArrayList<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 1. 确保只在服务端执行
        if (event.side == LogicalSide.CLIENT) {
            return;
        }

        // 2. 如果你只希望逻辑发生在 tick 的结束阶段（通常是 END）
        //   阶段分为 START 和 END，大部分逻辑放在 END 阶段执行
        if (event.phase == TickEvent.Phase.END) {

            CommandSourceStack source = event.player.createCommandSourceStack()
                    .withPosition(event.player.position())
                    .withRotation(event.player.getRotationVector());
            // 你的代码逻辑
            for (String s :commands){
                try {
                    event.player.getServer().getCommands().performPrefixedCommand(source, s);
                }catch (NullPointerException e){
                    Galmc_api.LOGGER.error(e.getMessage());
                }
            }
            commands.clear();
        }
    }
}
