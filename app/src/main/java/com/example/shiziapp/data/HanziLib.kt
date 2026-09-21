package com.example.shiziapp.data

/**
 * 一个学习汉字单元
 *
 * @param character 汉字
 * @param pinyin 拼音（带声调）
 * @param emoji 与字义对应的Emoji（系统字体渲染，免网络/免版权）
 * @param phrase 简单组词
 */
data class Hanzi(
    val character: String,
    val pinyin: String,
    val emoji: String,
    val phrase: String
)

/**
 * 学习阶段（关卡）。按难度/主题把生字分组，逐阶段解锁。
 */
enum class Stage(
    val id: Int,
    val name: String,
    val emoji: String
) {
    STAGE_1(1, "大自然", "🌿"),
    STAGE_2(2, "家庭人物", "👨‍👩‍👧"),
    STAGE_3(3, "动物朋友", "🐾"),
    STAGE_4(4, "身体动作", "🙌"),
    STAGE_5(5, "生活用品", "🏠"),
    STAGE_6(6, "好吃的", "🍎"),
    STAGE_7(7, "数来数去", "🔢"),
    STAGE_8(8, "方位天气", "☀️");

    companion object {
        fun fromId(id: Int): Stage = values().firstOrNull { it.id == id } ?: STAGE_1
    }
}

/**
 * 识字字库：内置基础启蒙字表，按阶段组织。
 * 每个阶段约10-15个常见启蒙字。
 */
object HanziLib {

    val stages: List<Stage> = Stage.values().toList()

