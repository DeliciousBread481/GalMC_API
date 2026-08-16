package net.caixukun.galmc.ui.character;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NextText {
   public String type;
   public List<String> refer = new ArrayList();
   public List<String> text = new ArrayList();

   public NextText(JsonObject t) {
      try {
         this.type = t.get("type").getAsString();
         if (Objects.equals(this.type, "choice")) {
            for(JsonElement product : t.getAsJsonArray("data")) {
               JsonObject item = product.getAsJsonObject();
               this.refer.add(item.get("refer").getAsString());
               this.text.add(item.get("text").getAsString());
            }
         } else if (!Objects.equals(this.type, "end")) {
            this.refer.add(t.getAsJsonArray("data").get(0).getAsJsonObject().get("refer").getAsString());
         }
      } catch (NullPointerException var5) {
         this.type = "end";
      }

   }
}
