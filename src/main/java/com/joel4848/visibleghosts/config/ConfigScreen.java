package com.joel4848.visibleghosts.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private TransparencySlider transparencySlider;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Visible Ghosts (Self) Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Slider for ghost transparency
        this.transparencySlider = new TransparencySlider(
                this.width / 2 - 100,
                this.height / 2 - 20,
                200,
                20,
                ModConfig.getInstance().getGhostTransparency()
        );
        this.addDrawableChild(this.transparencySlider);

        // Done button
        this.addDrawableChild(ButtonWidget.builder(
                        ScreenTexts.DONE,
                        button -> {
                            if (this.client != null) {
                                this.client.setScreen(this.parent);
                            }
                        })
                .dimensions(this.width / 2 - 100, this.height / 2 + 20, 200, 20)
                .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        // Draw title
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                this.width / 2,
                20,
                0xFFFFFF
        );
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    private static class TransparencySlider extends SliderWidget {
        public TransparencySlider(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, Text.empty(), initialValue / 255.0);
            this.updateMessage();
        }

        @Override
        protected void updateMessage() {
            int value = (int) (this.value * 255);
            this.setMessage(Text.literal("Ghost Transparency: ")
                    .append(Text.literal(String.valueOf(value)).formatted(Formatting.GOLD)));
        }

        @Override
        protected void applyValue() {
            int value = (int) (this.value * 255);
            ModConfig.getInstance().setGhostTransparency(value);
        }
    }
}