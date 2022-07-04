package net.yellowyears.playtimer.commands;

import net.yellowyears.playtimer.PlaytimerMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableTextContent;

public class ToggleCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        boolean newStatus = PlaytimerMod.toggle();

        if (newStatus) {
            source.sendFeedback(new TranslatableTextContent("playtimer.show"));
        } else {
            source.sendFeedback(new TranslatableTextContent("playtimer.hide"));
        }

        return 0;
    }
}
