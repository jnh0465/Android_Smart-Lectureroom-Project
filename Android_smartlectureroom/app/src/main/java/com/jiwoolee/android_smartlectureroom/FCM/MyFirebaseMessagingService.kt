package com.jiwoolee.android_smartlectureroom.FCM

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jiwoolee.android_smartlectureroom.view.main.FragmentActivity
import com.jiwoolee.android_smartlectureroom.R
import java.net.URL

//fcm 푸시

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var bigPicture: Bitmap //비트맵

    override fun onNewToken(mToken: String?) {     //토큰
        super.onNewToken(mToken)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        PushUtils.acquireWakeLock(this) //푸시알림시 화면깨우기

        when {
            remoteMessage!!.data["imageurl"] == null -> sendNotificationAlert(remoteMessage.data["title"], remoteMessage.data["body"]) //node 서버에서 온 imageurl값이 null이면 알림으로 인식, sendNotification_alert로
            else -> sendNotification(remoteMessage.data["title"], remoteMessage.data["imageurl"])                                       //imageurl값이 있으면 광고로 인식, sendNotification_ad로
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult -> val deviceToken = instanceIdResult.token }
    }

    private fun sendNotificationAlert(title: String?, body: String?) {
        PushUtils.releaseWakeLock() //푸시알림시 화면깨우기
        val intent = Intent(this, FragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelId = getString(R.string.default_notification_channel_id)                                                       //이제 Builder사용할 때 channelId필요, strings.xml에 저장되어있음
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("오투미_$title")                                               //타이틀
                .setContentText(body)                                                           //텍스트
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setVibrate(longArrayOf(100, 0, 100, 0))
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {                                          //오레오부터 채널필요
            val channelName = getString(R.string.default_notification_channel_name)                                                        //strings.xml에 저장되어있음
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)                                                    //진동
            channel.vibrationPattern = longArrayOf(100, 200, 100, 200)                                 //진동간격설정
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    private fun sendNotification(title: String?, myimg: String?) {
        PushUtils.releaseWakeLock() //화면깨우기
        val intent = Intent(this, FragmentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT)

        try {
            val url = URL(myimg)
            bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream())              //이미지 비트맵으로 변환
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val channelId = getString(R.string.default_notification_channel_id)                          //이제 Builder사용할 때 channelId필요, strings.xml에 저장되어있음
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("오투미_$title")                                               //타이틀
                .setContentText("두 손가락을 이용해 아래로 당겨주세요")                         //텍스트
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setVibrate(longArrayOf(100, 0, 100, 0))

                .setStyle(NotificationCompat.BigPictureStyle()                              //손가락으로 내렸을 때 화면
                        .bigPicture(bigPicture)                                                 //비트맵이미지
                        .setBigContentTitle("오투미_$title")                                    //타이틀
                        .setSummaryText("* 수신거부 : 설정>알림거부"))                          //텍스트
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {                                           //오레오부터 채널필요
            val channelName = getString(R.string.default_notification_channel_name)                 //strings.xml에 저장되어있음
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            channel.enableVibration(true)                                                              //진동
            channel.vibrationPattern = longArrayOf(100, 200, 100, 200)                                //진동간격설정
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private val TAG = "MyFirebaseMsgService"
    }
}