package com.lockin.dashboard.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lockin.dashboard.audio.AmbientSoundEngine
import com.lockin.dashboard.audio.Soundscape
import com.lockin.dashboard.data.FocusSound
import com.lockin.dashboard.data.PreferencesManager
import com.lockin.dashboard.data.db.LockInDatabase
import com.lockin.dashboard.data.model.FocusSession
import com.lockin.dashboard.data.repository.LockInRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

enum class TimerState { IDLE, RUNNING, PAUSED, FINISHED }

class FocusViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LockInDatabase.getDatabase(application)
    private val repository = LockInRepository(db.dayLogDao(), db.taskDao(), db.focusSessionDao(), db.noteDao())
    private val preferencesManager = PreferencesManager.getInstance(application)

    private val todayKey = LocalDate.now().toString()

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _selectedMinutes = MutableStateFlow(25)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(25 * 60)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    val sessions: StateFlow<List<FocusSession>> = repository.getFocusSessions(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val focusSound: StateFlow<FocusSound> = preferencesManager.focusSound
    val vibrationEnabled: StateFlow<Boolean> = preferencesManager.vibrationEnabled
    val soundscape: StateFlow<Soundscape> = preferencesManager.soundscape
    val soundscapeVolume: StateFlow<Float> = preferencesManager.soundscapeVolume

    private var countDownTimer: CountDownTimer? = null
    private var currentRingtone: Ringtone? = null

    fun selectDuration(minutes: Int) {
        if (_timerState.value == TimerState.IDLE || _timerState.value == TimerState.FINISHED) {
            _selectedMinutes.value = minutes
            _remainingSeconds.value = minutes * 60
            _timerState.value = TimerState.IDLE
        }
    }

    fun setFocusSound(sound: FocusSound) {
        preferencesManager.setFocusSound(sound)
    }

    fun setVibrationEnabled(enabled: Boolean) {
        preferencesManager.setVibrationEnabled(enabled)
    }

    fun setSoundscape(soundscape: Soundscape) {
        preferencesManager.setSoundscape(soundscape)
        if (_timerState.value == TimerState.RUNNING) {
            AmbientSoundEngine.start(soundscape, soundscapeVolume.value)
        }
    }

    fun setSoundscapeVolume(vol: Float) {
        preferencesManager.setSoundscapeVolume(vol)
        AmbientSoundEngine.setVolume(vol)
    }

    fun previewSound(sound: FocusSound) {
        stopRingtone()
        playSoundFor(sound)
    }

    fun stopSoundPreview() {
        stopRingtone()
    }

    fun start() {
        if (_timerState.value == TimerState.RUNNING) return
        stopRingtone()
        _timerState.value = TimerState.RUNNING

        // Start ambient soundscape if chosen
        if (soundscape.value != Soundscape.NONE) {
            AmbientSoundEngine.start(soundscape.value, soundscapeVolume.value)
        }

        val millis = _remainingSeconds.value * 1000L
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(millis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingSeconds.value = (millisUntilFinished / 1000).toInt()
            }
            override fun onFinish() {
                _remainingSeconds.value = 0
                _timerState.value = TimerState.FINISHED
                AmbientSoundEngine.stop()
                onTimerCompleted()
                saveSession()
            }
        }.start()
    }

    fun pause() {
        countDownTimer?.cancel()
        AmbientSoundEngine.stop()
        _timerState.value = TimerState.PAUSED
    }

    fun togglePlayPause() {
        if (_timerState.value == TimerState.RUNNING) {
            pause()
        } else {
            start()
        }
    }

    fun reset() {
        countDownTimer?.cancel()
        AmbientSoundEngine.stop()
        stopRingtone()
        _remainingSeconds.value = _selectedMinutes.value * 60
        _timerState.value = TimerState.IDLE
    }

    private fun onTimerCompleted() {
        // 1. Trigger Vibration if enabled (Default: ON)
        if (vibrationEnabled.value) {
            triggerVibration()
        }

        // 2. Play Selected Sound / Ringtone
        playSoundFor(focusSound.value)

        // 3. Award XP for finishing focus round (+10 XP per minute)
        preferencesManager.addXp(_selectedMinutes.value * 10)
    }

    private fun triggerVibration() {
        try {
            val context = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 400, 200, 400, 200, 600)
                    val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 400, 200, 400, 200, 600), -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playSoundFor(sound: FocusSound) {
        if (sound == FocusSound.NONE) return
        try {
            val context = getApplication<Application>()
            val soundType = when (sound) {
                FocusSound.ALARM -> RingtoneManager.TYPE_ALARM
                FocusSound.NOTIFICATION, FocusSound.CHIME -> RingtoneManager.TYPE_NOTIFICATION
                FocusSound.RINGTONE -> RingtoneManager.TYPE_RINGTONE
                FocusSound.NONE -> return
            }

            val uri = RingtoneManager.getDefaultUri(soundType)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            uri?.let {
                val ringtone = RingtoneManager.getRingtone(context, it)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ringtone.audioAttributes = AudioAttributes.Builder()
                        .setUsage(
                            if (sound == FocusSound.ALARM) AudioAttributes.USAGE_ALARM
                            else AudioAttributes.USAGE_NOTIFICATION
                        )
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                }
                currentRingtone = ringtone
                ringtone.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopRingtone() {
        try {
            currentRingtone?.let {
                if (it.isPlaying) it.stop()
            }
            currentRingtone = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveSession() {
        viewModelScope.launch {
            repository.saveFocusSession(
                FocusSession(
                    id = UUID.randomUUID().toString(),
                    dateKey = todayKey,
                    durationMinutes = _selectedMinutes.value
                )
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        AmbientSoundEngine.stop()
        stopRingtone()
    }
}
