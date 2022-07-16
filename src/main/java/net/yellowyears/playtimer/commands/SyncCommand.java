package net.yellowyears.playtimer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import net.yellowyears.playtimer.GuiPlayTime;

public class SyncCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        GuiPlayTime gui = GuiPlayTime.getMaybeInstance();
        if (gui != null) { gui.syncWithServer(); }
        source.sendFeedback(Text.translatable("playtimer.sync"));
        return 0;
    }
}
