package net.caixukun.galmc.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.List;
import net.caixukun.galmc.event.OpenUIEvent;
import net.caixukun.galmc.resource.GalResourceManger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class CommandInit {
   private static final SuggestionProvider<CommandSourceStack> RESOURCE_PATH_SUGGESTIONS = (context, builder) -> {
      List<String> data = GalResourceManger.getText();
      data.addAll(GalResourceManger.getCCg());
      return SharedSuggestionProvider.suggest(data, builder);
   };

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)Commands.literal("play_galgame").then(Commands.argument("path", StringArgumentType.greedyString()).suggests(RESOURCE_PATH_SUGGESTIONS).executes((context) -> executeResourceCommand(context))));
   }

   private static int executeResourceCommand(CommandContext<CommandSourceStack> context) {
      String resourcePath = StringArgumentType.getString(context, "path");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Entity var4 = source.getEntity();
      if (var4 instanceof ServerPlayer player) {
         String newPath = resourcePath.replace("galmc_api:", "").replace("\"", "");
         OpenUIEvent.openUI(newPath, player.getUUID());
         return 1;
      } else {
         MinecraftServer server = source.getServer();
         List<ServerPlayer> players = server.getPlayerList().getPlayers();
         if (!players.isEmpty() && players.size() == 1) {
            ServerPlayer singlePlayer = (ServerPlayer)players.get(0);
            String newPath = resourcePath.replace("galmc_api:", "").replace("\"", "");
            OpenUIEvent.openUI(newPath, singlePlayer.getUUID());
            return 1;
         } else {
            source.sendFailure(Component.literal("不能为多个玩家播放galgame"));
            return 0;
         }
      }
   }
}
