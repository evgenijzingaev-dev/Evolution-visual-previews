#!/usr/bin/env bash
set -euo pipefail
APK="zona-web-lite/app/build/outputs/apk/debug/app-debug.apk"
mkdir -p zona-web-lite/screenshots
adb install -r "$APK"
adb shell settings put system accelerometer_rotation 0 || true
adb shell settings put system user_rotation 1 || true
adb shell wm size 1920x1080
adb shell wm density 240
adb shell am start -W -n com.haier.zonaweblite/.MainActivity
sleep 5
adb shell pidof com.haier.zonaweblite
adb exec-out screencap -p > zona-web-lite/screenshots/menu-android7.png
adb shell dumpsys meminfo com.haier.zonaweblite > zona-web-lite/screenshots/menu-memory.txt
python3 - <<'PY'
from pathlib import Path
p=Path("zona-web-lite/screenshots/menu-android7.png")
assert p.read_bytes().startswith(b"\x89PNG\r\n\x1a\n")
assert p.stat().st_size>3000
print("Android 7.1.1 screen captured:", p.stat().st_size, "bytes")
PY
