package net.yellowyears.playtimer;

import net.yellowyears.playtimer.commands.HelpCommand;
import net.yellowyears.playtimer.commands.ResetCommand;
import net.yellowyears.playtimer.commands.SyncCommand;
import net.yellowyears.playtimer.commands.ToggleCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class PlaytimerMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("Playtimer");

	private static File hiddenfile = new File(".playtimer-hidden");
	static boolean timerVisible = true;

	@Override
	public void onInitialize() {
		Storage storage = Storage.getInstance();
		storage.readFile();
		LOGGER.debug("Playtimes loaded...");

		ClientCommandManager.DISPATCHER.register(
				literal("playtimer")
						.then(literal("reset").executes(new ResetCommand()))
						.then(literal("sync").executes(new SyncCommand()))
						.then(literal("toggle").executes(new ToggleCommand()))
						.then(literal("help").executes(new HelpCommand()))
						.executes(new HelpCommand())
		);

		loadVisible();
	}

	public static boolean toggle() {
		timerVisible = !timerVisible;
		saveVisible();
		return timerVisible;
	}

	public static void loadVisible() {
		timerVisible = !hiddenfile.exists();
	}

	public static void saveVisible() {
		if (timerVisible) {
			if (hiddenfile.exists()) {
				hiddenfile.delete();
			}
		} else {
			if (!hiddenfile.exists()) {
				try {
					hiddenfile.createNewFile();
				} catch (IOException e) {
					LOGGER.error("Could not update timer visibility status...");
				}
			}
		}
	}

}
