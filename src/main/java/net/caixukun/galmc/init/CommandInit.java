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
      return SharedSuggestionProvider.m_82970_(data, builder);
   };

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_("play_galgame").then(Commands.m_82129_("path", StringArgumentType.greedyString()).suggests(RESOURCE_PATH_SUGGESTIONS).executes((context) -> executeResourceCommand(context))));
   }

   private static int executeResourceCommand(CommandContext<CommandSourceStack> context) {
      String resourcePath = StringArgumentType.getString(context, "path");
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      Entity var4 = source.m_81373_();
      if (var4 instanceof ServerPlayer player) {
         String newPath = resourcePath.replace("galmc_api:", "").replace("\"", "");
         OpenUIEvent.openUI(newPath, player.m_20148_());
         return 1;
      } else {
         MinecraftServer server = source.m_81377_();
         List<ServerPlayer> players = server.m_6846_().m_11314_();
         if (!players.isEmpty() && players.size() == 1) {
            ServerPlayer singlePlayer = (ServerPlayer)players.get(0);
            String newPath = resourcePath.replace("galmc_api:", "").replace("\"", "");
            OpenUIEvent.openUI(newPath, singlePlayer.m_20148_());
            return 1;
         } else {
            source.m_81352_(Component.m_237113_("不能为多个玩家播放galgame"));
            return 0;
         }
      }
   }
}
