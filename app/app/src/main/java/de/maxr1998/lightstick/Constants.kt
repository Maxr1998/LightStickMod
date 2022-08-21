package de.maxr1998.lightstick

import java.util.UUID

object Constants {
    const val LIGHT_STICK_ADDRESS = "34:94:54:F4:AC:EE"
    val SERVICE_UUID: UUID = UUID.fromString("147a1293-f137-4ca1-8410-7a50c277632e")
    val COLOR_CHARACTERISTIC_UUID: UUID = UUID.fromString("435aeb49-d070-4e6e-8ecb-6d3459797cf7")
    val EFFECT_CHARACTERISTIC_UUID: UUID = UUID.fromString("5db3fa02-8556-4692-b99e-03da81fd8f10")
}