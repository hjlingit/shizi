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

/**
 * 听音选字：播放一个字的声音，从三个汉字里选出听到的。
 */
class ListenGameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val pool = HanziLib.all.shuffled()
    private var current: Hanzi? = null
    private var score = 0
    private var answerClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvQuestionHint.text = "👂 点一下听声音，选出听到的字"
        binding.tvQuestionMain.text = "🔊"
        binding.btnNext.visibility = View.GONE
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.tvQuestionMain.setOnClickListener {
            current?.let { (requireActivity() as MainActivity).speak(it.character) }
        }
        binding.btnNext.setOnClickListener { nextQuestion() }
        showScore()
        nextQuestion()
    }

    private fun showScore() {
        binding.tvScore.text = "⭐ $score"
    }

    private fun nextQuestion() {
        val activity = requireActivity() as MainActivity
        answerClicked = false
        binding.btnNext.visibility = View.GONE
        binding.tvFeedback.text = ""

        val target = pool.random()
        val options = mutableListOf(target)
        while (options.size < 3) {
            val c = pool.random()
            if (options.none { it.character == c.character }) options.add(c)
        }
        options.shuffle()
        current = target

        binding.tvQuestionMain.text = "🔊"

        val area = binding.optionsArea
        area.removeAllViews()
        options.forEach { opt ->
            val btn = TextView(requireContext()).apply {
                text = opt.character
                textSize = 60f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFFFFFFFF.toInt())
                setBackgroundResource(R.drawable.bg_button_purple)
                gravity = android.view.Gravity.CENTER
                val lp = LinearLayout.LayoutParams(0, 140, 1f)
                lp.setMargins(8, 0, 8, 0)
                layoutParams = lp
            }
            btn.setOnClickListener { onOptionClick(opt) }
            area.addView(btn)
        }

        binding.tvQuestionMain.postDelayed({
            activity.speak("听一听，这是哪个字？${target.character}")
        }, 300)
    }

    private fun onOptionClick(clicked: Hanzi) {
        if (answerClicked) return
        answerClicked = true
        val activity = requireActivity() as MainActivity
        if (clicked.character == current?.character) {
            binding.tvFeedback.text = "✅ 真棒！答对啦"
            activity.speak("答对啦！${clicked.character}，${clicked.phrase}")
            score++
            showScore()
            if (score % 5 == 0) {
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
