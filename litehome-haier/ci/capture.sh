#!/usr/bin/env bash
set -euo pipefail
mkdir -p litehome-haier/screenshots
APK="litehome-haier/android/app/build/outputs/apk/debug/app-debug.apk"
adb install -r "$APK"
# Force a TV-like 16:9 landscape screen before launching the app.
adb shell settings put system accelerometer_rotation 0 || true
adb shell settings put system user_rotation 1 || true
adb shell wm size 1920x1080
adb shell wm density 240
adb shell am force-stop com.litehome.haierlite || true
adb shell am start -W -n com.litehome.haierlite/.MainActivity
sleep 5
adb exec-out screencap -p > litehome-haier/screenshots/home-android-7.1.png
adb shell am force-stop com.litehome.haierlite
adb shell am start -W -n com.litehome.haierlite/.MainActivity --es screen settings
sleep 4
adb exec-out screencap -p > litehome-haier/screenshots/settings-android-7.1.png
# Fail if the emulator did not produce actual PNG captures.
python3 - <<'PY'
import pathlib
for file in sorted(pathlib.Path("litehome-haier/screenshots").glob("*.png")):
    data=file.read_bytes()
    assert data[:8]==b"\x89PNG\r\n\x1a\n", f"Not a PNG: {file}"
    assert len(data)>8000, f"Unexpected empty screenshot: {file}"
    print(file, len(data), "bytes")
assert len(list(pathlib.Path("litehome-haier/screenshots").glob("*.png")))==2
PY
