package de.maxr1998.lightstick

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.maxr1998.lightstick.permission.PermissionManager
import de.maxr1998.lightstick.ui.model.Effect
import de.maxr1998.lightstick.ui.model.ErrorState
import de.maxr1998.lightstick.util.LightStickGattService
import de.maxr1998.lightstick.util.extension.isWritable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@SuppressLint("MissingPermission")
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private var shouldConnect = true
    private val scanning = MutableStateFlow(false)

    val connected: State<Boolean>
        get() = _connected
    private val _connected: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Used to signal unrecoverable errors
     */
    val error: State<ErrorState>
        get() = _error
    private val _error: MutableState<ErrorState> = mutableStateOf(ErrorState.NONE)

    private val gattClient: MutableStateFlow<BluetoothGatt?> = MutableStateFlow(null)
    private var lightStickGattService: LightStickGattService? = null

    private val bluetoothManager: BluetoothManager = checkNotNull(app.getSystemService()) {
        "BluetoothManager not found"
    }
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    private var bleScanning = false
    private val bleCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (device.address == Constants.LIGHT_STICK_ADDRESS) {
                when (callbackType) {
                    ScanSettings.CALLBACK_TYPE_FIRST_MATCH -> {
                        connect(device)
                        scanning.value = false
                    }
                }
            }
        }
    }
    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> when (newState) {
                    BluetoothGatt.STATE_CONNECTED -> {
                        Timber.v("Successfully connected to device")
                        scanning.value = false
                        gattClient.value = gatt
                        gatt.discoverServices()
                    }
                    BluetoothGatt.STATE_DISCONNECTED -> {
                        Timber.v("Successfully disconnected from device")
                        gatt.close()
                        clear()
                        shouldConnect = false
                    }
                }
                else -> {
                    Timber.w("Received error code %d, closing connection to retry", status)
                    gatt.close()
                    clear()
                    tryScan()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Timber.v("Services discovered")
                    val service = gatt.getService(Constants.SERVICE_UUID)
                    if (service == null) {
                        gatt.disconnect()
                        _error.value = ErrorState.MISSING_SERVICE
                        return
                    }
                    val colorCharacteristic = service.getCharacteristic(Constants.COLOR_CHARACTERISTIC_UUID)
                    val effectCharacteristic = service.getCharacteristic(Constants.EFFECT_CHARACTERISTIC_UUID)

                    if (colorCharacteristic == null || effectCharacteristic == null) {
                        gatt.disconnect()
                        _error.value = ErrorState.MISSING_CHARACTERISTIC
                        return
                    }

                    gatt.setCharacteristicNotification(colorCharacteristic, true)
                    gatt.setCharacteristicNotification(effectCharacteristic, true)

                    lightStickGattService = LightStickGattService(
                        gattService = service,
                        colorCharacteristic = colorCharacteristic,
                        effectCharacteristic = effectCharacteristic,
                    )
                }
                else -> {
                    gatt.disconnect()
                    _error.value = ErrorState.SERVICE_DISCOVERY_FAILURE
                }
            }
        }
    }

    init {
        viewModelScope.launch {
            scanning.collect(::updateScanState)
        }
        viewModelScope.launch {
            gattClient.collect { client ->
                _connected.value = client != null
            }
        }
    }

    private fun updateScanState(isScanning: Boolean) {
        if (!PermissionManager.hasBlePermission(getApplication())) return

        if (isScanning) {
            bleScanning = true
            val filter = ScanFilter.Builder()
                .setDeviceAddress(Constants.LIGHT_STICK_ADDRESS)
                .build()
            val scanSettings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH)
                .build()
            bluetoothLeScanner.startScan(listOf(filter), scanSettings, bleCallback)
        } else {
            bluetoothLeScanner.stopScan(bleCallback)
        }
    }

    fun tryScan() {
        if (shouldConnect) scanning.value = true
    }

    private fun connect(device: BluetoothDevice) {
        if (!PermissionManager.hasBlePermission(getApplication())) return

        device.connectGatt(getApplication(), false, bluetoothGattCallback)
    }

    @Suppress("OPT_IN_USAGE")
    fun setColor(color: Color): Boolean {
        val client = gattClient.value
        val characteristic = lightStickGattService?.colorCharacteristic
        if (client == null || characteristic == null || !characteristic.isWritable()) {
            return false
        }

        val colorsPacked = UByteArray(Int.SIZE_BYTES) { index ->
            @Suppress("MagicNumber")
            when {
                index < 3 -> {
                    val shiftWidth = 6 - index
                    ((color.value shr Byte.SIZE_BITS * shiftWidth) and UByte.MAX_VALUE.toULong()).toUByte()
                }
                else -> UByte.MIN_VALUE
            }
        }.toByteArray()

        characteristic.value = colorsPacked
        client.writeCharacteristic(characteristic)

        return true
    }

    fun launchEffect(effect: Effect): Boolean {
        val client = gattClient.value
        val characteristic = lightStickGattService?.effectCharacteristic
        if (client == null || characteristic == null || !characteristic.isWritable()) {
            return false
        }

        characteristic.value = effect.effectName.toByteArray()
        client.writeCharacteristic(characteristic)

        return true
    }

    private fun clear() {
        gattClient.value = null
        lightStickGattService = null
    }

    override fun onCleared() {
        Timber.v("Ended process, disconnecting…")
        scanning.value = false
        gattClient.value?.disconnect()
        clear()
    }
}