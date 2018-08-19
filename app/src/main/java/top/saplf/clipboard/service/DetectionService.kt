package top.saplf.clipboard.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import top.saplf.clipboard.AppInfo
import top.saplf.clipboard.patchAppInfo

class DetectionService : AccessibilityService() {
  override fun onInterrupt() {
  }

  override fun onAccessibilityEvent(event: AccessibilityEvent) {
    if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
      try {
        val pn = event.packageName.toString()
        // if there's no such package, goto catch
        applicationContext.packageManager.getPackageInfo(pn, 0)
        currentApp = patchAppInfo(pn)
      } catch (e: Exception) {
      }
    }
  }

  companion object {
    @Volatile var currentApp: AppInfo? = null
  }
}