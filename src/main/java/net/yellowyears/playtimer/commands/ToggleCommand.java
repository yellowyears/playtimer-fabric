package net.yellowyears.playtimer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.yellowyears.playtimer.PlaytimerMod;

public class ToggleCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        boolean newStatus = PlaytimerMod.toggle();

        if (newStatus) {
            source.sendFeedback(Text.translatable("playtimer.show"));
        } else {
            source.sendFeedback(Text.translatable("playtimer.hide"));
        }

        return 0;
    }
}
