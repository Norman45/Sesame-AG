package io.github.aoguai.sesameag.util

/**
 * 模块状态与 libxposed 运行时解析。
 *
 * 只使用框架通过 libxposed 提供的官方名称和 API 版本。
 */
object ModuleStatus {
    // This object also runs in the standalone settings process, where the compileOnly API jar is absent.
    const val MIN_SUPPORTED_LIBXPOSED_API = 102

    private const val UNKNOWN_FRAMEWORK = "Unknown"

    enum class FrameworkCategory {
        LSPOSED,
        UNSUPPORTED,
    }

    data class FrameworkInfo(
        val displayName: String,
        val category: FrameworkCategory,
    )

    fun resolveFrameworkInfo(officialFrameworkName: String?): FrameworkInfo {
        val displayName = officialFrameworkName?.trim()?.takeIf { it.isNotBlank() } ?: UNKNOWN_FRAMEWORK
        return FrameworkInfo(displayName, classifyFrameworkName(displayName))
    }

    /** 免 root 注入框架（LSPatch 一类）：没有 Xposed 管理器，走集成/本地补丁注入。 */
    private fun isRootlessInjectionFramework(officialName: String): Boolean =
        officialName.lowercase().contains("lspatch")

    fun classifyFrameworkName(frameworkName: String?): FrameworkCategory {
        val officialName = frameworkName?.trim() ?: return FrameworkCategory.UNSUPPORTED
        return if (officialName == "LSPosed" || officialName == "Vector" || isRootlessInjectionFramework(officialName)) {
            FrameworkCategory.LSPOSED
        } else {
            FrameworkCategory.UNSUPPORTED
        }
    }

    fun isSupportedLsposedFramework(frameworkName: String?, apiVersion: Int): Boolean {
        return apiVersion >= MIN_SUPPORTED_LIBXPOSED_API &&
            classifyFrameworkName(frameworkName) == FrameworkCategory.LSPOSED
    }
}
