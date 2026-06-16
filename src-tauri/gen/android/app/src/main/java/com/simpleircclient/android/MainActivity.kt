package com.simpleircclient.android

import android.os.Bundle
import androidx.activity.enableEdgeToEdge

class MainActivity : TauriActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    // Keep the process (and thus the IRC connection) alive while backgrounded.
    ConnectionService.start(this)
  }

  override fun onDestroy() {
    ConnectionService.stop(this)
    super.onDestroy()
  }
}
