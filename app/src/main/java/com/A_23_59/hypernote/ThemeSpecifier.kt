package com.A_23_59.hypernote


class ThemeSpecifier(var themeState: ThemeState) {
    var themeIsDark = false


    fun themeModifier() {

        themeIsDark = when (themeState) {
            ThemeState.LIGHT->{
                false
            }
            ThemeState.DARK -> {
                true
            }
            ThemeState.GLASSMORPHISM -> {
                true
            }
        }

    }


}