    /**
     * 获取指定阶段的汉字列表
     */
    fun hanziForStage(stage: Stage): List<Hanzi> = when (stage) {
        Stage.STAGE_1 -> listOf(
            Hanzi("日", "rì", "☀️", "太阳"),
            Hanzi("月", "yuè", "🌙", "月亮"),
            Hanzi("山", "shān", "⛰️", "高山"),
            Hanzi("水", "shuǐ", "💧", "喝水"),
            Hanzi("火", "huǒ", "🔥", "火苗"),
            Hanzi("云", "yún", "☁️", "白云"),
            Hanzi("雨", "yǔ", "🌧️", "下雨"),
            Hanzi("雪", "xuě", "❄️", "雪花"),
            Hanzi("风", "fēng", "🍃", "刮风"),
            Hanzi("天", "tiān", "🌤️", "天空"),
            Hanzi("地", "dì", "🌍", "大地"),
            Hanzi("花", "huā", "🌸", "花朵"),
            Hanzi("草", "cǎo", "🌱", "小草"),
            Hanzi("树", "shù", "🌳", "大树"),
            Hanzi("星", "xīng", "⭐", "星星")
        )
        Stage.STAGE_2 -> listOf(
            Hanzi("人", "rén", "🧑", "人们"),
            Hanzi("爸", "bà", "👨", "爸爸"),
            Hanzi("妈", "mā", "👩", "妈妈"),
            Hanzi("爷", "yé", "👴", "爷爷"),
            Hanzi("奶", "nǎi", "👵", "奶奶"),
            Hanzi("哥", "gē", "👦", "哥哥"),
            Hanzi("姐", "jiě", "👧", "姐姐"),
            Hanzi("我", "wǒ", "🙋", "我们"),
            Hanzi("你", "nǐ", "👉", "你好"),
            Hanzi("他", "tā", "🧑‍🤝‍🧑", "他们"),
            Hanzi("女", "nǚ", "👧", "女孩"),
            Hanzi("男", "nán", "👦", "男孩"),
            Hanzi("宝", "bǎo", "💎", "宝贝"),
            Hanzi("家", "jiā", "🏠", "家庭")
        )
        Stage.STAGE_3 -> listOf(
            Hanzi("猫", "māo", "🐱", "小猫"),
            Hanzi("狗", "gǒu", "🐶", "小狗"),
            Hanzi("鸟", "niǎo", "🐦", "小鸟"),
            Hanzi("鱼", "yú", "🐟", "小鱼"),
            Hanzi("马", "mǎ", "🐴", "小马"),
            Hanzi("牛", "niú", "🐮", "小牛"),
            Hanzi("猪", "zhū", "🐷", "小猪"),
            Hanzi("羊", "yáng", "🐑", "小羊"),
            Hanzi("鸡", "jī", "🐔", "小鸡"),
            Hanzi("鸭", "yā", "🦆", "小鸭"),
            Hanzi("兔", "tù", "🐰", "兔子"),
            Hanzi("熊", "xióng", "🐻", "小熊"),
            Hanzi("猴", "hóu", "🐵", "猴子"),
            Hanzi("象", "xiàng", "🐘", "大象"),
            Hanzi("虎", "hǔ", "🐯", "老虎")
        )
        Stage.STAGE_4 -> listOf(
            Hanzi("眼", "yǎn", "👀", "眼睛"),
            Hanzi("耳", "ěr", "👂", "耳朵"),
            Hanzi("口", "kǒu", "👄", "嘴巴"),
            Hanzi("手", "shǒu", "✋", "小手"),
            Hanzi("足", "zú", "🦶", "小脚"),
            Hanzi("头", "tóu", "🙆", "头发"),
            Hanzi("笑", "xiào", "😊", "欢笑"),
            Hanzi("哭", "kū", "😢", "哭泣"),
            Hanzi("走", "zǒu", "🚶", "走路"),
            Hanzi("跑", "pǎo", "🏃", "跑步"),
            Hanzi("跳", "tiào", "🤸", "跳跃"),
            Hanzi("坐", "zuò", "🧘", "坐下"),
            Hanzi("看", "kàn", "👁️", "看见"),
            Hanzi("听", "tīng", "👂", "听见"),
            Hanzi("说", "shuō", "💬", "说话")
        )
        Stage.STAGE_5 -> listOf(
            Hanzi("门", "mén", "🚪", "大门"),
            Hanzi("窗", "chuāng", "🪟", "窗户"),
            Hanzi("床", "chuáng", "🛏️", "小床"),
            Hanzi("衣", "yī", "👕", "衣服"),
            Hanzi("书", "shū", "📖", "书本"),
            Hanzi("笔", "bǐ", "✏️", "铅笔"),
            Hanzi("杯", "bēi", "🥤", "杯子"),
            Hanzi("车", "chē", "🚗", "汽车"),
            Hanzi("船", "chuán", "⛵", "小船"),
            Hanzi("球", "qiú", "⚽", "皮球"),
            Hanzi("灯", "dēng", "💡", "电灯"),
            Hanzi("椅", "yǐ", "🪑", "椅子"),
            Hanzi("钟", "zhōng", "🕐", "时钟"),
            Hanzi("刀", "dāo", "🔪", "小刀"),
            Hanzi("伞", "sǎn", "☂️", "雨伞")
        )
        Stage.STAGE_6 -> listOf(
            Hanzi("米", "mǐ", "🍚", "大米"),
            Hanzi("面", "miàn", "🍜", "面条"),
            Hanzi("糖", "táng", "🍬", "糖果"),
            Hanzi("蛋", "dàn", "🥚", "鸡蛋"),
            Hanzi("肉", "ròu", "🍖", "肉肉"),
            Hanzi("鱼", "yú", "🐟", "鱼汤"),
            Hanzi("菜", "cài", "🥬", "蔬菜"),
            Hanzi("果", "guǒ", "🍎", "水果"),
            Hanzi("茶", "chá", "🍵", "喝茶"),
            Hanzi("奶", "nǎi", "🥛", "牛奶"),
            Hanzi("瓜", "guā", "🍉", "西瓜"),
            Hanzi("桃", "táo", "🍑", "桃子"),
            Hanzi("豆", "dòu", "🫘", "豆子"),
            Hanzi("包", "bāo", "🥟", "包子"),
            Hanzi("饭", "fàn", "🍚", "米饭")
        )
        Stage.STAGE_7 -> listOf(
            Hanzi("一", "yī", "1️⃣", "一个"),
            Hanzi("二", "èr", "2️⃣", "两个"),
            Hanzi("三", "sān", "3️⃣", "三个"),
            Hanzi("四", "sì", "4️⃣", "四个"),
            Hanzi("五", "wǔ", "5️⃣", "五个"),
            Hanzi("六", "liù", "6️⃣", "六个"),
            Hanzi("七", "qī", "7️⃣", "七个"),
            Hanzi("八", "bā", "8️⃣", "八个"),
            Hanzi("九", "jiǔ", "9️⃣", "九个"),
            Hanzi("十", "shí", "🔟", "十个"),
            Hanzi("百", "bǎi", "💯", "一百"),
            Hanzi("个", "gè", "🔢", "几个"),
            Hanzi("大", "dà", "🐘", "很大"),
            Hanzi("小", "xiǎo", "🐭", "很小"),
            Hanzi("多", "duō", "➕", "很多")
        )
        Stage.STAGE_8 -> listOf(
            Hanzi("上", "shàng", "⬆️", "上面"),
            Hanzi("下", "xià", "⬇️", "下面"),
            Hanzi("左", "zuǒ", "⬅️", "左边"),
            Hanzi("右", "yòu", "➡️", "右边"),
            Hanzi("前", "qián", "👆", "前面"),
            Hanzi("后", "hòu", "👇", "后面"),
            Hanzi("里", "lǐ", "📥", "里面"),
            Hanzi("外", "wài", "📤", "外面"),
            Hanzi("开", "kāi", "🔓", "开门"),
            Hanzi("关", "guān", "🔒", "关门"),
            Hanzi("买", "mǎi", "🛒", "买东西"),
            Hanzi("卖", "mài", "🏷️", "卖东西"),
            Hanzi("来", "lái", "➡️", "过来"),
            Hanzi("去", "qù", "🏃", "出去"),
            Hanzi("有", "yǒu", "✅", "拥有"),
            Hanzi("没", "méi", "❌", "没有")
        )
    }

    /** 全部汉字（用于随机题池） */
    val all: List<Hanzi> = stages.flatMap { hanziForStage(it) }

    /** 总字数 */
    val totalCount: Int get() = all.size
}
