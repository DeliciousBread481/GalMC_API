package net.caixukun.galmc.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.caixukun.galmc.network.GalNetwork;
import net.caixukun.galmc.network.OpenGalScreenPacket;
import net.caixukun.galmc.resource.GalResourceManger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public class CommandInit {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 在这里注册指令
        dispatcher.register(
                Commands.literal("play_galgame").then(Commands.argument("path", StringArgumentType.greedyString())
                        .suggests(RESOURCE_PATH_SUGGESTIONS)
                        .executes(context -> executeResourceCommand(context))
                )
        );
    }
    private static final SuggestionProvider<CommandSourceStack> RESOURCE_PATH_SUGGESTIONS =
            (context, builder) -> {
                List<String> data = GalResourceManger.getText();
                data.addAll(GalResourceManger.getCCg());
                return SharedSuggestionProvider.suggest(data, builder);
            };
    private static int executeResourceCommand(CommandContext<CommandSourceStack> context){
        String resourcePath = StringArgumentType.getString(context, "path");
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            String newPath = resourcePath.replace("galmc_api:", "").replace("\"", "");
            GalNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new OpenGalScreenPacket(newPath, false, player.getUUID()));
            return 1;
        } else {
            source.sendFailure(
                    Component.literal("§c只有玩家可以使用此指令！")
            );
            return 0;
        }

    }

}
