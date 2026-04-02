<div align="center">

# MercuryChat

<p align="center">
  <img src="http://i.imgur.com/VjzWm5F.png" alt="MercuryChat Screenshot"/>
</p>

**MercuryChat** is a JVM-based overlay utility for Path of Exile, designed for efficient trade management and real-time gameplay monitoring. This community-maintained fork focuses on stability, performance, and advanced filtering capabilities.

[![GitHub release](https://img.shields.io/github/release/mr-september/MercuryChat.svg)](https://github.com/mr-september/MercuryChat/releases)
[![GitHub downloads](https://img.shields.io/github/downloads/mr-september/MercuryChat/total.svg)](https://github.com/mr-september/MercuryChat/releases)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://java.com/download)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE.TXT)

**Performance Optimized** | **Highly Customizable** | **Community Driven**

### Resources
[Latest Release](https://github.com/mr-september/MercuryChat/releases) | [Java 8+](https://java.com/download) | [Wiki](https://github.com/mr-september/MercuryChat/wiki)

---

## Support FOSS Development

Maintaining and evolving open-source software relies on community support. If MercuryChat improves your experience, consider supporting the project.

<p align="center">
<a href="https://www.paypal.com/donate/?hosted_button_id=WFXL2T42BBCRN">
  <img src="https://raw.githubusercontent.com/mr-september/central_automation_hub/refs/heads/main/bluePayPalbutton.svg" alt="PayPal" height="32">
</a>
<a href="https://ko-fi.com/Q5Q11I49GI">
  <img src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Ko-fi" height="32">
</a>
<a href="https://liberapay.com/mr-september/donate">
  <img src="https://liberapay.com/assets/widgets/donate.svg" alt="Liberapay" height="32">
</a>
<a href="https://nowpayments.io/donation?api_key=5b5fabd5-2c33-4525-99a3-bf27f587780c" target="_blank" rel="noreferrer noopener">
  <img src="https://nowpayments.io/images/embeds/donation-button-black.svg" alt="Crypto donation button by NOWPayments" height="32">
</a>
</p>

</div>

## Table of Contents

- [Quick Start](#quick-start)
- [New Features](#new-features)
- [Feature Overviews](#feature-overviews)
- [System Requirements](#system-requirements)
- [Installation](#installation)
- [Usage Guide](#usage-guide)
- [Developer & Build Guide](#developer--build-guide)
- [Changelog](#changelog)

## Quick Start

1. **Prerequisites**: Ensure [Java 8+](https://java.com/download) is installed.
2. **Download**: Get the [Latest Release](https://github.com/mr-september/MercuryChat/releases).
3. **Launch**: Execute `MercuryChat.jar` or `MercuryChat.exe`.
4. **Configuration**: Set Path of Exile to **Windowed** or **Borderless** mode.

> [!IMPORTANT]
> Path of Exile must be in Windowed or Borderless mode for the overlay to function correctly.

## New Features

### Chat Scanner: "+text" Response Support
Automatically detects and processes modern Path of Exile chat patterns.
- **Smart Detection**: Identifies patterns like "Free elder +elder".
- **One-Click Response**: Integration for global and trade chat responses.
- **Hotkey Support**: Full keyboard shortcut integration for rapid replies.

### Advanced Filtering: Start Operator (^)
Enhanced filtering logic for cleaner scanner results.
- **Prefix Filtering**: Use the `^` operator to exclude messages that start with specific character strings.
- **Combined Logic**: Support for multiple filter combinations (e.g., `^+`, `^-`).

## Feature Overviews

### Overseer: Monitoring & Overlays
Customizable timers and screen-capture overlays for real-time tracking.
- **Flask & Buff Tracking**: Monitor flask durations and Vaal skill uptimes.
- **Cooldown Monitoring**: Track internal skill cooldowns.
- **Screen Duplication**: Duplicate specific screen regions for better visibility (e.g., mini-map or health bar).

### Notification Management
Streamlined trade and scanner notifications with actionable shortcuts.
- **Quick Responses**: Contextual buttons for "Sold," "Wait 5m," etc.
- **Smart Grouping**: Stacked notifications for multiple trade requests.
- **Notification History**: Persistent storage for all trade metadata.

### Stash Overlay & Highlighting
Precision highlighting for stash tab item retrieval.
- **Cell Highlighting**: Visual overlays for exact stash locations.
- **Coordinate Alignment**: Customizable grid settings for different resolutions.

### Focus Mode (DND)
Silent background processing for high-intensity gameplay sessions.
- **Toggleable State**: Instantly pause visual alerts while maintaining history.
- **One-Click Resume**: Restore queued notifications after content is cleared.

## System Requirements

- **Runtime**: Java 8 or higher (JDK 8 recommended)
- **OS**: Windows 10/11, Linux (Ubuntu 18.04+)
- **Memory**: 512MB RAM minimum (1GB recommended)
- **PoE Settings**: Windowed or Borderless mode required

## Installation

### Option 1: JAR (Universal)
1. Install [Java 8+](https://java.com/download).
2. Download `MercuryChat.jar`.
3. Launch via double-click or `java -jar MercuryChat.jar`.

### Option 2: Windows Executable
1. Download `MercuryChat.exe`.
2. Run as Administrator to ensure full overlay functionality.

> [!TIP]
> The `.exe` is a wrapper around the `.jar`. If Windows flags it as "unrecognized," click "More info" > "Run anyway."

## Usage Guide

1. **Launch** MercuryChat prior to starting Path of Exile.
2. **Configure Game Path** in the settings to allow log monitoring.
3. **Customize Hotkeys** for trade responses and scanner navigation.
4. **Test** the notification system via the "Test" button in settings.

## 🛠️ Developer & Build Guide

### Prerequisites
- **Java 8+**
- **Maven 3.6+**
- **Git**
- **Launch4j** (optional, for Windows EXE generation)

### 🚀 Automated Release Process

The project uses GitHub Actions for automated builds and releases.

#### Creating a New Release
1. **Git Tag (Recommended)**:
   ```bash
   git tag v1.8.0
   git push origin v1.8.0
   ```
2. **Manual Trigger**:
   - Go to **Actions** tab → **Build and Release** → **Run workflow**.

#### Build Artifacts
Each release automatically generates:
- `MercuryChat-{version}-exe.zip` (Windows executable)
- `MercuryChat-{version}-jar.zip` (Universal JAR)
- `MercuryChat-{version}-lang.zip` (Language files)

### 🔧 Local Development

#### Using Build Scripts
The repository includes scripts to simplify the build process:

- **Windows**: `build_mercury.bat` (Full) or `build_mercury_simple.bat` (Fast)
- **Linux/macOS**: `./build_mercury.sh` (Full) or `./build_mercury_simple.sh` (Fast)

#### Using Maven Directly
```bash
# Standard Maven Build
mvn clean package

# Build without running tests
mvn clean package -DskipTests
```

### 🐛 Troubleshooting

| Issue | Resolution |
| :--- | :--- |
| **Launch4j EXE fails** | Ensure Launch4j is in your PATH. This is non-critical for JAR creation. |
| **Missing Dependencies** | Run `mvn dependency:purge-local-repository` to clear the cache. |
| **Java Version Mismatch** | Verify `java -version` is 8 or higher. JDK 8 is recommended for maximum compatibility. |
| **UI Rendering Issues** | Ensure all UI actions are wrapped in `SwingUtilities.invokeLater()`. |

---

## Changelog

For a detailed history of changes, see [CHANGELOG.md](CHANGELOG.md).

### Recent Highlights

**v1.8.0 - Project Refinement (Upcoming)**
- Renaming to MercuryChat and UI padding fixes

**v1.7.0 - Security & Build Improvements**
- Comprehensive CodeQL security analysis integration
- Java 8 compatibility enhancements
- Maven multi-module build fixes
- GitHub Actions CI/CD improvements
- Deep refactoring of entire project for stability and performance

- **v1.5.0**: Community-requested feature enhancements for channel management.

---

<p align="center">
  <strong>MercuryChat</strong><br>
  <em>Path of Exile Chat Monitoring Utility</em>
</p>

<p align="center">
  <a href="https://github.com/mr-september/MercuryChat/releases">Download</a> •
  <a href="https://github.com/mr-september/MercuryChat/issues">Issues</a> •
  <a href="https://github.com/mr-september/MercuryChat/discussions">Discussions</a> •
  <a href="https://github.com/mr-september/MercuryChat/wiki">Wiki</a>
</p>

## Star History
<div align="center">
  <a href="https://www.star-history.com/#mr-september/MercuryChat&Date">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/svg?repos=mr-september/MercuryChat&type=Date&theme=dark" />
      <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/svg?repos=mr-september/MercuryChat&type=Date" />
      <img alt="Star History Chart" src="https://api.star-history.com/svg?repos=mr-september/MercuryChat&type=Date" />
    </picture>
  </a>
</div>
