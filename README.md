# Playtimer

Very simple mod\* that puts a red timer in the bottom right corner of the screen that shows how long you have been playing on a certain world/server. Inspired by Grian/Scar/Joel's Hardcore Series. No mods required on the server side.

This mod is built for Fabric (Fabric API is not required). No Forge version is planned.

\* No configuration required, just drop the jar file in the `mods/` folder and start playing Minecraft with a timer!

## How does this mod work?

This mod keeps track of how long you have been playing on a certain world (or server). The timer is managed locally, so it works even for servers that don't properly track your statistics. Only the first time when you join a world with this mod enabled, will the server statistics be queried to initialise the timer.

## How to install

Compile it yourself by cloning this repo, and running the `build` Gradle task.

Precompiled binaries can be found on https://modrinth.com/mod/playtimer/versions