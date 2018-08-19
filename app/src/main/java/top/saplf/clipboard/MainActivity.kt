package top.saplf.clipboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import top.saplf.clipboard.service.ListenClipboardService

class MainActivity : AppCompatActivity() {

  private val permissionAlert by lazy {
    AlertDialog.Builder(this)
      .setTitle(R.string.dlg_title)
      .setMessage(R.string.dlg_permission)
      .setPositiveButton(R.string.dlg_ok) { _, _ ->
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
      }
      .setNegativeButton(R.string.dlg_cancel) { _, _ ->
        finish()
      }
      .setCancelable(false)
      .create()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    if (!isAccessibilityOn) {
      permissionAlert.show()
    }
    startService(Intent(this, ListenClipboardService::class.java))

    textView.setOnClickListener {
      stopService(Intent(this, ListenClipboardService::class.java))
      finish()
    }
  }

  override fun onResume() {
    super.onResume()

    if (!isAccessibilityOn) {
      permissionAlert.show()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (!isAccessibilityOn) {
      stopService(Intent(this, ListenClipboardService::class.java))
    }
  }
}
