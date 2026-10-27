package dev.brothalucious.colorwins;
import java.util.Random;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Client-side grayscale-first color evolution for Minecraft 1.21.2. */
public final class ColorWinsClient implements ClientModInitializer {
    public static final String MOD_ID = "colorwins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Random RANDOM = new Random();
    private static final int EVOLUTION_TICKS = 20 * 60;
    private static final int TRANSITION_TICKS = 20 * 5;
    private static ManagedShaderEffect effect;
    private static Uniform1f transitionUniform, colorCountUniform, skyHueUniform, terrainHueUniform, entitiesHueUniform, uiHueUniform;
    private static int ticksSinceJoin, lastEvolutionStep = -1, colorCount;
    private static float transitionProgress, skyHue, terrainHue, entitiesHue, uiHue;

    @Override public void onInitializeClient() {
        try {
            effect = ShaderEffectManager.getInstance().manage(Identifier.of(MOD_ID, "color_shift"));
            transitionUniform = effect.findUniform1f("Transition");
            colorCountUniform = effect.findUniform1f("ColorCount");
            skyHueUniform = effect.findUniform1f("SkyHue");
            terrainHueUniform = effect.findUniform1f("TerrainHue");
            entitiesHueUniform = effect.findUniform1f("EntitiesHue");
            uiHueUniform = effect.findUniform1f("UiHue");
        } catch (RuntimeException ex) {
            LOGGER.error("Could not initialize Color Wins post effect. Check Satin/Minecraft compatibility.", ex);
            effect = null;
        }
        ClientTickEvents.END_CLIENT_TICK.register(ColorWinsClient::tick);
        LOGGER.info("Color Wins loaded: grayscale start, then a random color channel each minute.");
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) { reset(); return; }
        ticksSinceJoin++;
        int step = ticksSinceJoin / EVOLUTION_TICKS;
        while (lastEvolutionStep < step && colorCount < 4) { lastEvolutionStep++; chooseRandomCategoryHue(); }
        transitionProgress = MathHelper.clamp((ticksSinceJoin % EVOLUTION_TICKS) / (float) TRANSITION_TICKS, 0.0F, 1.0F);
        transitionProgress = transitionProgress * transitionProgress * (3.0F - 2.0F * transitionProgress);
    }

    private static void chooseRandomCategoryHue() {
        float hue = RANDOM.nextFloat();
        switch (RANDOM.nextInt(4)) { case 0 -> skyHue = hue; case 1 -> terrainHue = hue; case 2 -> entitiesHue = hue; case 3 -> uiHue = hue; default -> throw new IllegalStateException(); }
        colorCount = Math.min(4, colorCount + 1);
    }
    private static void reset() { ticksSinceJoin=0; lastEvolutionStep=-1; colorCount=0; transitionProgress=0; skyHue=terrainHue=entitiesHue=uiHue=0; }
    public static void renderFullFrame() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || effect == null) return;
        if (transitionUniform != null) transitionUniform.set(transitionProgress);
        if (colorCountUniform != null) colorCountUniform.set(colorCount);
        if (skyHueUniform != null) skyHueUniform.set(skyHue);
        if (terrainHueUniform != null) terrainHueUniform.set(terrainHue);
        if (entitiesHueUniform != null) entitiesHueUniform.set(entitiesHue);
        if (uiHueUniform != null) uiHueUniform.set(uiHue);
        effect.render(0.0F);
    }
}
