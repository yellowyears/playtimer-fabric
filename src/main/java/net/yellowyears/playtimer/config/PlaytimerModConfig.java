package net.yellowyears.playtimer.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.ColorPicker;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;


@Config(name = "playtimer")
public class PlaytimerModConfig implements ConfigData {
    @ColorPicker
    @Tooltip()
    public int playtimerColour = 0xFF5555;

    @ColorPicker
    @Tooltip()
    public int captionColour = 0xFF5555;

    @Tooltip()
    public String caption = "";

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Tooltip()
    public PlaytimerPosition playtimerPosition = PlaytimerPosition.BOTTOM_RIGHT;

    public enum PlaytimerPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        @Override
        public String toString() {
            return "playtimer.theme." + this.name().toLowerCase();
        }

    }

    @BoundedDiscrete(min = 1, max = 100)
    @Tooltip()
    public int scalePercentage = 30; // 30% = 1.5f

    @Tooltip()
    public boolean useMilliseconds = false;

}