package de.maxr1998.lightstick.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class PermissionManager(
    activity: ComponentActivity,
    callback: PermissionGrantedCallback,
) {
    private val requestPermissionsLauncher: ActivityResultLauncher<Array<String>>

    init {
        val contract = ActivityResultContracts.RequestMultiplePermissions()
        requestPermissionsLauncher = activity.registerForActivityResult(contract) { permissions ->
            permissions.forEach { (permission, granted) ->
                if (granted) {
                    callback.onPermissionGranted(permission)
                } else {
                    callback.onPermissionRejected(permission)
                }
            }
        }
    }

    fun requestPermissions(permissions: Array<String>) {
        requestPermissionsLauncher.launch(permissions)
    }

    fun requestBlePermission() {
        requestPermissions(bluetoothPermissions)
    }

    companion object {
        private val bluetoothPermissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            )
            else -> arrayOf(Manifest.permission.BLUETOOTH)
        }

        fun hasBlePermission(context: Context): Boolean = bluetoothPermissions.all { permission ->
            context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}