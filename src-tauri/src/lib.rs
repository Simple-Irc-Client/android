mod irc;

use irc::commands::{irc_connect, irc_disconnect, irc_quit, irc_send};
use irc::state::IrcState;

/// Mobile entry point. Tauri's Android template calls this from the generated
/// JNI glue; the `#[tauri::mobile_entry_point]` attribute exports it under the
/// symbol the Kotlin side loads.
///
/// This mirrors the desktop builder (`desktop/src-tauri/src/lib.rs`) minus the
/// updater plugin — Android updates ship through the Play Store / APK, not
/// `tauri-plugin-updater`.
#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_clipboard_manager::init())
        .plugin(tauri_plugin_notification::init())
        .plugin(tauri_plugin_opener::init())
        .manage(IrcState::new())
        .invoke_handler(tauri::generate_handler![
            irc_connect,
            irc_send,
            irc_quit,
            irc_disconnect,
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
