# 🐰 识字乐园（ShiziApp）

一个为 **4 岁孩子**设计的看图识字安卓 App，全中文 + 普通话语音引导，孩子可完全独立操作。

## ✨ 功能一览

| 模块 | 说明 |
|------|------|
| 📖 **学一学** | 8 大主题、120+ 个基础启蒙汉字，每字配 Emoji 图 + 拼音 + 组词 + 标准普通话朗读 |
| 🎮 **玩一玩** | 三种小游戏：**翻牌配对**、**听音选字**、**看图选字**，边玩边巩固 |
| 🎀 **小兔子** | 答对/学习赚金币 → **开盲盒** 或 **逛商店** → 收集装扮给小兔子穿衣服 |
| 🏆 **我的奖章** | 查看已学字数与各主题进度，自动解锁后续主题 |

## 👶 儿童友好设计

- 超大按钮，无需识字也能操作
- 全程语音提示（进场自动朗读）
- 屏幕锁定竖屏
- 鲜艳配色、兔子主题
- 克制激励：学好字得金币，金币换装扮，培养坚持

## 🛠️ 技术栈

- **语言**：Kotlin（原生安卓）
- **UI**：ViewBinding + 原生视图（无第三方复杂依赖）
- **存储**：Jetpack DataStore（进度/金币/装扮本地持久化）
- **语音**：Android TextToSpeech（离线标准普通话）
- **图片**：采用系统 Emoji 渲染，免网络、免版权、无需图片素材

## 📦 内容（8 大主题）

1. 🌿 大自然　2. 👨‍👩‍👧 家庭人物　3. 🐾 动物朋友　4. 🙌 身体动作
5. 🏠 生活用品　6. 🍎 好吃的　7. 🔢 数来数去　8. ☀️ 方位天气

## 🚀 如何构建运行（两种方式任选其一）

当前代码为完整可编译的 Android 工程，已内置 **GitHub Actions 云编译** 和 **gradle wrapper**。

### 方式一（推荐，无需装任何软件）：GitHub 云端自动编译 APK ✅

1. 在 GitHub 上新建一个仓库（公开或私有均可）
2. 把本目录整体推送上去（见下方 git 命令）
3. 进入仓库 → **Actions** 标签页 → 左侧选 **Build APK** → 点 **Run workflow**
4. 等待约 3-5 分钟，绿勾表示编译成功
5. 点开该次运行 → 底部 **Artifacts** → 下载 `shizi-app-debug-apk`，解压得到 `app-debug.apk`，传到手机安装即可

> 每次往 main/master 推送代码也会自动触发编译。

### 方式二：本地 Android Studio 构建

1. 安装 **Android Studio**（官网免费，自带 JDK）
2. **Open** → 选择本目录 `E:\KIDS-APP\ShiziApp`
3. 首次自动下载依赖（联网，几分钟）
4. 连接手机（开**开发者模式**）或建模拟器 → 点 **Run ▶**
> 或用 **Build → Build APK** 只生成安装包。

## 📁 项目结构

```
ShiziApp/
├── app/src/main/java/com/example/shiziapp/
│   ├── MainActivity.kt          # 入口容器
│   ├── data/                     # 数据与持久化
│   │   ├── HanziLib.kt           # 120+ 汉字词库（8主题）
│   │   ├── RabbitItems.kt        # 兔子装扮定义
│   │   └── ProgressRepository.kt # DataStore 进度存取
│   ├── ui/                       # 界面
│   │   ├── HomeFragment.kt       # 主菜单
│   │   ├── Learn*.kt             # 看图识字
│   │   ├── *GameFragment.kt      # 三种游戏
│   │   ├── RabbitFragment.kt     # 兔子装扮
│   │   └── ProgressFragment.kt   # 奖章进度
│   └── util/TTSUtils.kt          # 普通话语音朗读
└── app/src/main/res/             # 布局与资源
```

## ✏️ 想加/改字？

编辑 `data/HanziLib.kt`，按主题增删 `Hanzi("字", "拼音", "Emoji", "组词")` 即可，无需改其他代码。

## ⬆️ 推送到 GitHub（云编译用）

在项目目录 `E:\KIDS-APP\ShiziApp` 下打开命令行，依次执行：

```bash
git init
git add .
git commit -m "识字乐园 v1.0"
git branch -M main
git remote add origin https://github.com/你的用户名/你的仓库名.git
git push -u origin main
```

> 若未配置过 GitHub 登录，push 时会要求输入用户名和个人访问令牌（Personal Access Token，需勾选 repo 权限）。更省事的方式：直接用 GitHub Desktop 或网页上传。
