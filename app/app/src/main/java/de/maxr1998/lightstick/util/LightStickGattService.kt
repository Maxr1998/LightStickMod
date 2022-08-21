package de.maxr1998.lightstick.util

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

data class LightStickGattService(
    val gattService: BluetoothGattService,
    val colorCharacteristic: BluetoothGattCharacteristic,
    val effectCharacteristic: BluetoothGattCharacteristic,
)