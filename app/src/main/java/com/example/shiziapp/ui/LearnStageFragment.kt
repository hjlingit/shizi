package com.example.shiziapp.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.shiziapp.MainActivity
import com.example.shiziapp.R
import com.example.shiziapp.data.HanziLib
import com.example.shiziapp.data.Stage
import com.example.shiziapp.databinding.FragmentLearnStageBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LearnStageFragment : Fragment() {

    private var _binding: FragmentLearnStageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnStageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildStages()
    }

    private fun buildStages() {
        val container = binding.stageContainer
        container.removeAllViews()
        val activity = requireActivity() as MainActivity

        viewLifecycleOwner.lifecycleScope.launch {
            val unlocked = activity.progress.unlockedStage.first()

            HanziLib.stages.forEach { stage ->
                val isUnlocked = stage.id <= unlocked
                val count = HanziLib.hanziForStage(stage).size

                val card = TextView(requireContext()).apply {
                    text = if (isUnlocked) {
                        "${stage.emoji}  ${stage.name}  （$count 个字）"
                    } else {
                        "🔒  ${stage.name} （要先学之前的哦）"
                    }
                    textSize = 22f
                    typeface = Typeface.DEFAULT_BOLD
                    setTextColor(0xFFFFFFFF.toInt())
                    setBackgroundResource(R.drawable.bg_button_blue)
                    gravity = android.view.Gravity.CENTER
                    setPadding(20, 28, 20, 28)
                    val lp = LinearLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 10, 0, 10)
                    layoutParams = lp
                    isEnabled = isUnlocked
                    alpha = if (isUnlocked) 1f else 0.55f
                }

                if (isUnlocked) {
                    card.setOnClickListener {
                        activity.speak("${stage.name}，开始学习")
                        navigate(LearnCardFragment.newInstance(stage.id))
                    }
                }
                container.addView(card)
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
