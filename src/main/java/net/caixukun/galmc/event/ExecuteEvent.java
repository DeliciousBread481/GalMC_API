package net.caixukun.galmc.event;

import java.util.ArrayList;
import java.util.UUID;
import net.caixukun.galmc.Galmc_api;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.tuple.Pair;

public class ExecuteEvent {
   public static ArrayList<Pair<UUID, String>> commands = new ArrayList();

   @SubscribeEvent
   public void onPlayerTick(TickEvent.PlayerTickEvent event) {
      if (event.side != LogicalSide.CLIENT) {
         if (event.phase == Phase.END) {
            for(Pair<UUID, String> s : commands) {
               if (s.getLeft() == event.player.getUUID()) {
                  CommandSourceStack source = event.player.createCommandSourceStack().withPosition(event.player.position()).withRotation(event.player.getRotationVector());

                  try {
                     event.player.getServer().getCommands().performPrefixedCommand(source, (String)s.getRight());
                  } catch (NullPointerException e) {
                     Galmc_api.LOGGER.error(e.getMessage());
                  }
               }
            }

            commands.clear();
         }

      }
   }
}
