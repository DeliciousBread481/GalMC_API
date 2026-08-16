package net.caixukun.galmc.ui.character;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.caixukun.galmc.Galmc_api;
import net.caixukun.galmc.event.ExecuteEvent;
import net.caixukun.your_wife.execute.ExecuteMethods;
import org.apache.commons.lang3.tuple.Pair;

public class End_Execute {
   public String type;
   public List<String> data = new ArrayList();
   private ExecuteMethods executeMethods = new ExecuteMethods();

   public End_Execute(JsonObject jsonObject) {
      try {
         this.type = jsonObject.get("type").getAsString();

         for(JsonElement product : jsonObject.getAsJsonArray("data")) {
            this.data.add(product.getAsString());
         }
      } catch (NullPointerException var4) {
         this.type = "null";
      }

   }

   public boolean execute(final UUID player) {
      if (Objects.equals(this.type, "null")) {
         return true;
      } else if (Objects.equals(this.type, "java")) {
         this.executeMethods.execute(player, (String)this.data.get(0));
         return true;
      } else {
         if (Objects.equals(this.type, "command")) {
            try {
               for(final String s : this.data) {
                  ExecuteEvent.commands.add(new Pair<UUID, String>() {
                     public UUID getLeft() {
                        return player;
                     }

                     public String getRight() {
                        return s;
                     }

                     public String setValue(String value) {
                        return "";
                     }
                  });
               }
            } catch (RuntimeException e) {
               Galmc_api.LOGGER.error("执行失败");
               Galmc_api.LOGGER.error(e.getMessage());
               return false;
            }
         }

         return false;
      }
   }
}
