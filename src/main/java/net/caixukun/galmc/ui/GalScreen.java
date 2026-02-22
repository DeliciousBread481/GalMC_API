package net.caixukun.galmc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.caixukun.galmc.Galmc_api;
import net.caixukun.galmc.event.OpenUIEvent;
import net.caixukun.galmc.init.SoundInit;
import net.caixukun.galmc.ui.character.Character;
import net.caixukun.galmc.ui.character.NextText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;

public class GalScreen extends Screen {
    public static final Logger LOGGER = LogUtils.getLogger();
    public Font fonta;
    ResourceLocation GUI = ResourceLocation.fromNamespaceAndPath(Galmc_api.MODID, "texture/gui/text.png");
    //GUI组件设置数据
    Character core;
    public boolean auto=false;
    public boolean skip=false;

    //文本框相关数据
    public boolean rendtext = true;
    public int text_speed = 2;

    //选项框
    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    NextText choice = null;

    //音乐
    public String old_music = null;
    private int music_tick;
    private int music_ticks=0;
    private boolean cg;
    private CGGalleryScreen cgGalleryScreen = null;
    public UUID player;
    public GalScreen(String start, UUID player) {
        super(Component.literal("启动！"));
        this.core = new Character(start);
        this.player = player;
        this.cg = false;
    }
    public GalScreen(String start, UUID player,CGGalleryScreen cgGalleryScreen) {
        super(Component.literal("启动！"));
        this.core = new Character(start);
        this.player = player;
        this.cg = true;
        this.cgGalleryScreen = cgGalleryScreen;
    }


    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        this.fonta = this.font;
        music();
        if(skip) next();
        this.core.render(p_281549_,this);
        for (Renderable renderable : this.renderables) {
            renderable.render(p_281549_, p_281550_, p_282878_, p_282465_);
        }
    }

    @Override
    protected void init() {
        if(core.init()) {

            assert this.minecraft != null;
            //开始音乐播放
            if (this.core.music != null) {
                if (!this.core.music.get("music").getAsString().equals(old_music)) {
                    stopSound();
                    try {
                        playSound(SoundInit.REG_SOUNDS.get(this.core.music.get("music").getAsString()).get(), 1.0f, 1.0f);
                    }catch (NullPointerException exception){
                        LOGGER.error("音频播放失败：可能音频路径不正确",exception);
                    }
                }
            } else {
                stopSound();
            }
            try {
                old_music = this.core.music.get("music").getAsString();
                this.music_tick = this.core.music.get("length").getAsInt();
            }catch (NullPointerException e){
                LOGGER.error("获取音乐信息失败：可能音乐信息未填写",e);
            }
            if(music_tick==-1) this.music_tick = 6000;


            super.init();
        }else {
            super.init();
            LOGGER.error("初始化"+core.id+"GUI失败：请检查资源文件(json)格式完整性或者检查路径是否正确");
            this.onClose();
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    private void music(){
        music_ticks++;//音乐连续播放
        if(music_ticks==music_tick){
            try {
                playSound(SoundInit.REG_SOUNDS.get(this.core.music.get("music").getAsString()).get(), 1.0f, 1.0f);
            }catch (NullPointerException exception){
                LOGGER.error("音频播放失败：可能音频路径不正确",exception);
            }
            music_ticks = 0;
        }
    }
    public void renderContain(GuiGraphics guiGraphics, int screenWidth, int screenHeight, ResourceLocation BACKGROUND_TEXTURE) {
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
    public void renderUI(GuiGraphics guiGraphics){

        if (rendtext) {
            guiGraphics.blit(GUI,
                    0, getY(740), // 位置
                    0, 0,           // 纹理坐标
                    getX(1920), getY(356),  // 尺寸
                    getX(1920), getY(356)        // 纹理尺寸
            );
        }
    }
    private boolean isMouseOverArea(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    };
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttona) {
        // 左键点击事件处理
        if (buttona == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.choice==null) {
            // 检查是否点击了自定义区域
            if(isMouseOverArea((int)mouseX,(int) mouseY,getX(1496),getY(307+740),getX(100),getY(49))){
                this.auto = !this.auto;
            } else if(isMouseOverArea((int)mouseX,(int) mouseY,getX(1608),getY(307+740),getX(100),getY(49))){
                this.skip = !this.skip;
            } else if(isMouseOverArea((int)mouseX,(int) mouseY,getX(1832),getY(307+740),getX(100),getY(49))){
                this.onClose();
            }else if (isMouseOverArea((int)mouseX, (int)mouseY, 0, getY(740), getX(1920),getY(356)) && rendtext) {
                next();
            }
        } else if (buttona == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            //右键隐藏文本
            this.rendtext = !this.rendtext;
        }
        return super.mouseClicked(mouseX, mouseY, buttona);
    }
    private void new_text(String path){
        if (button1 != null) {
            button1.visible = false;
            button1.active = false;
        }
        if (button2 != null) {
            button2.visible = false;
            button2.active = false;
        }
        if (button3 != null) {
            button3.visible = false;
            button3.active = false;
        }
        if (button4 != null) {
            button4.visible = false;
            button4.active = false;
        }
        this.core = new Character(path);
        this.button1 = null;
        this.button2 = null;
        this.choice = null;
        this.button3 = null;
        music_tick=0;
        music_ticks=0;
        this.skip=false;
        this.init();
    }
    
    public boolean next(){
        NextText nextText = core.next();
        if (nextText == null) {
            //正常往下推进
            if(core.is_sound()){
                try {
                    playVoiceSound(SoundInit.REG_SOUNDS.get(core.getSound()).get(), 1.0f, 1.0f);
                }catch (NullPointerException exception){
                    LOGGER.error("音频播放失败：可能音频路径不正确",exception);
                }

            }
            return true;
        }else {
            //已经结束

            if(cg){
                onClose();
                OpenUIEvent.openCG(this.player);
            }else if(Objects.equals(nextText.type, "end")){
                core.execute.execute(player);
                onClose();
            } else if (Objects.equals(nextText.type, "next")) {
                //接着下一节剧情
                new_text(nextText.refer.get(0));
                return true;
            }else if(Objects.equals(nextText.type, "choice")){
                //渲染选择框
                this.choice = nextText;
                int totalHeight = (20 * 3) + (20 * 2);

                // 计算起始Y位置（屏幕中心 - 总高度的一半）
                int startY = (this.height - totalHeight) / 2;
                try {
                    // 创建第一个按钮
                    button1 = Button.builder(
                                    Component.literal(nextText.text.get(0)),
                                    button -> new_text(nextText.refer.get(0))
                            )
                            .bounds(
                                    (this.width - 200) / 2,  // X居中
                                    startY,                           // Y位置
                                    200,
                                    20
                            ).createNarration(button -> Component.literal(nextText.text.get(0)))
                            .build();

                    // 创建第二个按钮
                    button2 = Button.builder(
                                    Component.literal(nextText.text.get(1)),
                                    button -> new_text(nextText.refer.get(1))
                            )
                            .bounds(
                                    (this.width - 200) / 2,
                                    startY + 20 + 20,
                                    200,
                                    20
                            )
                            .build();
                    this.addRenderableWidget(button1);
                    this.addRenderableWidget(button2);
                    if (nextText.refer.size() >= 3) {
                        button3 = Button.builder(
                                        Component.literal(nextText.text.get(2)),
                                        button -> new_text(nextText.refer.get(2))
                                )
                                .bounds(
                                        (this.width - 200) / 2,
                                        startY + (20 + 20) * 2,
                                        200,
                                        20
                                )
                                .build();
                        this.addRenderableWidget(button3);
                    }
                    if (nextText.refer.size() == 4) {
                        button4 = Button.builder(
                                        Component.literal(nextText.text.get(3)),
                                        button -> new_text(nextText.refer.get(3))
                                )
                                .bounds(
                                        (this.width - 200) / 2,
                                        startY + (20 + 20) * 2,
                                        200,
                                        20
                                )
                                .build();
                        this.addRenderableWidget(button4);
                    }
                }catch (IndexOutOfBoundsException e){
                    LOGGER.error("按钮创建失败",e);
                }

                // 添加按钮到屏幕


            }
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 键盘按键处理
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {

            return false;
        }

        if (keyCode == GLFW.GLFW_KEY_UP) {
            this.core.before();
        }
        if (keyCode == GLFW.GLFW_KEY_F9) {
            onClose();
            return true;
        }
        if(keyCode == GLFW.GLFW_KEY_LEFT_CONTROL){
            this.skip = true;
        }


        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int p_94715_, int p_94716_, int p_94717_) {
        this.skip = false;
        return super.keyReleased(p_94715_, p_94716_, p_94717_);
        
    }

    public int getX(int x){
        return (int) (this.width*1.0*(x*1.0/1920.0));
    }
    public int getY(int y){
        return (int) (this.height*1.0*(y*1.0/1080.0));
    }
    @Override
    public void onClose(){
        if(cg && cgGalleryScreen!=null){
            Minecraft.getInstance().setScreen(cgGalleryScreen);
            stopSound();
            super.onClose();
        }
        stopSound();
        super.onClose();
    }
    private void playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.getSoundManager().play(
                    SimpleSoundInstance.forUI(soundEvent, pitch, volume)
            );
        }else {
            
        }
    }
    SoundInstance old_soundEvent = null;

    private void playVoiceSound(SoundEvent soundEvent, float volume, float pitch) {
        if (this.minecraft != null && this.minecraft.player != null) {
            if (old_soundEvent != null) this.minecraft.getSoundManager().stop(this.old_soundEvent);
            old_soundEvent = SimpleSoundInstance.forUI(soundEvent, pitch, volume);
            this.minecraft.getSoundManager().play(
                    old_soundEvent
            );

        }else {
            System.out.println("fuck");
        }
    }
    private void stopSound() {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.getSoundManager().stop();
            } else {
                System.out.println("fuck");
            }
    }
}
