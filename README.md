<div align="center">
    <img width="384" src="MattzLauncher/src/main/res/drawable/app_name_title.png" alt="MattzLauncher"></img>
</div>

<div align="center">

[![Android CI](https://github.com/Mattz1415/MattzLauncher/actions/workflows/build.yml/badge.svg)](https://github.com/Mattz1415/MattzLauncher/actions/workflows/build.yml)
[![GitHub release](https://img.shields.io/github/v/release/Mattz1415/MattzLauncher?color=00F0FF)](https://github.com/Mattz1415/MattzLauncher/releases)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

</div>

**MattzLauncher** is a next-generation Minecraft: Java Edition launcher for Android, completely overhauled with a modern **Glassmorphism & Microsoft Fluent Design System** and a pure **Dark OLED Theme (#000000)** for unmatched visual elegance, power efficiency, and butter-smooth performance (60–120 FPS).

---

### 🌟 Key Highlights & Remake Features

- 🌌 **Pure Dark OLED Theme (#000000)**: True black background maximizing OLED screen battery longevity and contrast.
- 🪟 **Glassmorphism & Fluent Design System**:
  - Translucent frosted glass acrylic cards and dialogs with elegant hairline borders.
  - Dynamic depth lighting, layered elevations, and smooth 14–20dp rounded squircle corners.
  - Sharp, comfortable Electric Cyan (`#00F0FF`) and Azure neon accents.
- ⚡ **Physics-Based UI/UX Motion & Animations**:
  - Silky-smooth screen entrance/exit depth transitions.
  - Tactile micro-interactions on buttons, chips, and cards with spring response.
  - Hardware-accelerated RenderThread animations ensuring stable 60–120 FPS without dropped frames.
  - Cinematic indeterminate progress bars with vibrant glowing linear tracks.
- 🎮 **Rock-Solid Game Launch Stability**: Retains 100% of PojavLauncher & runtime core capabilities (GLFW, OpenAL, Touch Controller, Multi-JRE 8/17/21).
- 🤖 **Automated CI/CD**: Full GitHub Actions workflow compiling APKs across multiple architectures (`all`, `arm64-v8a`, `armeabi-v7a`, `x86_64`) with instant artifact uploads.

---

### 🛠️ Building From Source

To build MattzLauncher locally:

```bash
# Build JRE LWJGL3 component
./gradlew jre_lwjgl3glfw:build

# Build MattzLauncher Debug APK
./gradlew MattzLauncher:assembleDebug -Darch=all
```

The compiled APK will be generated in `MattzLauncher/build/outputs/apk/debug/`.

---

### 📜 License

MattzLauncher is open-source software licensed under the [GNU General Public License v3.0 (GPL-3.0)](LICENSE).
Based on [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) and ZalithLauncher foundations.
