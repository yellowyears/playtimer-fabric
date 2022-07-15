package net.yellowyears.playtimer.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "playtimer")
public class PlaytimerModConfig implements ConfigData {
    @ConfigEntry.ColorPicker
    public int colour = 0xFF5555;

}