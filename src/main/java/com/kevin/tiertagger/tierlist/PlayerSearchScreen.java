package com.kevin.tiertagger.tierlist;

import com.kevin.tiertagger.TierCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.uku3lig.ukulib.config.option.widget.TextInputWidget;
import net.uku3lig.ukulib.config.screen.CloseableScreen;
import net.uku3lig.ukulib.utils.Ukutils;

import java.util.Locale;

public class PlayerSearchScreen extends CloseableScreen {
    private TextInputWidget textField;
    private ButtonWidget searchButton;

    public PlayerSearchScreen(Screen parent) {
        super("Player Search", parent);
    }

    @Override
    protected void init() {
        String username = Text.translatable("tiertagger.search.user").getString();
        this.textField = this.addSelectableChild(new TextInputWidget(this.width / 2 - 100, 116, 200, 20,
                "", s -> {}, username, s -> s.matches("[a-zA-Z0-9_-]+"), 32));

        this.searchButton = this.addDrawableChild(
                ButtonWidget.builder(Text.translatable("tiertagger.search"), button -> this.tryShowProfile())
                        .dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20)
                        .build()
        );
        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.close())
                        .dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20)
                        .build()
        );

        this.setInitialFocus(this.textField);
    }

    @Override
    public void tick() {
        super.tick();
        this.searchButton.active = this.textField.isValid();
    }

    private void tryShowProfile() {
        String username = this.textField.getText();
        TierCache.searchPlayer(username.toLowerCase(Locale.ROOT))
                .exceptionally(t -> {
                    Ukutils.sendToast(Text.of("Error"), Text.of("Could not find player " + username));
                    return null;
                });

        MinecraftClient.getInstance().setScreen(new PlayerInfoScreen(this, username));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.textField.setText(string);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        this.textField.render(context, mouseX, mouseY, delta);
    }
}
