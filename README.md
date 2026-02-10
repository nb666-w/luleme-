# 🦌 鹿了么 (Luleme)

> 坚持自律，遇见更好的自己

一款基于 Android Jetpack Compose 构建的自律打卡 App，帮助你记录和追踪日常习惯，用数据驱动自我成长。

## ✨ 功能特色

- 🎯 **一键签到** — 快速记录，支持心情标记（5 级）和文字备注
- 📸 **图片附件** — 签到时可附加图片，记录更有仪式感
- 📅 **补签功能** — 忘记打卡？选择日期即可补签
- 📊 **数据统计** — 连续签到天数、趋势图表、心情分布、里程碑
- 🏆 **成就系统** — 解锁各种坚持成就，激励你持续自律
- 🤖 **AI 分析** — 接入 OpenAI / DeepSeek 等 API，智能分析健康数据
- 📤 **数据导出** — 支持 CSV 格式导出到 Downloads 目录
- 🦌 **趣味启动页** — 一群鹿奔腾而过的动画开屏
- 🛑 **健康提醒** — 每日签到超过 100 次？App 会用搞笑方式提醒你休息

## 🛠️ 技术栈

| 技术 | 用途 |
|------|------|
| **Kotlin** | 主要开发语言 |
| **Jetpack Compose** | 声明式 UI 框架 |
| **Room Database** | 本地数据持久化 |
| **DataStore** | 轻量级键值存储 |
| **Coil** | 图片异步加载 |
| **Material 3** | Material Design 3 组件库 |
| **Vico** | 图表可视化 |
| **Coroutines + Flow** | 异步编程 |

## 📦 编译运行

### 环境要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK 36
- Gradle 8.13+

### 编译步骤

```bash
# 克隆项目
git clone https://github.com/nb666-w/luleme.git
cd luleme

# 编译 Debug APK
./gradlew assembleDebug

# APK 输出路径
# app/build/outputs/apk/debug/app-debug.apk
```

### 在 Android Studio 中运行

1. 用 Android Studio 打开项目根目录
2. 等待 Gradle Sync 完成
3. 连接 Android 设备或启动模拟器
4. 点击 ▶️ Run

## 📁 项目结构

```
app/src/main/java/com/example/lululu/
├── data/
│   ├── dao/          # Room DAO 接口
│   ├── database/     # 数据库配置
│   ├── entity/       # 数据实体类
│   └── repository/   # 数据仓库层
├── ui/
│   ├── screen/       # 各页面 Composable
│   ├── theme/        # 主题、颜色、字体
│   └── viewmodel/    # ViewModel
├── notification/     # 通知与提醒
├── MainActivity.kt
└── LululuApplication.kt
```

## ⚙️ AI 分析功能配置

App 内置 AI 健康分析功能，需要自行配置 API：

1. 打开 App → 设置 → API Key 设置
2. 填入你的 API Key、Base URL 和模型名称
3. 支持 OpenAI、DeepSeek、通义千问等兼容 OpenAI API 的服务

> **注意**：API Key 仅存储在你的设备本地，不会上传到任何服务器。

## 🤝 贡献

欢迎贡献代码！请查看 [CONTRIBUTING.md](CONTRIBUTING.md) 了解详情。

## 📄 许可证

本项目基于 [Apache License 2.0](LICENSE) 开源。

```
Copyright 2026 鹿了么 Contributors

Licensed under the Apache License, Version 2.0
```

## 💖 支持

如果觉得这个项目对你有帮助，欢迎：
- ⭐ 给项目一个 Star
- 🐛 提交 Issue 报告 Bug
- 🔀 提交 Pull Request
- ☕ 在 App 设置页面请开发者喝杯咖啡
