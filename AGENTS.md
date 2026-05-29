# AGENTS.md

## Scope / 范围
- EN: This is an Android/Kotlin repo with a separate Vue web module; Gradle includes only `:app`, `:modules:book`, and `:modules:rhino`.
- 中文：这是 Android/Kotlin 项目，另有独立的 Vue Web 模块；Gradle 只包含 `:app`、`:modules:book`、`:modules:rhino`。
- EN: `modules/web` is not a Gradle project; run its Node commands from `modules/web`.
- 中文：`modules/web` 不是 Gradle 子项目；Web 命令必须在 `modules/web` 下执行。

## Android Commands / Android 命令
- EN: Use JDK 17 and the checked-in Gradle wrapper (`gradle-8.14.4`): `./gradlew` on Unix or `./gradlew.bat` on Windows.
- 中文：使用 JDK 17 和仓库内 Gradle Wrapper（`gradle-8.14.4`）；Unix 用 `./gradlew`，Windows 用 `./gradlew.bat`。
- EN: Main local verification: `./gradlew :app:assembleAppDebug :app:testAppDebugUnitTest :app:lintAppDebug`.
- 中文：主要本地验证命令：`./gradlew :app:assembleAppDebug :app:testAppDebugUnitTest :app:lintAppDebug`。
- EN: Run one JVM test with `./gradlew :app:testAppDebugUnitTest --tests io.legado.app.JsTest`.
- 中文：单个 JVM 测试示例：`./gradlew :app:testAppDebugUnitTest --tests io.legado.app.JsTest`。
- EN: Instrumented tests require a connected emulator/device: `./gradlew :app:connectedAppDebugAndroidTest`.
- 中文：仪器测试需要已连接的模拟器或真机：`./gradlew :app:connectedAppDebugAndroidTest`。
- EN: CI release build uses `./gradlew assembleapprelease --build-cache --parallel --daemon --warning-mode all` after injecting signing properties.
- 中文：CI 发布构建会先注入签名属性，再运行 `./gradlew assembleapprelease --build-cache --parallel --daemon --warning-mode all`。

## Web Commands / Web 命令
- EN: `modules/web` expects Node `>=20` and pnpm `>=9`; CI uses Node 22 and pnpm 9.
- 中文：`modules/web` 要求 Node `>=20`、pnpm `>=9`；CI 使用 Node 22 和 pnpm 9。
- EN: From `modules/web`: install with `pnpm i`, typecheck with `pnpm type-check`, build without copying with `pnpm build-only`, full build with `pnpm build`.
- 中文：在 `modules/web` 下：`pnpm i` 安装，`pnpm type-check` 类型检查，`pnpm build-only` 只构建，`pnpm build` 完整构建。
- EN: `pnpm build` runs `scripts/sync.js`; it copies `dist` to `app/src/main/assets/web/vue` only when `GITHUB_ENV` exists, so local builds normally print “非Github WorkFlows环境，取消文件复制”.
- 中文：`pnpm build` 会运行 `scripts/sync.js`；只有存在 `GITHUB_ENV` 时才把 `dist` 复制到 `app/src/main/assets/web/vue`，本地通常会输出“非Github WorkFlows环境，取消文件复制”。
- EN: Web dev server is `pnpm dev` on port `8080`; it needs the Android app WebService backend, or set `VITE_API` / browser `localStorage.remoteUrl`.
- 中文：Web 开发服务为 `pnpm dev`，端口 `8080`；需要 Android App 提供 WebService 后端，或设置 `VITE_API` / 浏览器 `localStorage.remoteUrl`。
- EN: Web lint has only a fixer (`pnpm lint:fix`); there is no non-mutating lint script.
- 中文：Web lint 只有修复命令（`pnpm lint:fix`）；没有只检查不修改的 lint 脚本。

