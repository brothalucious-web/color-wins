# Color Wins — Grayscale to Random Color Evolution

Fabric client mod for Minecraft **1.21.2** (Java 21).

Starts in grayscale. Each minute the evolution counter advances and a randomly selected category is assigned its own randomly selected hue: sky, terrain, mobs/entities, or HUD/UI. Fades are smoothed over approximately five seconds. The colors are randomized rather than following a fixed red/blue/green order.

## Rendering caveat
This first implementation is a full-frame post-process with approximate screen-space masks, not object-aware masks. The shader cannot reliably distinguish every sky pixel from terrain, individual mobs from blocks, or all UI elements. Those categories may overlap; precise separation requires deeper Minecraft rendering integration or dedicated render-layer masks. A successful build does not prove the effect works in-game; test with Minecraft 1.21.2, Fabric Loader, Fabric API, and Satin.

## Build
Windows: `gradlew.bat build`. Linux/macOS: `./gradlew build`. JAR output: `build/libs/`.
