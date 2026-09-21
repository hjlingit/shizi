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
import com.example.shiziapp.data.Hanzi
import com.example.shiziapp.data.HanziLib
import com.example.shiziapp.databinding.FragmentMatchingBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * 翻牌配对：3个字，6张卡（每字各一"汉字卡"和一"Emoji卡"），翻对一对即消除。
 */
class MatchingGameFragment : Fragment() {

    private var _binding: FragmentMatchingBinding? = null
    private val binding get() = _binding!!

    /** 卡片数据结构 */
    private data class Card(
        val id: Int,
        val isEmoji: Boolean,
        val ownerChar: String,
        val faceText: String,
        var matched: Boolean = false,
        var flipped: Boolean = false,
        var view: TextView? = null
    )

    private var cards: List<Card> = emptyList()
    private var firstCard: Card? = null
    private var lock = false
    private var pairsFound = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMatchingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.btnNewGame.setOnClickListener { newGame() }
        newGame()
    }

    private fun newGame() {
        val pool = HanziLib.all
        // 抽3个不同字
        val chosen = mutableListOf<Hanzi>()
        while (chosen.size < 3) {
            val c = pool.random()
            if (chosen.none { it.character == c.character }) chosen.add(c)
        }
        // 生成6张卡片
        val cardList = mutableListOf<Card>()
        var id = 0
        chosen.forEach { h ->
            cardList.add(Card(id++, false, h.character, h.character))
            cardList.add(Card(id++, true, h.character, h.emoji))
        }
        cards = cardList.shuffled()
        firstCard = null
        lock = false
        pairsFound = 0
        renderGrid()
        updateScore()
    }

    private fun renderGrid() {
        val container = binding.gridContainer
        container.removeAllViews()
        // 2行 x 3列
        var idx = 0
        for (row in 0 until 2) {
            val rowLayout = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
                )
            }
            for (col in 0 until 3) {
                val card = cards[idx++]
                val tv = makeCardView(card)
                rowLayout.addView(tv)
            }
            container.addView(rowLayout)
        }
    }

    private fun makeCardView(card: Card): TextView {
        return TextView(requireContext()).apply {
            text = "❓"
            textSize = 52f
            gravity = Gravity.CENTER
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundResource(R.drawable.bg_button_purple)
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f)
            lp.setMargins(8, 8, 8, 8)
            layoutParams = lp
            card.view = this
            setOnClickListener { onCardClick(card) }
        }
    }

    private fun flipCard(card: Card, reveal: Boolean) {
        card.flipped = reveal
        val tv = card.view ?: return
        if (reveal && !card.matched) {
            tv.text = card.faceText
            tv.setBackgroundResource(if (card.isEmoji) R.drawable.bg_button_green else R.drawable.bg_button_red)
            // 播字
            (requireActivity() as MainActivity).tts.speak(card.ownerChar)
        } else if (!reveal && !card.matched) {
            tv.text = "❓"
            tv.setBackgroundResource(R.drawable.bg_button_purple)
        } else {
            tv.visibility = View.INVISIBLE
        }
    }

    private fun onCardClick(card: Card) {
        if (lock || card.matched || card.flipped) return
        flipCard(card, true)
        if (firstCard == null) {
            firstCard = card
        } else {
            val f = firstCard!!
            lock = true
            firstCard = null
            if (f.ownerChar == card.ownerChar && f.id != card.id) {
                // 配对成功
                f.matched = true
                card.matched = true
                (requireActivity() as MainActivity).speak("配对成功！${card.ownerChar}")
                pairsFound++
                updateScore()
                if (pairsFound == 3) {
                    binding.btnNewGame.postDelayed({
                        (requireActivity() as MainActivity).speak("太棒啦，全部配完！")
                        viewLifecycleOwner.lifecycleScope.launch {
                            (requireActivity() as MainActivity).progress.addCoins(2)
                        }
                    }, 300)
                }
                // 隐藏已匹配
                f.view?.visibility = View.INVISIBLE
                card.view?.visibility = View.INVISIBLE
                lock = false
            } else {
                // 不匹配：延时翻回
                val firstView = f.view
                val secondView = card.view
                firstView?.postDelayed({
                    flipCard(f, false)
                    flipCard(card, false)
                    lock = false
                }, 700)
            }
        }
    }

    private fun updateScore() {
        binding.tvScoreM.text = "找到 $pairsFound / 3 对"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
