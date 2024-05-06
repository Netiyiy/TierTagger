package com.kevin.tiertagger.tierlist;

import com.kevin.tiertagger.TierCache;
import com.kevin.tiertagger.model.PlayerInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Locale;

public class PlayerSearchScreen extends Screen {
    private TextFieldWidget textField;
    private ButtonWidget searchButton;
    private final Screen parent;

    public PlayerSearchScreen(Screen parent) {
        super(Text.of("Player Search"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.textField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, Text.empty());
        this.textField.setMaxLength(32);
        this.addSelectableChild(this.textField);

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
        this.textField.tick();
        this.searchButton.active = this.textField.getText().matches("[a-zA-Z0-9_-]+");
    }

    private void tryShowProfile() {
        String username = this.textField.getText();
        PlayerInfo i = TierCache.searchPlayer(username.toLowerCase(Locale.ROOT))
                .exceptionally(t -> {
                    sendToast("Could not find player " + username);
                    return null;
                })
                .join();

        if (i != null) {
            MinecraftClient.getInstance().setScreen(new PlayerInfoScreen(this, username, i));
        }
    }

    private void sendToast(String body) {
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Error"), Text.of(body));
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.textField.setText(string);
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("tiertagger.search.user"), this.width / 2 - 100, 100, 10526880);
        this.textField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }
}
