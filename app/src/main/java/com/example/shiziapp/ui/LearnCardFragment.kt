package com.example.shiziapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shiziapp.MainActivity
import com.example.shiziapp.data.Hanzi
import com.example.shiziapp.data.HanziLib
import com.example.shiziapp.data.Stage
import com.example.shiziapp.databinding.FragmentLearnCardBinding
import kotlinx.coroutines.launch

/**
 * 看图识字：逐字学习卡片。
 */
class LearnCardFragment : Fragment() {

    private var _binding: FragmentLearnCardBinding? = null
    private val binding get() = _binding!!

    private lateinit var hanzis: List<Hanzi>
    private var index = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stageId = requireArguments().getInt(ARG_STAGE, 1)
        hanzis = HanziLib.hanziForStage(Stage.fromId(stageId))
        index = 0

        showCurrent()
        setupButtons()
    }

    private fun setupButtons() {
        val activity = requireActivity() as MainActivity
        binding.btnBack.setOnClickListener { activity.onBackPressedDispatcher.onBackPressed() }

        binding.btnReplay.setOnClickListener {
            val h = current()
            activity.tts.speakHanzi(h.character, h.pinyin, h.phrase)
        }

        binding.btnNext.setOnClickListener {
            if (index < hanzis.size - 1) {
                index++
                showCurrent()
            } else {
                activity.speak("本主题全部学完啦，真棒！")
                // 学完整个阶段解锁下一阶段
                viewLifecycleOwner.lifecycleScope.launch { maybeUnlockNext() }
                (activity).supportFragmentManager.popBackStack()
            }
        }

        binding.btnLearned.setOnClickListener {
            activity.speak("太棒了，你学会啦")
            viewLifecycleOwner.lifecycleScope.launch {
                activity.progress.markLearned(current().character)
                activity.progress.addCoins(1)
                maybeUnlockNext()
            }
        }
    }

    /** 每浏览到一个字，自动标记为已学（低门槛） */
    private fun maybeMarkStageLearned() {
        val activity = requireActivity() as MainActivity
        viewLifecycleOwner.lifecycleScope.launch {
            activity.progress.markLearned(current().character)
            maybeUnlockNext()
        }
    }

    /** 若当前阶段已全部掌握，解锁下一阶段 */
    private suspend fun maybeUnlockNext() {
        val activity = requireActivity() as MainActivity
        val stageId = requireArguments().getInt(ARG_STAGE, 1)
        val stage = Stage.fromId(stageId)
        val all = HanziLib.hanziForStage(stage)
        val learned = activity.progress.learnedChars.first().toSet()
        if (all.all { it.character in learned }) {
            activity.progress.unlockStage(stageId + 1)
        }
    }

    private fun current(): Hanzi = hanzis[index]

    private fun showCurrent() {
        val h = current()
        binding.tvEmoji.text = h.emoji
        binding.tvChar.text = h.character
        binding.tvPinyin.text = h.pinyin
        binding.tvPhrase.text = h.phrase
        binding.tvCounter.text = "${index + 1} / ${hanzis.size}"
        // 自动标记已学 + 检查解锁
        maybeMarkStageLearned()
        // 进入时自动朗读
        (requireActivity() as MainActivity).tts.speakHanzi(h.character, h.pinyin, h.phrase)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STAGE = "stage"
        fun newInstance(stageId: Int) = LearnCardFragment().apply {
            arguments = Bundle().apply { putInt(ARG_STAGE, stageId) }
        }
    }
}
