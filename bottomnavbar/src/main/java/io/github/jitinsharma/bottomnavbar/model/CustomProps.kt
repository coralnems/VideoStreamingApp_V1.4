package io.github.jitinsharma.bottomnavbar.model

import android.graphics.Color

data class CustomProps(
        var secondaryTextColor : Int = Color.BLACK,
        var secondaryImageColor : Int = Color.BLACK,
        var primaryTextColor : Int = Color.BLACK,
        var primaryButtonBg : Int = Color.BLACK,
        var lineColor : Int = Color.BLACK,
        var stripBg : Int = Color.WHITE,
        var secondaryItemTextClickedColor : Int = -1,
        var secondaryItemImageClickedColor : Int = -1
)