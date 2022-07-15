package net.yellowyears.playtimer.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;

@Config(name = "playtimer")
public class PlaytimerModConfig implements ConfigData {
    @ConfigEntry.ColorPicker
    public int colour = 0xFF5555;

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public PlaytimerPosition playtimerPosition = PlaytimerPosition.BOTTOM_RIGHT;

    public static enum PlaytimerPosition {
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT;

        @Override
        public String toString() {
            return "playtimer.theme." + this.name().toLowerCase();
        }
    }

}