package de.maxr1998.lightstick

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import de.maxr1998.lightstick.permission.PermissionGrantedCallback
import de.maxr1998.lightstick.permission.PermissionManager
import de.maxr1998.lightstick.ui.ComposeApp

class MainActivity : ComponentActivity(), PermissionGrantedCallback {

    private val mainViewModel: MainViewModel by viewModels()
    private val permissionManager: PermissionManager = PermissionManager(this, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeApp()
        }

        if (PermissionManager.hasBlePermission(this)) {
            mainViewModel.tryScan()
        } else {
            permissionManager.requestBlePermission()
        }
    }

    override fun onPermissionGranted(permission: String) {
        if (PermissionManager.hasBlePermission(this)) {
            mainViewModel.tryScan()
        }
    }

    override fun onPermissionRejected(permission: String) {
        Toast.makeText(this, R.string.ble_permission_required, Toast.LENGTH_SHORT).show()
        finish()
    }
}