package com.jsonquizzz.domain.model

data class AppSettings(
    val isDarkMode: Boolean = false,
    val dynamicColors: Boolean = true,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val enableHints: Boolean = true,
    val instantFeedback: Boolean = true,
    val selectedThemeColor: ThemeColor = ThemeColor.DEFAULT,
    val fontSize: FontSize = FontSize.MEDIUM
)

enum class ThemeColor(val displayName: String, val hexColor: Long) {
    DEFAULT("Default", 0xFF6750A4),
    BLUE("Blue", 0xFF1976D2),
    GREEN("Green", 0xFF388E3C),
    RED("Red", 0xFFD32F2F),
    ORANGE("Orange", 0xFFF57C00),
    PURPLE("Purple", 0xFF7B1FA2),
    TEAL("Teal", 0xFF00796B),
    PINK("Pink", 0xFFC2185B)
}

enum class FontSize(val displayName: String, val scale: Float) {
    SMALL("Small", 0.85f),
    MEDIUM("Medium", 1.0f),
    LARGE("Large", 1.15f),
    EXTRA_LARGE("Extra Large", 1.3f)
}
