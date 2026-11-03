package com.example.bai_2

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigHelper {
    private const val KEY_WELCOME = "welcome_message"

    /** Lấy lời chào từ Remote Config; lỗi mạng thì dùng giá trị mặc định. */
    suspend fun load(): String {
        val rc = FirebaseRemoteConfig.getInstance()
        rc.setConfigSettingsAsync(remoteConfigSettings { minimumFetchIntervalInSeconds = 60 })
        rc.setDefaultsAsync(mapOf(KEY_WELCOME to "Chào mừng bạn đến với UTH Todo!"))
        return try {
            rc.fetchAndActivate().await()
            rc.getString(KEY_WELCOME)
        } catch (e: Exception) {
            rc.getString(KEY_WELCOME)
        }
    }
}
