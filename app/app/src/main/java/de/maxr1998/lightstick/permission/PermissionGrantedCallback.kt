package de.maxr1998.lightstick.permission

interface PermissionGrantedCallback {
    fun onPermissionGranted(permission: String)

    fun onPermissionRejected(permission: String)
}