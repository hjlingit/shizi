package com.example.shiziapp.ui

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.shiziapp.MainActivity
import com.example.shiziapp.R
import com.example.shiziapp.databinding.FragmentGameHomeBinding

class GameHomeFragment : Fragment() {

    private var _binding: FragmentGameHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        val container = binding.gameContainer

        fun addGame(emoji: String, name: String, colorRes: Int, target: Fragment) {
            val btn = TextView(requireContext()).apply {
                text = "$emoji\n$name"
                textSize = 24f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(0xFFFFFFFF.toInt())
                setBackgroundResource(colorRes)
                gravity = Gravity.CENTER
                setPadding(12, 30, 12, 30)
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
                )
                lp.setMargins(0, 8, 0, 8)
                layoutParams = lp
            }
            btn.setOnClickListener {
                activity.speak(name)
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, target)
                    .addToBackStack(null)
                    .commit()
            }
            container.addView(btn)
        }

        addGame("🃏", "翻牌配对", R.drawable.bg_button_red, MatchingGameFragment())
        addGame("👂", "听音选字", R.drawable.bg_button_blue, ListenGameFragment())
        addGame("🖼️", "看图选字", R.drawable.bg_button_green, PictureGameFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
