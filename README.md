# Simple Irc Client — Android

Tauri 2 Android shell for [Simple Irc Client](../). It reuses the canonical
frontend (`../core`) and the Rust IRC transport crate (`../network-rs`, `sic-irc`)
exactly as the desktop app does — see `../PROJECT_STRUCTURE.md`.

## Architecture

- **UI:** `../core` (React + TS + Vite). The kernel is transport-agnostic;
  `core/src/network/irc/transport.ts` auto-selects the Tauri transport inside a
  Tauri webview (Android included).
- **Transport:** `../network-rs` (`sic-irc`) via the thin Tauri glue in
  `src-tauri/src/irc/` (shared verbatim with `desktop/src-tauri/src/irc/`).
- No gateway dependency — connects directly to IRC over TCP/TLS.

## Prerequisites

- Rust + the Android targets:
  `aarch64-linux-android armv7-linux-androideabi i686-linux-android x86_64-linux-android`
- Android SDK + NDK, with `ANDROID_HOME` and `NDK_HOME` exported
- JDK 17+

## Build & run

```sh
pnpm install
pnpm run core:build        # build ../core into ../core/dist
pnpm run android:init      # one-time: generate src-tauri/gen/android
pnpm run android:dev       # run on emulator/device
pnpm run android:build     # release AAB/APK
```
