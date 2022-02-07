package be.bjarno.playtimer.commands;

import be.bjarno.playtimer.GuiPlayTime;
import be.bjarno.playtimer.PlaytimerMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableText;

public class ToggleCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        boolean newStatus = PlaytimerMod.toggle();

        if (newStatus) {
            source.sendFeedback(new TranslatableText("playtimer.show"));
        } else {
            source.sendFeedback(new TranslatableText("playtimer.hide"));
        }

        return 0;
    }
}
