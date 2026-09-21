package com.example.shiziapp.ui

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shiziapp.MainActivity
import com.example.shiziapp.R
import com.example.shiziapp.data.HanziLib
import com.example.shiziapp.data.Stage
import com.example.shiziapp.databinding.FragmentProgressBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 进度/奖章页：展示已学字数、每个阶段的完成情况。
 */
class ProgressFragment : Fragment() {

    private var _binding: FragmentProgressBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProgressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        binding.btnBackP.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val learned = activity.progress.learnedChars.first().toSet()
            val unlocked = activity.progress.unlockedStage.first()
            binding.tvStat.text = "已认识 ${learned.size} 个字 🎉"

            val container = binding.stageProgressContainer
            container.removeAllViews()
            HanziLib.stages.forEach { stage ->
                val total = HanziLib.hanziForStage(stage).size
                val done = HanziLib.hanziForStage(stage).count { it.character in learned }
                val isUnlocked = stage.id <= unlocked

                val row = TextView(requireContext()).apply {
                    val status = if (!isUnlocked) "🔒"
                    else if (done == total) "🏅"
                    else "⭐"
                    text = "$status ${stage.emoji} ${stage.name}   $done / $total 个字"
                    textSize = 20f
                    setTextColor(0xFF4A4A4A.toInt())
                    setBackgroundResource(R.drawable.bg_card)
                    setPadding(20, 20, 20, 20)
                    gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 8, 0, 8)
                    layoutParams = lp
                }
                container.addView(row)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
