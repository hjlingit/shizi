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
import com.example.shiziapp.databinding.FragmentShopBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 装扮商店：用金币购买指定装扮。
 */
class ShopFragment : Fragment() {

    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity() as MainActivity
        binding.btnBackS.setOnClickListener {
            activity.onBackPressedDispatcher.onBackPressed()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            activity.progress.coins.collect { coins ->
                binding.tvCoinsS.text = "🪙$coins"
            }
        }
        renderShop()
    }

    private fun renderShop() {
        val activity = requireActivity() as MainActivity
        viewLifecycleOwner.lifecycleScope.launch {
            val owned = activity.progress.ownedItems.first()
            val container = binding.shopContainer
            container.removeAllViews()

            // 分类
            val grouped = RabbitItems.allItems.groupBy { it.category }
            grouped.forEach { (category, items) ->
                val title = TextView(requireContext()).apply {
                    text = "${category.emoji} ${category.label}"
                    textSize = 22f
                    setTextColor(0xFF4A4A4A.toInt())
                }
                val lp = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                lp.setMargins(0, 12, 0, 6)
                container.addView(title, lp)

                // 每行2个
                val row = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }
                items.forEachIndexed { index, item ->
                    row.addView(makeItemCard(item, item.id in owned))
                    if ((index + 1) % 2 == 0) {
                        container.addView(row)
                        // 新行
                    }
                }
                // 若单数补空
                if (items.size % 2 != 0) container.addView(row)
            }
        }
    }

    private fun makeItemCard(item: RabbitItem, owned: Boolean): TextView {
        return TextView(requireContext()).apply {
            text = "${item.emoji}\n${item.name}\n" + if (owned) "✅ 已拥有" else "🪙${item.price}"
            textSize = 18f
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.bg_card)
            setPadding(8, 14, 8, 14)
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            lp.setMargins(5, 5, 5, 5)
            layoutParams = lp
            if (owned) {
                setTextColor(0xFFAAAAAA.toInt())
                alpha = 0.6f
            } else {
                setTextColor(0xFF4A4A4A.toInt())
                setOnClickListener { buyItem(item) }
            }
        }
    }

    private fun buyItem(item: RabbitItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            val activity = requireActivity() as MainActivity
            val ok = activity.progress.spendCoins(item.price)
            if (ok) {
                activity.progress.addOwnedItem(item.id)
                activity.progress.equip(item.id)
                activity.speak("买到了${item.name}，真好看！")
                renderShop()
            } else {
                activity.speak("金币不够啦，先去多学几个字吧")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
