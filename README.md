![Minecraft version](https://img.shields.io/badge/minecraft-1.12.2-69C12E.svg) [![GitHub release](https://img.shields.io/github/release/mccreery/hotswap.svg)](https://github.com/mccreery/hotswap/releases/latest) [![CurseForge downloads](http://cf.way2muchnoise.eu/full_hotswap_downloads.svg)](https://minecraft.curseforge.com/projects/hotswap)

# HotSwap
This is a small, simple mod for Minecraft (Forge) which adds hotkeys and mouse controls to cycle through rows of the player's inventory, either one slot at a time or the entire row.

## Controls
- `J` and `K`: cycle the current slot down and up, respectively (configurable)
- `H` and `L`: cycle the whole row down and up, respectively (configurable)
- `Alt+MWheel`: cycle the current slot
- `Ctrl+Alt+MWheel`: cycle the whole row

## Development
Setting up the workspace is a little different from usual. Forge usually checks for access transformers (ATs) from dependencies before actually running the task which extracts them (MinecraftForge/ForgeGradle#312). To correctly apply the ATs, run:
```
./gradlew extractDependencyATs
```
before the usual `./gradlew setupDecompWorkspace`.
