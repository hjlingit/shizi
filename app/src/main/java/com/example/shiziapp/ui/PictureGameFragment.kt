package com.example.shiziapp.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shiziapp.MainActivity
import com.example.shiziapp.R
import com.example.shiziapp.data.Hanzi
import com.example.shiziapp.data.HanziLib
import com.example.shiziapp.databinding.FragmentGameBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 看图选字：显示Emoji，孩子从三个汉字里选出正确的。
 */
class PictureGameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val pool = HanziLib.all.shuffled()
    private var current: Hanzi? = null
    private var score = 0
    private var answerClicked = false
    private var gameOver = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuestionHint.text = "看看下面的图，点出正确的字"
        binding.btnNext.visibility = View.GONE
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // 点击图片可重复听（播词）
        binding.tvQuestionMain.setOnClickListener {
            current?.let { (requireActivity() as MainActivity).speak(it.phrase) }
        }
        binding.btnNext.setOnClickListener {
            nextQuestion()
        }
        showScore()
        nextQuestion()
    }

    private fun showScore() {
        binding.tvScore.text = "⭐ $score"
    }

    private fun makeOption(char: String): TextView =
        TextView(requireContext()).apply {
            text = char
            textSize = 60f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundResource(R.drawable.bg_button_blue)
            gravity = android.view.Gravity.CENTER
            val lp = LinearLayout.LayoutParams(0, 140, 1f)
            lp.setMargins(8, 0, 8, 0)
            layoutParams = lp
        }

    private fun nextQuestion() {
        val activity = requireActivity() as MainActivity
        answerClicked = false
        binding.btnNext.visibility = View.GONE
        binding.tvFeedback.text = ""

        // 选3个不同字
        var target = pool.random()
        val options = mutableListOf(target)
        while (options.size < 3) {
            val candidate = pool.random()
            if (options.none { it.character == candidate.character }) options.add(candidate)
        }
        options.shuffle()
        current = target

        binding.tvQuestionMain.text = target.emoji
        binding.tvQuestionMain.textSize = 130f

        val area = binding.optionsArea
        area.removeAllViews()
        options.forEach { opt ->
            val btn = makeOption(opt.character)
            btn.setOnClickListener {
                onOptionClick(opt)
            }
            area.addView(btn)
        }
        binding.tvQuestionMain.postDelayed({
            activity.speak("它的字是哪一个呢？")
        }, 200)
    }

    private fun onOptionClick(clicked: Hanzi) {
        if (answerClicked || gameOver) return
        answerClicked = true
        val activity = requireActivity() as MainActivity
        if (clicked.character == current?.character) {
            binding.tvFeedback.text = "✅ 真棒！答对啦"
            activity.speak("太棒了，答对了！${clicked.character}，${clicked.phrase}")
            score++
            showScore()
            if (score % 5 == 0) {
                // 每5题奖励金币
                viewLifecycleOwner.lifecycleScope.launch { activity.progress.addCoins(1) }
            }
        } else {
            binding.tvFeedback.text = "😊 再想想～"
            activity.speak("再想想看哦")
        }
        binding.btnNext.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
