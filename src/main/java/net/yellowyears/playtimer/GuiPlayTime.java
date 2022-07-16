package net.yellowyears.playtimer;

import org.slf4j.Logger;
import java.time.Duration;
import java.time.LocalDateTime;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.Stats;

import net.yellowyears.playtimer.config.PlaytimerModConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class GuiPlayTime {

    MinecraftClient minecraft;

    PlaytimerModConfig config = AutoConfig.getConfigHolder(PlaytimerModConfig.class).getConfig();

    private static final Logger LOGGER = PlaytimerMod.LOGGER;

    private GuiPlayTime() {
        minecraft = MinecraftClient.getInstance();
    }

    private static GuiPlayTime INSTANCE = null;
    public static GuiPlayTime getInstance() {
        if (INSTANCE == null) { LOGGER.debug("Creating instance!"); INSTANCE = new GuiPlayTime(); }
        return INSTANCE;
    }

    public static GuiPlayTime getMaybeInstance() {
        return INSTANCE;
    }

    public static void removeInstance() {
        if (INSTANCE != null) {
            LOGGER.debug("Cleaning up instance!...");

            INSTANCE.cleanUp();
        }

        INSTANCE = null;
    }

    boolean running = false;
    Duration startDuration = null;
    LocalDateTime startTime = null;
    boolean forceRefresh = false;

    public void checkStats() {
        if (minecraft.player == null) { return; }

        if (!running || forceRefresh) {
            Duration savedDuration = Storage.getInstance().getDuration(timerId);

            if (savedDuration == null || forceRefresh) {
                int numberOfTicks = minecraft.player.getStatHandler().getStat(Stats.CUSTOM, Stats.PLAY_TIME);
                int seconds = numberOfTicks / 20;
                savedDuration = Duration.ofSeconds(seconds);
                Storage.getInstance().saveDuration(timerId, savedDuration);

                forceRefresh = false;
            }

            startDuration = savedDuration;
            pauseDuration = Duration.ZERO;
            startTime = LocalDateTime.now();
            running = true;
        }
    }

    public void requestStatsRefresh() {
        ClientStatusC2SPacket packet = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS);
        ClientPlayNetworkHandler network = minecraft.getNetworkHandler();
        if (network == null) { return; }
        network.sendPacket(packet);
    }

    boolean isPaused = false;
    LocalDateTime pauseStartTime = null;
    Duration pauseDuration = Duration.ZERO;

    public Duration getPlayTime() {
        if (!running) return null;

        Duration timePaused = Duration.ZERO;
        LocalDateTime now = LocalDateTime.now();

        if (minecraft.isPaused()) {
            if (!isPaused) {
                pauseStartTime = LocalDateTime.now();
                counter = -1;
            }
            timePaused = Duration.between(pauseStartTime, now);
            isPaused = true;
        } else {
            if (isPaused) {
                pauseDuration = pauseDuration.plus(Duration.between(pauseStartTime, now));
            }
            isPaused = false;
        }

        return Duration.between(startTime, now)
                .plus(startDuration)
                .minus(timePaused)
                .minus(pauseDuration);
    }

    public void reset() {
        startTime = LocalDateTime.now();
        startDuration = Duration.ZERO;
        pauseDuration = Duration.ZERO;
    }

    public void syncWithServer() {
        requestStatsRefresh();
        forceRefresh = true;
    }

    boolean isInit = false;
    String timerId;

    public void init() {
        if (isInit) return;

        // Determine server name
        IntegratedServer iServer = minecraft.getServer();
        ServerInfo sInfo = minecraft.getCurrentServerEntry();

        String playmode = null;
        String worldName = null;
        String playerName = null;

        if (minecraft.player != null) {
            GameProfile profile = minecraft.player.getGameProfile();
            if (profile == null) {
                playerName = minecraft.player.getEntityName();
            } else {
                playerName = profile.getId().toString();
            }
        }

        if (playerName == null) { playerName = "unknown"; }

        if (iServer != null) {
            worldName = iServer.getSaveProperties().getLevelName();
            playmode = "SP";
        } else if (sInfo != null) {
            String address = sInfo.address;
            String[] parts = address.split(":");
            if (parts.length == 1) {
                worldName = parts[0] + ":" + "25565";
            } else if (parts.length == 2) {
                worldName = parts[0] + ":" + parts[1];
            }
            playmode = "MP";
        }

        if (playmode == null) {
            timerId = "undefined";
        } else {
            timerId = playmode + "(" + worldName + "," + playerName + ")";
        }


        // Ask for statistics...
        requestStatsRefresh();

        isInit = true;
    }

    int counter = 5000;
    int initialCounter = counter;

    public void updateDurationInStorage(Duration duration) {
        if (duration == null) { return; }
        Storage.getInstance().saveDuration(timerId, duration);
        counter = initialCounter;
    }

    private void cleanUp() {
        Duration duration = getPlayTime();
        updateDurationInStorage(duration);
    }

    boolean oldPauseScreenState = false;

    // Text renderer variables
    float xOffset; float yOffset;
    float xOffsetCaption; float yOffsetCaption;
    float maxScale = 5f;

    public void render(MatrixStack stack) {
        if (minecraft == null || minecraft.player == null || minecraft.world == null || minecraft.player.world == null) {
            return;
        }

        init();

        Duration duration = getPlayTime();

        if (duration == null) { return; }

        if (!minecraft.isPaused()) {
            counter--;
        }

        boolean refresh = false;
        boolean pauseScreenState = (minecraft.currentScreen != null);
        if (oldPauseScreenState != pauseScreenState && pauseScreenState) { refresh = true; }
        oldPauseScreenState = pauseScreenState;
        if (counter <= 0 || refresh) {
            updateDurationInStorage(duration);
        }

        String hms = String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        if (forceRefresh) {
            hms = "??:??:??";
        }

        if (!PlaytimerMod.timerVisible) { return; }

        // Draw the text in the bottom right corner of the screen.
        int xNeed = minecraft.textRenderer.getWidth(hms);
        int yNeed = minecraft.textRenderer.fontHeight;
        Window mainWindow = minecraft.getWindow();

        int xNeedCaption = minecraft.textRenderer.getWidth(config.caption);

        // Prevent crash when config changes
        if(config.playtimerPosition == null) {
            config.playtimerPosition = PlaytimerModConfig.PlaytimerPosition.BOTTOM_RIGHT;
        }

        int scalePercentage = config.scalePercentage;
        float scale = (scalePercentage * maxScale) / 100;

        float xPosCaption;
        int yPosCaption = Math.round((mainWindow.getScaledHeight() - yNeed * scale) * yOffsetCaption);

        stack.push();
        stack.scale(scale, scale, scale);

        // Setting different offsets for different positions
        // Needs re-doing, some are inconsistent
        // TOP LEFT: X=0.015 Y=0.025 : XS=0.0078, YS=0.085
        // TOP RIGHT: X=0.985 Y=0.025 : XS=0.985 YS=0.085
        // BOTTOM LEFT: X=0.025 Y=0.965: XS=0.02 YS=0.905
        // BOTTOM RIGHT X=0.965 Y=0.965: XS=0.96 YS=0.905
        switch(config.playtimerPosition){
            case TOP_LEFT -> {
                xOffset = 0.01f;
                yOffset = 0.025f;
                xOffsetCaption = 0.0078f;
                yOffsetCaption = 0.085f;
                xPosCaption = Math.round((mainWindow.getScaledWidth() + xNeedCaption * scale) * xOffsetCaption);
            }
            case TOP_RIGHT -> {
                xOffset = 0.985f;
                yOffset = 0.025f;
                xOffsetCaption = 0.985f;
                yOffsetCaption = 0.085f;
                xPosCaption = Math.round((mainWindow.getScaledWidth() - xNeedCaption * scale) * xOffsetCaption);
            }
            case BOTTOM_LEFT -> {
                xOffset = 0.025f;
                yOffset = 0.965f;
                xOffsetCaption = 0.02f;
                yOffsetCaption = 0.905f;
                xPosCaption = Math.round((mainWindow.getScaledWidth() + xNeedCaption * scale) * xOffsetCaption);
            }
            default -> { // BOTTOM_RIGHT
                xOffset = 0.965f;
                yOffset = 0.965f;
                xOffsetCaption = 0.96f;
                yOffsetCaption = 0.905f;
                xPosCaption = Math.round((mainWindow.getScaledWidth() - xNeedCaption * scale) * xOffsetCaption);
            }
        }

        int xPos = Math.round((mainWindow.getScaledWidth() - xNeed * scale) * xOffset);
        int yPos = Math.round((mainWindow.getScaledHeight() - yNeed * scale) * yOffset);

        int colour = config.colour;

        // Render Timer
        minecraft.textRenderer.drawWithShadow(stack, hms, xPos / scale, yPos / scale, colour);

        //Render Caption
        minecraft.textRenderer.drawWithShadow(stack, config.caption, xPosCaption / scale, yPosCaption / scale, colour);

        stack.pop();
    }

}