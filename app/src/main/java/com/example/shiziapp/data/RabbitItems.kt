package com.example.shiziapp.data

/**
 * 兔子装扮物品。类别：帽子 / 衣服 / 鞋子 / 配饰。
 */
enum class ItemCategory(val label: String, val emoji: String) {
    HAT("帽子", "🎩"),
    CLOTHES("衣服", "👕"),
    SHOES("鞋子", "👟"),
    ACCESSORY("配饰", "🎀")
}

data class RabbitItem(
    val id: String,
    val name: String,
    val category: ItemCategory,
    val emoji: String,
    val price: Int
)

/**
 * 兔子装扮商店 + 盲盒池定义。
 */
object RabbitItems {

    /** 商店里可购买的所有物品 */
    val allItems: List<RabbitItem> = listOf(
        RabbitItem("hat_red", "小红帽", ItemCategory.HAT, "🔴", 100),
        RabbitItem("hat_blue", "小蓝帽", ItemCategory.HAT, "🔵", 100),
        RabbitItem("hat_crown", "金色皇冠", ItemCategory.HAT, "👑", 300),
        RabbitItem("hat_flower", "小花环", ItemCategory.HAT, "🌸", 200),
        RabbitItem("hat_star", "星星帽", ItemCategory.HAT, "⭐", 250),

        RabbitItem("cloth_dress", "小花裙", ItemCategory.CLOTHES, "👗", 200),
        RabbitItem("cloth_t_shirt", "条纹T恤", ItemCategory.CLOTHES, "👕", 150),
        RabbitItem("cloth_sweater", "红毛衣", ItemCategory.CLOTHES, "🧶", 250),
        RabbitItem("cloth_overall", "背带裤", ItemCategory.CLOTHES, "👖", 250),
        RabbitItem("cloth_tutu", "芭蕾裙", ItemCategory.CLOTHES, "🩰", 350),

        RabbitItem("shoe_red", "红皮鞋", ItemCategory.SHOES, "🔴", 120),
        RabbitItem("shoe_boots", "小雨靴", ItemCategory.SHOES, "🥾", 180),
        RabbitItem("shoe_sneaker", "小白鞋", ItemCategory.SHOES, "👟", 150),

        RabbitItem("acc_bow", "蝴蝶结", ItemCategory.ACCESSORY, "🎀", 120),
        RabbitItem("acc_glasses", "圆眼镜", ItemCategory.ACCESSORY, "👓", 200),
        RabbitItem("acc_necklace", "珍珠项链", ItemCategory.ACCESSORY, "📿", 300),
        RabbitItem("acc_balloon", "大气球", ItemCategory.ACCESSORY, "🎈", 150),
        RabbitItem("acc_backpack", "小书包", ItemCategory.ACCESSORY, "🎒", 250)
    )

    fun itemById(id: String): RabbitItem? = allItems.firstOrNull { it.id == id }

    /** 盲盒抽取池（惊喜感：含值钱皇冠/芭蕾裙等） */
    val gachaPool: List<RabbitItem> = allItems
}
