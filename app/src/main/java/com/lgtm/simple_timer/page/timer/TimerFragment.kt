package com.lgtm.simple_timer.page.timer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.lgtm.simple_timer.R
import com.lgtm.simple_timer.databinding.FragmentTimerBinding
import com.lgtm.simple_timer.delegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TimerFragment: Fragment(R.layout.fragment_timer) {

    private val binding: FragmentTimerBinding by viewBinding(FragmentTimerBinding::bind)

    private val viewModel : TimerViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        setListeners()

        observeViewModel()
    }

    private fun initToolbar() = with(binding.toolbar) {
        inflateMenu(R.menu.menu_home)
        setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_setting -> {
                    moveToSetting()
                    return@setOnMenuItemClickListener true
                }
                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
    }

    private fun setListeners() {

    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    // 실제로는 TimerView에 uiState.remainTime 넘겨주기

                    binding.textView.text = uiState.remainTime.toString()
                }
            }
        }
    }

   private fun moveToSetting() {
        findNavController().navigate(TimerFragmentDirections.actionCompassFragmentToMapFragment())
    }

}

// 1시간 = 60분 = 3600초 ... 360도
// 중심을 원점으로하는 좌표평면으로 생각하면, 사분면마다 터치의 x,y 방향에 대해 프로그레스 진행 방향은 결정적.
// |dx|+|dy|의 단위에 따라 시간을 늘리거나 줄이면 되는데, 현재 시각에따라 몇 틱씩 늘리거나 줄일지를 결정하자.
//   ex. 초단위로 볼떄, 1000 이하면 단위는 30, 500이하면 10, 60이하면 1 이런식...
// 프로그레스바의 상태를 잘 정의해보자.

// M0 : 일단은 토글, 리셋 버튼만 있는 5초짜리 틱계산기 만들기
//    - AlarmManager 만들기
// M1 : 프로그레스바 붙이기
//    - 뷰 스펙부터 잘 짜고 코드 작업 시작하자.


/**
 * uiState
 *   - remainTimeInSec: Long
 *   - state: State {INIT, PROGRESS, PAUSE, FIN}
 *   - settingTime: Long ... 다시하기 버튼 추가
 * 남은 시간에 따라 결정되는 프로그레스의 진척도, 색상 등은 뷰가 갖되 갈아끼울 수 있게 델리게이션을 통해 가져볼까
 **  - 시간에 따라, 뷰의 모습은 결정적
 *
 * 시나리오
 * 1. 뷰의 여러가지 이벤트를 프레그먼트가 콜백으로 받아서 ViewModel에 Event 로 전달
 *   - 터치가 시작되는 이벤트
     - 터치가 이어지는 이벤트
     - 터치를 놓아서 결정된 이벤트
     - 시작 버튼 클릭
     - 리셋 버튼 클릭
   2. 알람이 시작되면 1초마다 1틱을 발생시켜 uiState를 갱신.
     - 타이머는 뷰모델이 갖고? 한 틱마다 uiState를 갱신?
     - 남은 시간이 0초가 되면 state같은것도 갱신하면 되겠다.
     - 뷰는 단지, uiState에 따라 갱신되는데, 이때 델리게이션을 통해 어떻게 그릴지 결정되는 형태.
 *
 * */