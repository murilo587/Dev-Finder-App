package com.example.devfinder.feature.publicrepos

import androidx.compose.ui.graphics.Color


fun getLanguageColors(language: String): Color {
    val languageColors = mapOf(
        "Kotlin" to Color(0xFFA97BFF),
        "Java" to Color(0xFFB07219),
        "JavaScript" to Color(0xFFF1E05A),
        "TypeScript" to Color(0xFF3178C6),
        "Python" to Color(0xFF3572A5),
        "Dart" to Color(0xFF00B4AB),
        "C" to Color(0xFF555555),
        "C++" to Color(0xFFF34B7D),
        "C#" to Color(0xFF178600),
        "Go" to Color(0xFF00ADD8),
        "Rust" to Color(0xFFDEA584),
        "Swift" to Color(0xFFFA7343),
        "PHP" to Color(0xFF4F5D95),
        "Ruby" to Color(0xFFCC342D),
        "Scala" to Color(0xFFDC322F),
        "Shell" to Color(0xFF89E051),
        "PowerShell" to Color(0xFF012456),
        "HTML" to Color(0xFFE34C26),
        "CSS" to Color(0xFF563D7C),
        "SCSS" to Color(0xFFC6538C),
        "Vue" to Color(0xFF41B883),
        "Svelte" to Color(0xFFFF3E00),
        "Objective-C" to Color(0xFF438EFF),
        "Objective-C++" to Color(0xFF6866FB),
        "R" to Color(0xFF198CE7),
        "MATLAB" to Color(0xFFE16737),
        "Lua" to Color(0xFF000080),
        "Perl" to Color(0xFF0298C3),
        "Haskell" to Color(0xFF5E5086),
        "Elixir" to Color(0xFF6E4A7E),
        "Erlang" to Color(0xFFB83998),
        "Clojure" to Color(0xFFDB5855),
        "F#" to Color(0xFFB845FC),
        "Visual Basic .NET" to Color(0xFF945DB7),
        "Assembly" to Color(0xFF6E4C13),
        "Dockerfile" to Color(0xFF384D54),
        "Jupyter Notebook" to Color(0xFFDA5B0B),
        "Terraform" to Color(0xFF7B42BC),
        "Groovy" to Color(0xFF4298B8),
        "Zig" to Color(0xFFF7A41D)
    )
    return languageColors[language] ?: Color.Gray
}