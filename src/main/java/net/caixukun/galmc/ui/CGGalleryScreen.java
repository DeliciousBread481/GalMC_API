package net.caixukun.galmc.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.caixukun.galmc.Galmc_api;
import net.caixukun.galmc.resource.GalResourceManger;
import net.caixukun.galmc.ui.character.Character;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * CG 鉴赏屏幕，每页9个缩略图，支持翻页，缩略图保持原图比例并居中显示。
 */
public class CGGalleryScreen extends Screen {

    private static final int PAGE_SIZE = 6;
    private static final int THUMBNAIL_SPACING = 10;      // 缩略图之间的间距
    private static final float THUMBNAIL_ASPECT_RATIO = 16.0f / 9.0f; // 16:9比例
    private int thumbnailWidth;      // 缩略图宽度（动态计算）
    private int thumbnailHeight;     // 缩略图高度（动态计算）

    private List<Character> cgList = new ArrayList<>();

    private int currentPage = 0;
    private int totalPages;
    private List<Character> currentPageCGs;
    private UUID uuid;
    private Button prevPageButton;
    private Button nextPageButton;
    private List<CGThumbnailButton> thumbnailButtons = new ArrayList<>();

    public CGGalleryScreen(UUID uuid) {
        super(Component.literal("CG鉴赏"));
        for(String path : GalResourceManger.getCCg()){
            Character c = new Character(path);
            if(c.init()){
                cgList.add(c);
            }
        }
        this.uuid = uuid;
        this.totalPages = (int) Math.ceil((double) cgList.size() / PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;
        updateCurrentPageCGs();
    }

    @Override
    protected void init() {
        super.init();

        int maxThumbnailWidth = (this.width - 4 * THUMBNAIL_SPACING) / 3; // 左右各留一个间距的边距
        thumbnailWidth = Math.min(200, maxThumbnailWidth); // 限制最大宽度200，避免太大
        thumbnailHeight = (int) (thumbnailWidth / THUMBNAIL_ASPECT_RATIO); // 根据16:9计算高度

        // 计算网格的起始位置（居中，但从顶部开始）
        int gridWidth = 3 * thumbnailWidth + 2 * THUMBNAIL_SPACING;
        int startX = (this.width - gridWidth) / 2;
        int startY = 60; // 从顶部60像素开始，留出标题空间

        thumbnailButtons.clear();
        for (int i = 0; i < PAGE_SIZE; i++) {
            int row = i / 3;
            int col = i % 3;
            int x = startX + col * (thumbnailWidth + THUMBNAIL_SPACING);
            int y = startY + row * (thumbnailHeight + THUMBNAIL_SPACING);

            // 方法1：使用 final 临时变量（推荐）
            final int index = i; // 用于调试，如果需要索引的话
            CGThumbnailButton button = new CGThumbnailButton(
                    x, y, thumbnailWidth, thumbnailHeight,
                    null,
                    btn -> {
                        // 注意：这里要通过 btn 参数获取按钮实例，而不是使用外部的 button 变量
                        CGThumbnailButton clickedButton = (CGThumbnailButton) btn;
                        if (clickedButton.getCharacter() != null) {  // 注意方法名应该是 getCgEntry() 而不是 getCharacter()
                            Minecraft.getInstance().setScreen(new GalScreen(clickedButton.Character.id,this.uuid,this));
                        }
                    }
            );
            addRenderableWidget(button);
            thumbnailButtons.add(button);
        }


        this.prevPageButton = Button.builder(Component.literal("<"), btn -> turnPage(-1))
                .bounds(getX(60), getY(40), 40, 20).build();
        addRenderableWidget(prevPageButton);

        this.nextPageButton = Button.builder(Component.literal(">"), btn -> turnPage(1))
                .bounds(getX(1800), getY(40), 40, 20).build();
        addRenderableWidget(nextPageButton);

        updatePageDisplay();
    }

    private void turnPage(int delta) {
        int newPage = currentPage + delta;
        if (newPage >= 0 && newPage < totalPages) {
            currentPage = newPage;
            updateCurrentPageCGs();
            updatePageDisplay();
        }
    }

    private void updateCurrentPageCGs() {
        int fromIndex = currentPage * PAGE_SIZE;
        int toIndex = Math.min(fromIndex + PAGE_SIZE, cgList.size());
        currentPageCGs = cgList.subList(fromIndex, toIndex);
    }

    private void updatePageDisplay() {
        for (int i = 0; i < PAGE_SIZE; i++) {
            CGThumbnailButton btn = thumbnailButtons.get(i);
            if (i < currentPageCGs.size()) {
                btn.setCharacter(currentPageCGs.get(i));
                btn.visible = true;
            } else {
                btn.setCharacter(null);
                btn.visible = false;
            }
        }
        prevPageButton.active = currentPage > 0;
        nextPageButton.active = currentPage < totalPages - 1;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if(GalResourceManger.cg_background!=null){
            renderContain(guiGraphics,this.width,this.height,ResourceLocation.fromNamespaceAndPath(Galmc_api.MODID,GalResourceManger.cg_background));
        }else {
            this.renderBackground(guiGraphics);
        }
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        String pageText = (currentPage + 1) + " / " + totalPages;
        guiGraphics.drawCenteredString(this.font, pageText, this.width / 2, this.height - 35, 0xAAAAAA);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
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

    // ================== 内部类 ==================

    /**
     * 自定义图片按钮，用于显示 CG 缩略图。
     * 绘制时会保持原图比例并居中显示在按钮区域内。
     */
    private static class CGThumbnailButton extends Button {
        private Character Character;

        public CGThumbnailButton(int x, int y, int width, int height, Character entry, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.Character = entry;
        }

        public void setCharacter(Character entry) {
            this.Character = entry;
        }

        public Character getCharacter() {
            return Character;
        }

        @Override
        protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (Character == null) return;

            // 绘制按钮背景（半透明黑色框，可自行调整或移除）
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x80000000);

            ResourceLocation texture = Character.get().background;
            if (texture != null) {
                int imgWidth = Character.get().ix;
                int imgHeight = Character.get().iy;

                // 计算保持原图比例且完全显示在按钮内的缩放尺寸
                float scale = Math.min((float) width / imgWidth, (float) height / imgHeight);
                int scaledWidth = (int) (imgWidth * scale);
                int scaledHeight = (int) (imgHeight * scale);

                // 计算居中偏移
                int offsetX = getX() + (width - scaledWidth) / 2;
                int offsetY = getY() + (height - scaledHeight) / 2;

                // 绘制缩放后的原图（实时压缩）
                guiGraphics.blit(texture, offsetX, offsetY, scaledWidth, scaledHeight, 0, 0, imgWidth, imgHeight, imgWidth, imgHeight);
            }
        }
    }
    public int getX(int x){
        return (int) (this.width*1.0*(x*1.0/1920.0));
    }
    public int getY(int y){
        return (int) (this.height*1.0*(y*1.0/1080.0));
    }
}