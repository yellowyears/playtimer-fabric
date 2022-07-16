package net.yellowyears.playtimer.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;

@Config(name = "playtimer")
public class PlaytimerModConfig implements ConfigData {
    @ConfigEntry.ColorPicker
    @ConfigEntry.Gui.Tooltip()
    public int colour = 0xFF5555;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip()
    public PlaytimerPosition playtimerPosition = PlaytimerPosition.BOTTOM_RIGHT;

    public enum PlaytimerPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        @Override
        public String toString() {
            return "playtimer.theme." + this.name().toLowerCase();
        }
    }

    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    @ConfigEntry.Gui.Tooltip()
    public int scalePercentage = 30; // 30% = 1.5f

}