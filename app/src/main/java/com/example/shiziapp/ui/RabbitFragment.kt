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
import com.example.shiziapp.data.RabbitItem
import com.example.shiziapp.data.RabbitItems
import com.example.shiziapp.databinding.FragmentRabbitBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 小兔子：展示装备后的兔子、开盲盒、进入商店、管理衣柜。
 */
class RabbitFragment : Fragment() {

    private var _binding: FragmentRabbitBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRabbitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity

        binding.btnBack.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }

        // 金币
        viewLifecycleOwner.lifecycleScope.launch {
            activity.progress.coins.collect { coins ->
                binding.tvCoinsR.text = "🪙 金币：$coins"
            }
        }

        // 开盲盒：消耗30金币，随机获得一件新装扮
        binding.btnGacha.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val ok = activity.progress.spendCoins(30)
                if (!ok) {
                    activity.speak("金币不够啦，先去学字赚钱吧")
                    return@launch
                }
                val owned = activity.progress.ownedItems.first().toMutableSet()
                val available = RabbitItems.gachaPool.filter { it.id !in owned }
                if (available.isEmpty()) {
                    activity.speak("你已经收集到所有装扮啦，好厉害！")
                    // 退钱
                    activity.progress.addCoins(30)
                    return@launch
                }
                val prize = available.random()
                activity.progress.addOwnedItem(prize.id)
                activity.progress.equip(prize.id)
                activity.speak("哇！抽到了${prize.name}，${prize.emoji}")
                refresh()
            }
        }

        binding.btnShop.setOnClickListener {
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, ShopFragment())
                .addToBackStack(null)
                .commit()
        }

        refresh()
    }

    private fun refresh() {
        val activity = requireActivity() as MainActivity
        refreshRabbit()
        refreshWardrobe()
    }

    private fun refreshRabbit() {
        viewLifecycleOwner.lifecycleScope.launch {
            val equipped = (requireActivity() as MainActivity).progress.equippedItems.first()
            val items = equipped.mapNotNull { RabbitItems.itemById(it) }

            // 分槽位展示
            val hat = items.firstOrNull { it.category == com.example.shiziapp.data.ItemCategory.HAT }
            val clothes = items.firstOrNull { it.category == com.example.shiziapp.data.ItemCategory.CLOTHES }
            val shoes = items.firstOrNull { it.category == com.example.shiziapp.data.ItemCategory.SHOES }
            val acc = items.firstOrNull { it.category == com.example.shiziapp.data.ItemCategory.ACCESSORY }

            binding.rabbitArea.removeAllViews()

            fun line(text: String, size: Float): TextView {
                return TextView(requireContext()).apply {
                    this.text = text
                    this.textSize = size
                    gravity = Gravity.CENTER
                }
            }

            binding.rabbitArea.addView(line("✨ ${hat?.emoji ?: ""}${acc?.emoji ?: ""} ✨", 46f))
            binding.rabbitArea.addView(line("${clothes?.emoji ?: ""}  🐰  ${clothes?.emoji ?: ""}", 90f))
            binding.rabbitArea.addView(line("${shoes?.emoji ?: ""}  👟  ${shoes?.emoji ?: ""}", 40f))
        }
    }

    private fun refreshWardrobe() {
        viewLifecycleOwner.lifecycleScope.launch {
            val activity = requireActivity() as MainActivity
            val owned = activity.progress.ownedItems.first()
            val equipped = activity.progress.equippedItems.first()
            val container = binding.wardrobeContainer
            container.removeAllViews()

            // 按类别分组展示
            val grouped = RabbitItems.allItems.groupBy { it.category }
            grouped.forEach { (category, items) ->
                val title = TextView(requireContext()).apply {
                    text = "${category.emoji} ${category.label}"
                    textSize = 20f
                    setTextColor(0xFF4A4A4A.toInt())
                }
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 12, 0, 4)
                container.addView(title, lp)

                val row = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                container.addView(row)

                val categoryOwned = items.filter { it.id in owned }
                if (categoryOwned.isEmpty()) {
                    val empty = TextView(requireContext()).apply {
                        text = "（还没有，去开盲盒吧）"
                        textSize = 16f
                        setTextColor(0xFFAAAAAA.toInt())
                    }
                    row.addView(empty)
                } else {
                    categoryOwned.forEach { item ->
                        val isEquipped = item.id in equipped
                        val tv = TextView(requireContext()).apply {
                            text = "${item.emoji}\n${item.name}"
                            textSize = 20f
                            gravity = Gravity.CENTER
                            setBackgroundResource(R.drawable.bg_card)
                            setPadding(4, 10, 4, 10)
                            val lpl = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                            lpl.setMargins(4, 4, 4, 4)
                            layoutParams = lpl
                            // 已装备高亮
                            if (isEquipped) {
                                text = "${item.emoji}\n${item.name}\n✅已穿"
                            }
                        }
                        tv.setOnClickListener { toggleEquip(item) }
                        row.addView(tv)
                    }
                    // 空位补齐
                    val remainder = 5 - categoryOwned.size
                    repeat(remainder.coerceAtLeast(0)) {
                        val blank = TextView(requireContext()).apply {
                            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                        }
                        row.addView(blank)
                    }
                }
            }
        }
    }

    private fun toggleEquip(item: RabbitItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            val activity = requireActivity() as MainActivity
            val equipped = activity.progress.equippedItems.first()
            if (item.id in equipped) {
                activity.progress.unequip(item.id)
                activity.speak("脱掉${item.name}")
            } else {
                activity.progress.equip(item.id)
                activity.speak("穿上${item.name}，真漂亮！")
            }
            refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
