package be.bjarno.playtimer;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;

public class Storage {

    private static final Logger LOGGER = PlaytimerMod.LOGGER;
    private static Storage INSTANCE = null;

    public static Storage getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Storage();
        }
        return INSTANCE;
    }

    private Storage() {

    }

    private final File playTimeFile = new File("./playtimes.txt");
    private HashMap<String, Duration> playTimes = new HashMap<>();

    private final String separator = " -> ";

    public void readFile() {
        HashMap<String, Duration> map = new HashMap<>();

        if (playTimeFile.exists()) {
            try {
                try (BufferedReader bufferedReader = Files.newReader(playTimeFile, Charsets.UTF_8)) {
                    bufferedReader.lines().forEach((line) -> {
                        line = line.trim();
                        if (line.length() > 0) {
                            String[] parts = line.split(separator);
                            String name = parts[0];
                            String rest = parts[1];
                            if (!name.contains(" ")) {
                                long millis = Long.parseLong(rest);
                                Duration d = Duration.ofMillis(millis);
                                map.put(name, d);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Could not load existing playtimes...");
            }
        }

        playTimes = map;
    }

    public void writeFile() {
        try {
            try (BufferedWriter bufferedWriter = Files.newWriter(playTimeFile, Charsets.UTF_8)) {
                boolean first = true;
                for (String name : playTimes.keySet()) {
                    Duration d = playTimes.getOrDefault(name, Duration.ZERO);
                    long millis = d.toMillis();
                    if (!first) {
                        bufferedWriter.write("\n");
                    }
                    bufferedWriter.write(name + separator + millis);
                    first = false;
                }

                bufferedWriter.flush();
                LOGGER.debug("Durations updated...");
            }
        } catch (Exception e) {
            LOGGER.error("Could not write playtimes...");
        }
    }

    public Duration getDuration(String name) {
        return playTimes.getOrDefault(name, null);
    }

    public void saveDuration(String path, Duration duration) {
        playTimes.put(path, duration);
        writeFile();
    }

}
