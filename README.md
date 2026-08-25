# BiliReward

Bilibili rewarded-ad bypass module for LSPosed.

Auto-skip Bilibili's "watch ad to claim reward" screens in mini-games, novels, etc.
Works by discovering the reward-ad Activity via type signatures, so it adapts to
different Bilibili versions without needing code changes.

## Supported Versions

- **8.49.0 ~ 9.0.0+** (and any future release that keeps the RewardAdListener field)

## How It Works

The module scans for an Activity that holds a `RewardAdListener` field, then:

1. Sets all boolean fields to `true` after `onCreate`
2. Returns `true` for every no-arg boolean method (the gate checks)
3. Forces all boolean arguments to `true` in methods taking `RewardAdCloseFrom`
4. Sets all boolean fields to `true` on `finish()`
5. Actively invokes the close/grant method and immediately finishes the ad
   activity, so the ad never becomes visible and the reward is still granted.

No hardcoded class/method/field names — relies entirely on type signatures,
so it survives obfuscation changes between releases.

## Build

```
javac -source 8 -target 8 \
  -bootclasspath <android.jar> \
  -cp stub \
  -d classes \
  app/src/main/java/io/nebula/bilireward/BiliReward.java

d8 --release --min-api 26 \
  --lib <android.jar> \
  --output . \
  classes/**/*.class
```

Then assemble the APK with `aapt2`, `zipalign`, and `apksigner`.

## Install

1. Install the APK in LSPosed
2. Enable the module, scope to `tv.danmaku.bili`
3. Force stop Bilibili and relaunch