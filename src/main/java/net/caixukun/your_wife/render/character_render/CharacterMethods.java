package net.caixukun.your_wife.render.character_render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.caixukun.galmc.ui.GalScreen;
import net.caixukun.galmc.ui.character.TextEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class CharacterMethods {
    public int pointer = 0;
    public int max = 0;
    int var1,var2,var3,var4 = 0;
    public void execute(GuiGraphics guiGraphics, String render_id, List<TextEntry> data, GalScreen galScreen){
        switch (render_id){
            case "b_cg":
                e1(guiGraphics,data,galScreen);
        }
    }

    private void e1(GuiGraphics guiGraphics, List<TextEntry> data,GalScreen galScreen){

    }


    public boolean next(){
        if (this.pointer == max){
            this.pointer = 0;
            this.max = 0;
            this.var1 = 0;
            this.var2 = 0;
            this.var3 = 0;
            this.var4 = 0;

            return false;
        }else {
            this.pointer++;
            return true;
        }
    }

    static class Tools{
        public static void renderContain(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ResourceLocation BACKGROUND_TEXTURE) {
            // 原理：保持图片比例，缩放到完全显示在屏幕内

            // 先绘制黑色背景
            guiGraphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);

            // 计算缩放比例
            float scaleX = (float) screenWidth / 1920;
            float scaleY = (float) screenHeight / 1080;
            float scale = Math.min(scaleX, scaleY); // 取较小值保证完全显示

            int renderWidth = (int) (1920 * scale);
            int renderHeight = (int) (1080 * scale);
            int renderX = (screenWidth - renderWidth) / 2;
            int renderY = (screenHeight - renderHeight) / 2;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);

            guiGraphics.blit(
                    BACKGROUND_TEXTURE,
                    renderX, renderY,
                    renderWidth, renderHeight,
                    0, 0,
                    1920, 1080,
                    1920, 1080
            );
        }
        private static int getX(int x,int width){
            return (int) (width*1.0*(x*1.0/1920.0));
        }
        private static int getY(int y,int height){
            return (int) (height*1.0*(y*1.0/1080.0));
        }
    }
}
