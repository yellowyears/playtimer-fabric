package be.bjarno.playtimer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stat.Stats;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.LocalDateTime;

public class GuiPlayTime {

    MinecraftClient minecraft;

    private static final Logger LOGGER = PlaytimerMod.LOGGER;

    private GuiPlayTime() {
        minecraft = MinecraftClient.getInstance();
    }

    private static GuiPlayTime INSTANCE = null;
    public static GuiPlayTime getINSTANCE() {
        if (INSTANCE == null) { LOGGER.debug("Creating instance!"); INSTANCE = new GuiPlayTime(); }
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

    public void updateStats() {
        if (minecraft.player == null) { return; }

        if (!running) {
            Duration savedDuration = Storage.getInstance().getDuration(worldName);

            if (savedDuration == null) {
                int numberOfTicks = minecraft.player.getStatHandler().getStat(Stats.CUSTOM, Stats.PLAY_TIME);
                int seconds = numberOfTicks / 20;
                savedDuration = Duration.ofSeconds(seconds);
                Storage.getInstance().saveDuration(worldName, savedDuration);
            }

            startDuration = savedDuration;
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

    boolean isInit = false;
    String worldName;

    public void init() {
        if (isInit) return;

        // Determine server name
        IntegratedServer iServer = minecraft.getServer();
        ServerInfo sInfo = minecraft.getCurrentServerEntry();

        String name = "undefined";
        if (iServer != null) {
            name = iServer.getSaveProperties().getLevelName();
        } else if (sInfo != null) {
            String address = sInfo.address;
            String[] parts = address.split(":");
            if (parts.length == 1) {
                name = parts[0] + ":" + "25565";
            } else if (parts.length == 2) {
                name = parts[0] + ":" + parts[1];
            }
        }
        worldName = name;

        // Ask for statistics...
        requestStatsRefresh();

        isInit = true;
    }

    int counter = 5000;
    int initialCounter = counter;

    public void syncTime(Duration duration) {
        if (duration == null) { return; }
        Storage.getInstance().saveDuration(worldName, duration);
        counter = initialCounter;
    }

    private void cleanUp() {
        Duration duration = getPlayTime();
        syncTime(duration);
    }

    boolean oldPauseScreenState = false;

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
            syncTime(duration);
        }

        String hms = String.format("%02d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());

        // Draw the text in the bottom right corner of the screen.
        int xneed = minecraft.textRenderer.getWidth(hms);
        int yneed = minecraft.textRenderer.fontHeight;
        Window mainWindow = minecraft.getWindow();
        float scale = 1.5f;
        float offset = 0.965f;
        int xpos = Math.round((mainWindow.getScaledWidth() - xneed * scale) * offset);
        int ypos = Math.round((mainWindow.getScaledHeight() - yneed * scale) * offset);

        stack.push();
        stack.scale(scale, scale, scale);
        minecraft.textRenderer.drawWithShadow(stack, hms, xpos / scale, ypos / scale, 0xff5555);
        stack.pop();
    }
}
