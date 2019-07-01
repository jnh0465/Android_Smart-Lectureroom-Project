package com.jiwoolee.android_smartlectureroom.FCM

import android.content.Context
import android.os.PowerManager

//푸시올때 화면깨우기

object PushUtils {
    private val TAG = PushUtils::class.java.simpleName

    private var mWakeLock: PowerManager.WakeLock? = null

    fun acquireWakeLock(context: Context) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, "WAKEUP"
        )

        mWakeLock!!.acquire()
    }

    fun releaseWakeLock() {
        if (mWakeLock != null) {
            mWakeLock!!.release()
            mWakeLock = null
        }
    }

}
