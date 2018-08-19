package top.saplf.clipboard

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.provider.Settings
import android.provider.Settings.Secure.*
import android.support.v4.app.NotificationCompat


data class AppInfo(
  val name: String,
  val packageName: String,
  val versionCode: Int,
  val versionName: String,
  val launcher: Drawable
)

inline val Context.isAccessibilityOn: Boolean
  get() {
    val accEnable = try {
      getInt(contentResolver, ACCESSIBILITY_ENABLED)
    } catch (e: Settings.SettingNotFoundException) {
      e.printStackTrace()
      0
    }

    if (accEnable == 1) {
      val enabledServices = getString(contentResolver, ENABLED_ACCESSIBILITY_SERVICES)
      return enabledServices.toLowerCase().contains(packageName.toLowerCase())
    }

    return false
  }

fun Context.patchAppInfo(packageName: String): AppInfo? =
  try {
    val pm = applicationContext.packageManager
    val packageInfo = pm.getPackageInfo(packageName, 0)
    val appInfo = packageInfo.applicationInfo
    AppInfo(
      name = appInfo.loadLabel(pm).toString(),
      packageName = packageName,
      versionCode = packageInfo.versionCode,
      versionName = packageInfo.versionName,
      launcher = appInfo.loadIcon(pm)
    )
  } catch (e: Exception) {
    e.printStackTrace()
    null
  }

fun Context.showNotification(title: String, content: String, icon: Bitmap?) {
  val notification = createNotification(title, content, icon)
  (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    .notify(0, notification)
}

fun Context.createNotification(title: String, content: String, icon: Bitmap? = null): Notification =
  NotificationCompat.Builder(this, packageName)
    .setContentTitle(title)
    .setLargeIcon(icon)
    .setSmallIcon(R.mipmap.ic_launcher)
    .setContentText(content)
    .setAutoCancel(true)
    .setTicker(content)
    .setDefaults(Notification.DEFAULT_SOUND)
    .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), FLAG_UPDATE_CURRENT))
    .build()

fun Drawable.toBitmap(): Bitmap {
  // 取 drawable 的长宽
  val w = intrinsicWidth
  val h = intrinsicHeight

  // 取 drawable 的颜色格式
  val config = if (opacity != PixelFormat.OPAQUE)
    Bitmap.Config.ARGB_8888
  else
    Bitmap.Config.RGB_565
  // 建立对应 bitmap
  val bitmap = Bitmap.createBitmap(w, h, config)
  // 建立对应 bitmap 的画布
  val canvas = Canvas(bitmap)
  setBounds(0, 0, w, h)
  // 把 drawable 内容画到画布中
  draw(canvas)
  return bitmap
}