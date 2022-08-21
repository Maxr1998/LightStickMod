@file:Suppress("MagicNumber")

package de.maxr1998.lightstick.ui.model

import androidx.compose.ui.graphics.Color

enum class MemberColor(memberColor: Long) {
    HEEJIN(0xFFDE3373),
    HYUNJIN(0xFFFCCA28),
    HASEUL(0xFF08A953),
    YEOJIN(0xFFF5712A),
    VIVI(0xFFF59EAF),
    KIM_LIP(0xFFEE1B3E),
    JINSOUL(0xFF1E76B8),
    CHOERRY(0xFF7C2E87),
    YVES(0xFF7A0434),
    CHUU(0xFFF98F81),
    GO_WON(0xFF37BA9B),
    OLIVIA_HYE(0xFFC0C2C2),
    ;

    val color = Color(memberColor)
}