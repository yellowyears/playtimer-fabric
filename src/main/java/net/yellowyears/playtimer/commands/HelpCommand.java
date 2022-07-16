package net.yellowyears.playtimer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.text.Text;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

public class HelpCommand implements Command<FabricClientCommandSource> {
    @Override
    public int run(CommandContext<FabricClientCommandSource> context) {
        FabricClientCommandSource source = context.getSource();
        source.sendFeedback(Text.translatable("playtimer.help"));
        return 0;
    }
}
