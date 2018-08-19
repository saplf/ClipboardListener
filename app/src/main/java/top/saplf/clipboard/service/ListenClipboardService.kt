package top.saplf.clipboard.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import top.saplf.clipboard.R
import top.saplf.clipboard.createNotification
import top.saplf.clipboard.showNotification
import top.saplf.clipboard.toBitmap

class ListenClipboardService : Service(), ClipboardManager.OnPrimaryClipChangedListener {

  private val clipboardManager by lazy {
    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
  }

  override fun onCreate() {
    super.onCreate()
    clipboardManager.addPrimaryClipChangedListener(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        .createNotificationChannel(NotificationChannel(packageName, "Default", NotificationManager.IMPORTANCE_DEFAULT))
    }
    val title = getString(R.string.notification_title_none)
    val content = getString(R.string.notification_content_none)
    startForeground(1, createNotification(title, content))
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    if (intent != null) {

    }
    return super.onStartCommand(intent, flags, startId)
  }

  override fun onBind(intent: Intent?): IBinder? {
    return null
  }

  override fun onDestroy() {
    super.onDestroy()
    clipboardManager.removePrimaryClipChangedListener(this)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      stopForeground(Service.STOP_FOREGROUND_REMOVE)
    }
  }

  override fun onPrimaryClipChanged() {
    val data = clipboardManager.primaryClip
    if (data === null) return

    val desc = data.description
    if (desc === null) return

    if (data.itemCount == 0 || desc.mimeTypeCount == 0) return

    DetectionService.currentApp?.let {
      val title = String.format(getString(R.string.notification_title), it.name)
      val content = String.format(getString(R.string.notification_content), data.getItemAt(0).text)
      showNotification(title, content, it.launcher.toBitmap())
    }
  }
}
