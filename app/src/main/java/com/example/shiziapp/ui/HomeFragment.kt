package com.example.shiziapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shiziapp.MainActivity
import com.example.shiziapp.R
import com.example.shiziapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity

        // 大兔子点击播放发音
        binding.bigRabbit.setOnClickListener { activity.speak("嗨，我们一起来学汉字吧") }

        binding.btnLearn.setOnClickListener { navigate(LearnStageFragment()) }
        binding.btnPlay.setOnClickListener { navigate(GameHomeFragment()) }
        binding.btnRabbit.setOnClickListener { navigate(RabbitFragment()) }
        binding.btnProgress.setOnClickListener { navigate(ProgressFragment()) }

        // 展示进度
        viewLifecycleOwner.lifecycleScope.launch {
            activity.progress.learnedChars.collect { learned ->
                binding.tvLearnedCount.text = "已经认识 ${learned.size} 个字啦"
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            activity.progress.coins.collect { coins ->
                binding.tvCoins.text = "🪙 金币：$coins"
            }
        }
    }

    private fun navigate(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
