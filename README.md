# Light Stick Mod

Modding the LOOΠΔ light stick with a custom PCB/firmware, rechargeable battery and a companion Android app for wireless control.

<p align="center">
<img src="images/hero.jpg" alt="Photo of the finished light stick" style="width: 60%">
</p>

## [pcb](pcb)

Designed in KiCAD, manufactured by JLCPCB. Uses the ESP32 as a controller.

## [firmware](firmware)

Built with [esphome.io](https://esphome.io).
Supports WiFi control, integrates with Home Assistant, introduces [animated effects](https://gfycat.com/grandcharminghogget).  
Thanks to deep sleep support, idle battery drain can be kept to a minimum.

## [app](app)

Companion app for Android to allow wireless control via Bluetooth LE. Written in Kotlin, using JetPack Compose for the UI.

## [build log](build-log.md)

Write-up on how I modded it.
