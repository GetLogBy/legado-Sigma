# AGENTS.md

## 项目范围
- Android/Kotlin 项目 + 独立 Vue Web 模块；Gradle 只包含 `:app`、`:modules:book`、`:modules:rhino`。
- `modules/web` 不是 Gradle 子项目；Web 命令必须在 `modules/web` 下执行。

## Android 命令
- JDK 17，仓库内 Gradle Wrapper (`gradle-8.14.4`)；Windows 用 `./gradlew.bat`，Unix 用 `./gradlew`。
- 主要本地验证：`./gradlew :app:assembleAppDebug :app:testAppDebugUnitTest :app:lintAppDebug`。
- 单个 JVM 测试：`./gradlew :app:testAppDebugUnitTest --tests io.legado.app.JsTest`。
- 仪器测试需要模拟器/真机：`./gradlew :app:connectedAppDebugAndroidTest`。
- 另有 `google` product flavor（见 `app/build.gradle` release.yml），对应 `assembleGoogleRelease`。
- CI 构建 `assembleapprelease` 前会注入签名属性；`releaseS` 为共存变体（applicationIdSuffix `.releaseS`）。
- Cronet 由 `gradle.properties` (`CronetVersion`, `CronetMainVersion`) 和 `./gradlew app:downloadCronet` 管理；会重写 `app/src/main/assets/cronet.json`、Cronet jar/so 文件，以及同步 `cronet-proguard-rules.pro`。
- `coreLibraryDesugaring` 已启用（`app/build.gradle:136`）。

## Web 命令
- Node `>=20`、pnpm `>=9`；CI 用 Node 22 + pnpm 9。
- `pnpm i` 安装，`pnpm type-check` 类型检查，`pnpm build-only` 只构建，`pnpm build` 完整构建。
- `pnpm build` = `type-check` → `build-only` → `node ./scripts/sync.js`；`sync.js` 仅在存在 `GITHUB_ENV` 时将 `dist` 复制到 `app/src/main/assets/web/vue`，本地会跳过。
- `pnpm dev` 开发服务端口 `8080`；需 Android App WebService 后端，或设 `VITE_API` / `localStorage.remoteUrl`。
- Lint 只有修复命令 `pnpm lint:fix`（`eslint . --fix`），无只读检查。

## 架构提示
- Android 入口：`io.legado.app.App`、`ui.welcome.WelcomeActivity`、`ui.main.MainActivity`、`service.WebService`。
- `app/…/model` = 规则解析 & 书籍/RSS 获取；`data` = Room 实体/DAO；`web` = 应用内 HTTP/WebSocket 服务。
- `:modules:book` 是 namespace `me.ag2s` 的小型 Android 库；`:modules:rhino`（namespace `com.script`）封装 Mozilla Rhino 供规则脚本用。
- Web 使用 hash 路由（`createWebHashHistory`）；`/` = 书架，`#/chapter` = 阅读页，`#/bookSource` = 书源编辑，`#/rssSource` = 订阅源编辑。
- API 文档见 `api.md`。

## 生成与入库产物
- Room schema 入库在 `app/schemas/io.legado.app.data.AppDatabase`；修改实体/迁移时要更新 `@Database(version=…)`、migrations/autoMigrations，并提交新的 schema JSON。
- 镜像仓库在 `settings.gradle` 中注释；仅供本地网络异常时启用，不要取消注释提交。
- `gradle/libs.versions.toml` 中有带"不要更新"注释的固定版本（`jsoup`、`commonsText`、`rhino`、`media3`、`gsyvideoplayer`、`room` 等）；不要随意升级。

## 风格与格式
- Android Kotlin official 风格（`kotlin.code.style=official`），Java/Kotlin 目标 17，minSdk 21，target/compile SDK 36。
- Web Prettier：无分号、单引号、`arrowParens: avoid`；`.editorconfig` 对 JS/TS/Vue 2 空格缩进。
- Vite 生成 `modules/web/src/auto-imports.d.ts` 和 `modules/web/src/components.d.ts`；自动导入/组件规则变化时要同步更新。
- 根 `package.json` 配置了 commitizen（`cz-conventional-changelog`）；CI 使用 `cz-conventional-changelog` 风格的提交信息（如 `Bump web`、`Bump cronet`）。
