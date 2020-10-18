package com.example.groupchatting

import android.app.Application
import com.onesignal.OneSignal
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application(){
    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init();
    }
}