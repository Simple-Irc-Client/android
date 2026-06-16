// On Android the entry point is `run()` (invoked via the generated JNI glue and
// `#[tauri::mobile_entry_point]`). This `main` only matters for desktop-style
// `cargo run`/debugging of the same crate.
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

fn main() {
    simple_irc_client_android_lib::run()
}
