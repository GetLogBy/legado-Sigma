# AGENTS.md

## 范围
- 这是 Android/Kotlin 项目，另有独立的 Vue Web 模块；Gradle 只包含 `:app`、`:modules:book`、`:modules:rhino`。
- `modules/web` 不是 Gradle 子项目；Web 命令必须在 `modules/web` 下执行。

## Android 命令
- 使用 JDK 17 和仓库内 Gradle Wrapper（`gradle-8.14.4`）；Unix 用 `./gradlew`，Windows 用 `./gradlew.bat`。
- 主要本地验证命令：`./gradlew :app:assembleAppDebug :app:testAppDebugUnitTest :app:lintAppDebug`。
- 单个 JVM 测试示例：`./gradlew :app:testAppDebugUnitTest --tests io.legado.app.JsTest`。
- 仪器测试需要已连接的模拟器或真机：`./gradlew :app:connectedAppDebugAndroidTest`。
- CI 发布构建会先注入签名属性，再运行 `./gradlew assembleapprelease --build-cache --parallel --daemon --warning-mode all`。

## Web 命令
- `modules/web` 要求 Node `>=20`、pnpm `>=9`；CI 使用 Node 22 和 pnpm 9。
- 在 `modules/web` 下：`pnpm i` 安装，`pnpm type-check` 类型检查，`pnpm build-only` 只构建，`pnpm build` 完整构建。
- `pnpm build` 会运行 `scripts/sync.js`；只有存在 `GITHUB_ENV` 时才把 `dist` 复制到 `app/src/main/assets/web/vue`，本地通常会输出“非Github WorkFlows环境，取消文件复制”。
- Web 开发服务为 `pnpm dev`，端口 `8080`；需要 Android App 提供 WebService 后端，或设置 `VITE_API` / 浏览器 `localStorage.remoteUrl`。
- Web lint 只有修复命令（`pnpm lint:fix`）；没有只检查不修改的 lint 脚本。

## 架构提示
- Android 入口包括 `io.legado.app.App`、启动页 `ui.welcome.WelcomeActivity`、主界面 `ui.main.MainActivity`、Web 后端 `service.WebService`。
- `app/src/main/java/io/legado/app/model` 放规则解析和书籍/RSS 获取；`data` 放 Room 实体/DAO；`web` 放 App 内 HTTP/WebSocket 服务。
- `:modules:book` 是命名空间 `me.ag2s` 的小型 Android 库；`:modules:rhino` 封装 Mozilla Rhino 供 `:app` 的规则脚本使用。
- Web 使用 hash 路由；`/` 是书架，`#/bookSource` 编辑书源，`#/rssSource` 编辑订阅源。

## 生成与入库产物
- Room schema 已入库在 `app/schemas/io.legado.app.data.AppDatabase`；修改实体/迁移时要更新 `AppDatabase.version`、migrations/autoMigrations，并提交新的 schema JSON。
- Cronet 由 `gradle.properties`（`CronetVersion`、`CronetMainVersion`）和 `./gradlew app:downloadCronet` 管理；该任务会重写 `app/src/main/assets/cronet.json` 以及 Cronet jar/so 文件。
- 不要把 `settings.gradle` 里的镜像仓库取消注释后提交；注释说明镜像仅供本地网络异常时使用。
- `gradle/libs.versions.toml` 中有带“不要更新”说明的依赖固定版本（如 `jsoup`、`commonsText`、`rhino` 等）；不要随意升级。

## 风格与格式
- Android 使用 Kotlin official 风格（`kotlin.code.style=official`），Java/Kotlin 目标 17，minSdk 21，target/compile SDK 36。
- Web 格式化为 Prettier：无分号、单引号、`arrowParens: avoid`；`.editorconfig` 对 JS/TS/Vue 设置 2 空格缩进。
- Vite 会生成 `modules/web/src/auto-imports.d.ts` 和 `modules/web/src/components.d.ts`；组件或自动导入发现规则变化时要同步更新。
