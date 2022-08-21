package de.maxr1998.lightstick.ui.model

import androidx.annotation.DrawableRes
import de.maxr1998.lightstick.R

enum class Effect(
    val effectName: String,
    @DrawableRes val icon: Int,
) {
    NONE("None", R.drawable.ic_none_24),
    BREATHING("Breathing", R.drawable.ic_breathing_24),
    STROBE("Strobe", R.drawable.ic_strobe_24),
    FAST_RAINBOW("Fast Rainbow", R.drawable.ic_fast_24),
    SUPERFAST_RAINBOW("Superfast Rainbow", R.drawable.ic_superfast_24),
    ;
}