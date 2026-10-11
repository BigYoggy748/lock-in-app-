package com.lockin.dashboard.data

import android.content.Context
import android.content.SharedPreferences
import com.lockin.dashboard.audio.Soundscape
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class FocusSound(val label: String) {
    ALARM("Alarm Sound"),
    NOTIFICATION("Notification Chime"),
    RINGTONE("Phone Ringtone"),
    CHIME("Soft Bell"),
    NONE("Mute (Vibrate Only)")
}

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("lockin_prefs", Context.MODE_PRIVATE)

    // ── Profile ──────────────────────────────────────────────────────────────
    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "CHAMP") ?: "CHAMP")
    val userName: StateFlow<String> = _userName.asStateFlow()

    // ── XP & Gamification ────────────────────────────────────────────────────
    private val _totalXp = MutableStateFlow(prefs.getInt(KEY_TOTAL_XP, 120))
    val totalXp: StateFlow<Int> = _totalXp.asStateFlow()

    // ── Focus Alert Sound & Vibration ─────────────────────────────────────────
    private val _focusSound = MutableStateFlow(
        try {
            FocusSound.valueOf(prefs.getString(KEY_FOCUS_SOUND, FocusSound.ALARM.name) ?: FocusSound.ALARM.name)
        } catch (e: Exception) {
            FocusSound.ALARM
        }
    )
    val focusSound: StateFlow<FocusSound> = _focusSound.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(prefs.getBoolean(KEY_VIBRATION, true))
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    // ── Ambient Soundscapes ───────────────────────────────────────────────────
    private val _soundscape = MutableStateFlow(
        try {
            Soundscape.valueOf(prefs.getString(KEY_SOUNDSCAPE, Soundscape.NONE.name) ?: Soundscape.NONE.name)
        } catch (e: Exception) {
            Soundscape.NONE
        }
    )
    val soundscape: StateFlow<Soundscape> = _soundscape.asStateFlow()

    private val _soundscapeVolume = MutableStateFlow(prefs.getFloat(KEY_SOUNDSCAPE_VOL, 0.5f))
    val soundscapeVolume: StateFlow<Float> = _soundscapeVolume.asStateFlow()

    fun setUserName(name: String) {
        val trimmed = name.trim().ifEmpty { "CHAMP" }
        prefs.edit().putString(KEY_USER_NAME, trimmed).apply()
        _userName.value = trimmed
    }

    fun addXp(amount: Int) {
        if (amount <= 0) return
        val updated = (_totalXp.value + amount).coerceAtLeast(0)
        prefs.edit().putInt(KEY_TOTAL_XP, updated).apply()
        _totalXp.value = updated
    }

    fun setFocusSound(sound: FocusSound) {
        prefs.edit().putString(KEY_FOCUS_SOUND, sound.name).apply()
        _focusSound.value = sound
    }

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION, enabled).apply()
        _vibrationEnabled.value = enabled
    }

    fun setSoundscape(soundscape: Soundscape) {
        prefs.edit().putString(KEY_SOUNDSCAPE, soundscape.name).apply()
        _soundscape.value = soundscape
    }

    fun setSoundscapeVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        prefs.edit().putFloat(KEY_SOUNDSCAPE_VOL, clamped).apply()
        _soundscapeVolume.value = clamped
    }

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_TOTAL_XP = "total_xp"
        private const val KEY_FOCUS_SOUND = "focus_sound"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_SOUNDSCAPE = "soundscape"
        private const val KEY_SOUNDSCAPE_VOL = "soundscape_vol"

        @Volatile
        private var INSTANCE: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferencesManager(context).also { INSTANCE = it }
            }
        }
    }
}