## Architecture Notes / 架构提示
- EN: Android entrypoints are `io.legado.app.App`, launcher `ui.welcome.WelcomeActivity`, main screen `ui.main.MainActivity`, and web backend `service.WebService`.
- 中文：Android 入口包括 `io.legado.app.App`、启动页 `ui.welcome.WelcomeActivity`、主界面 `ui.main.MainActivity`、Web 后端 `service.WebService`。
- EN: `app/src/main/java/io/legado/app/model` contains rule parsing and book/RSS acquisition; `data` contains Room entities/DAOs; `web` contains the in-app HTTP/WebSocket service.
- 中文：`app/src/main/java/io/legado/app/model` 放规则解析和书籍/RSS 获取；`data` 放 Room 实体/DAO；`web` 放 App 内 HTTP/WebSocket 服务。
- EN: `:modules:book` is a small Android library under namespace `me.ag2s`; `:modules:rhino` wraps Mozilla Rhino for rule scripting and is used by `:app`.
- 中文：`:modules:book` 是命名空间 `me.ag2s` 的小型 Android 库；`:modules:rhino` 封装 Mozilla Rhino 供 `:app` 的规则脚本使用。
- EN: Web routes are hash-based; `/` is bookshelf, `#/bookSource` edits book sources, and `#/rssSource` edits RSS sources.
- 中文：Web 使用 hash 路由；`/` 是书架，`#/bookSource` 编辑书源，`#/rssSource` 编辑订阅源。

## Generated And Versioned Artifacts / 生成与入库产物
- EN: Room schemas are committed under `app/schemas/io.legado.app.data.AppDatabase`; when changing entities/migrations, update `AppDatabase.version`, migrations/autoMigrations, and commit the new schema JSON.
- 中文：Room schema 已入库在 `app/schemas/io.legado.app.data.AppDatabase`；修改实体/迁移时要更新 `AppDatabase.version`、migrations/autoMigrations，并提交新的 schema JSON。
- EN: Cronet artifacts are managed by `gradle.properties` (`CronetVersion`, `CronetMainVersion`) plus `./gradlew app:downloadCronet`; this rewrites `app/src/main/assets/cronet.json` and Cronet jars/so files.
- 中文：Cronet 由 `gradle.properties`（`CronetVersion`、`CronetMainVersion`）和 `./gradlew app:downloadCronet` 管理；该任务会重写 `app/src/main/assets/cronet.json` 以及 Cronet jar/so 文件。
- EN: Do not enable mirror repositories in `settings.gradle` and commit them; comments there say mirrors are only for local connectivity failures.
- 中文：不要把 `settings.gradle` 里的镜像仓库取消注释后提交；注释说明镜像仅供本地网络异常时使用。
- EN: Dependency versions in `gradle/libs.versions.toml` include intentional “do not update” pins (`jsoup`, `commonsText`, `rhino`, etc.); do not bump them casually.
- 中文：`gradle/libs.versions.toml` 中有带“不要更新”说明的依赖固定版本（如 `jsoup`、`commonsText`、`rhino` 等）；不要随意升级。

## Style And Formatting / 风格与格式
- EN: Android uses Kotlin official style (`kotlin.code.style=official`), Java/Kotlin target 17, minSdk 21, target/compile SDK 36.
- 中文：Android 使用 Kotlin official 风格（`kotlin.code.style=official`），Java/Kotlin 目标 17，minSdk 21，target/compile SDK 36。
- EN: Web formatting is Prettier with no semicolons, single quotes, and `arrowParens: avoid`; `.editorconfig` sets 2-space indents for JS/TS/Vue.
- 中文：Web 格式化为 Prettier：无分号、单引号、`arrowParens: avoid`；`.editorconfig` 对 JS/TS/Vue 设置 2 空格缩进。
- EN: Vite auto-generates `modules/web/src/auto-imports.d.ts` and `modules/web/src/components.d.ts`; keep them in sync when component/import discovery changes.
- 中文：Vite 会生成 `modules/web/src/auto-imports.d.ts` 和 `modules/web/src/components.d.ts`；组件或自动导入发现规则变化时要同步更新。
