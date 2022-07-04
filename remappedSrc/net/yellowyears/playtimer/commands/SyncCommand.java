package net.yellowyears.playtimer.commands;

import net.yellowyears.playtimer.GuiPlayTime;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.text.TranslatableTextContent;

public class SyncCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        GuiPlayTime gui = GuiPlayTime.getMaybeInstance();
        if (gui != null) { gui.syncWithServer(); }
        source.sendFeedback(new TranslatableTextContent("playtimer.sync"));
        return 0;
    }
}
