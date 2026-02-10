# 贡献指南

感谢你对 **鹿了么** 的关注！欢迎以任何形式参与贡献。

## 🐛 报告 Bug

1. 在 [Issues](../../issues) 中搜索是否已有类似问题
2. 如果没有，创建新 Issue，请包含：
   - 设备型号和 Android 版本
   - 复现步骤
   - 期望行为 vs 实际行为
   - 截图或日志（如有）

## 💡 功能建议

欢迎在 Issues 中提出功能建议，标注 `enhancement` 标签。

## 🔀 提交 Pull Request

1. Fork 本项目
2. 创建功能分支：`git checkout -b feature/your-feature`
3. 提交更改：`git commit -m '添加某某功能'`
4. 推送分支：`git push origin feature/your-feature`
5. 创建 Pull Request

### 代码规范

- 使用 Kotlin 官方代码风格
- UI 使用 Jetpack Compose 声明式写法
- 中文注释，变量名使用英文
- 新功能请确保编译通过：`./gradlew assembleDebug`

### 提交信息格式

```
<类型>: <简短描述>

<详细说明（可选）>
```

类型包括：`feat`（新功能）、`fix`（修复）、`docs`（文档）、`style`（格式）、`refactor`（重构）

## 📄 许可

提交贡献即表示你同意将代码以 [Apache 2.0](LICENSE) 许可证发布。
