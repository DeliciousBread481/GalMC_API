package net.caixukun.galmc.ui.character;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NextText {
    public String type;
    public List<String> refer = new ArrayList<>();
    public List<String> text = new ArrayList<>();
    public NextText(JsonObject t){
        try{
            this.type = t.get("type").getAsString();
            if(Objects.equals(type, "choice")){
                for (JsonElement product :t.getAsJsonArray("data")){
                    JsonObject item = product.getAsJsonObject();
                    refer.add(item.get("refer").getAsString());
                    text.add(item.get("text").getAsString());
                }
            }else if(Objects.equals(type, "end")){
                //在gui中直接判断结束
            }else {
                refer.add(
                        t.getAsJsonArray("data").get(0).getAsJsonObject().get("refer").getAsString()
                );
            }
        }catch (NullPointerException nullPointerException){
            this.type="end";
        }
    }
}
