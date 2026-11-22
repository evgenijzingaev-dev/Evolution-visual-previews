# Haier LE43K6500SA — Android 7.1.1 emulator for Windows

Windows installer package is supplied directly as **Haier-LE43K6500SA-Emulator-Setup-Windows.zip** in the associated ChatGPT conversation. This repository stores its hardware parameters and documentation.

## Hardware profile

| Component | Real TV | AVD config |
| --- | --- | --- |
| SoC | Realtek RTD2841, four ARM Cortex-A53 cores | 4 *virtual x86* CPU cores |
| RAM | DDR3, approximately 1 GB | 1024 MB |
| Screen | FullHD 1920×1080 | 1920×1080 |
| Operating system | Haier Smart OS / Android 7.1 | Android 7.1.1 (API 25, Google APIs x86) |
| Storage | 8 GB eMMC incl. system | 6 GB userdata to approximate remaining capacity |
| GPU | Mali-450 MP2 | Host GPU / emulator, **not a GPU simulation** |

Profile overrides: [AVD-Haier-Hardware.ini](./AVD-Haier-Hardware.ini).

## Usage

Extract the Windows ZIP provided in chat, then run:

1. **Setup-Haier.bat**: installs official Android command-line tools, API 25 system image, creates an AVD and applies these hardware values. It asks users to accept Google licenses explicitly.
2. **Run-Haier.bat**: opens the Android emulator.
3. **Check-Haier.bat**: shows configured CPU/RAM and actual Android-reported CPU cores, memory and display.
4. **Install-Zona-Lite.bat**: installs the earlier test APK into the AVD; does not touch physical TV.
5. **Screenshot-Haier.bat**: saves a real emulator screenshot to a PNG.

Prerequisites: Windows 10/11 x64, virtualization enabled in BIOS, Windows Hypervisor Platform, Java 17+ or Android Studio, free disk space and internet access for official Android SDK downloads.

### Limits

This is **not an exact Haier emulator**: it does not execute the stock Haier firmware and cannot match hardware decoding/CPU performance, TV tuner drivers, or RAM usage of the real device. An APK working here might still crash on the TV. The Android Emulator's newer versions may have constraints on 1024 MB RAM; `Check-Haier.bat` reports the actual Android RAM after boot.

Sources: [Android SDK](https://developer.android.com/studio), [AVD Manager](https://developer.android.com/tools/avdmanager), [Android Emulator CLI](https://developer.android.com/studio/run/emulator-commandline).