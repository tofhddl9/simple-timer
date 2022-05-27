package com.lgtm.simple_timer.page.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lgtm.simple_timer.R
import com.lgtm.simple_timer.databinding.FragmentTimerBinding
import com.lgtm.simple_timer.delegate.viewBinding
import com.lgtm.simple_timer.page.setting.SettingSharedPreference
import com.lgtm.simple_timer.utils.showSnackBar
import com.lgtm.simple_timer.utils.vibrator
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerFragment: Fragment(R.layout.fragment_timer) {

    private val binding: FragmentTimerBinding by viewBinding(FragmentTimerBinding::bind)

    private val viewModel: TimerViewModel by viewModels()

    private val settingSharedPreferences by lazy { SettingSharedPreference(requireContext()) }

    private val mediaPlayer = MediaPlayer()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()

        setListeners()

        observeViewModel()
    }

    private fun initViews() {
        initToolbar()
        initProgressBar()
    }

    private fun initToolbar() = with(binding.toolbar) {
        inflateMenu(R.menu.menu_home)
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    moveToSetting()
                    return@setOnMenuItemClickListener true
                }
                R.id.menu_restart -> {
                    viewModel.onEvent(TimerEvent.ClickRestart)
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun initProgressBar() = with(binding) {
        progressBarTimer.setTimerTouchListener(::onDialTouched)
    }

    private fun onDialTouched(dialTouchInfo: DialTouchInfo) {
        viewModel.onEvent(TimerEvent.TouchDial(dialTouchInfo))
    }

    private fun setListeners() = with(binding) {
        remainTimeView.setOnClickListener {
            viewModel.onEvent(TimerEvent.ClickStartOrPause)
        }

        restartButton.setOnClickListener {
            viewModel.onEvent(TimerEvent.ClickRestart)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.remainTimeView.text = uiState.remainTime.toTimerFormat()
                    binding.progressBarTimer.updateTimer(uiState.angle, uiState.progress.toInt())
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.distinctUntilChanged { old, new ->
                    old.state == new.state
                }.collect { uiState ->
                    when (uiState.state) {
                        TimerState.Init -> {

                        }
                        TimerState.Running -> {
                            onTimerRunning(uiState.remainTime)
                        }
                        TimerState.Paused -> {
                            onTimerPaused()
                        }
                        TimerState.Finished -> {
                            onTimerFinished(uiState.restartTime)
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessageFlow.collect { errorMsg ->
                    showSnackBar(errorMsg.asString(requireContext()))
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        if (settingSharedPreferences.doesAlwaysOnDisplay) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun onTimerRunning(remainTime: Long) {
        showRunningUi()
        setAlarm(remainTime)

        stopRingAndVibrate()
    }

    private fun onTimerPaused() {
        cancelAlarm()
    }

    private fun onTimerFinished(restartTime: Long) {
        showFinishedUi(restartTime)

        startRingAndVibrate()
    }

    private fun startRingAndVibrate() {
        if (settingSharedPreferences.doesRing) {
            var ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(requireContext(), RingtoneManager.TYPE_RINGTONE)
            settingSharedPreferences.ringtone?.let {
                ringtoneUri = Uri.parse(it)
            }

            mediaPlayer.apply {
                setDataSource(requireContext(), ringtoneUri)
                setAudioStreamType(AudioManager.STREAM_RING)
                isLooping = settingSharedPreferences.doesRingInfinitely
                prepare()
            }.also {
                it.start()
            }
        }

        if (settingSharedPreferences.doesVibrate) {
            val pattern = longArrayOf(400, 500)
            requireContext().vibrator.vibrate(pattern, 0)
        }
    }

    private fun stopRingAndVibrate() {
        mediaPlayer.stop()
        mediaPlayer.reset()

        if (settingSharedPreferences.doesVibrate) {
            requireContext().vibrator.cancel()
        }
    }


    private fun moveToSetting() {
        findNavController().navigate(TimerFragmentDirections.actionCompassFragmentToMapFragment())
    }

    private fun showRunningUi() = with(binding) {
        restartButton.isVisible = false

        remainTimeView.isVisible = true
        progressBarTimer.isVisible = true
    }

    private fun showFinishedUi(settingTime: Long) = with(binding) {
        remainTimeView.isVisible = false
        progressBarTimer.isVisible = false

        restartButton.isVisible = true
        restartButton.text = settingTime.toTimerFormat()
    }

    private fun setAlarm(remainTime: Long) {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            Calendar.getInstance().timeInMillis + remainTime,
            pendingIntent
        )
    }

    private fun cancelAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 100, intent, PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel()
    }

}

private fun Long.toTimerFormat(): String {
    val remainSec = this / 1_000L
    val minute = remainSec / 60
    val sec = remainSec % 60

    return "${minute.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}"
}
