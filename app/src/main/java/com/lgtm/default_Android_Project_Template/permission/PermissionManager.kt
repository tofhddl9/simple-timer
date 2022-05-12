package com.lgtm.default_Android_Project_Template.permission

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class PermissionManager(private val activity: Activity) {

    private val permissionRequests = mutableMapOf<Int, PermissionRequest>()

    fun setPermissions(requestCode: Int, permissions: Array<String>): PermissionRequest.Builder {
        return PermissionRequest.Builder(this, requestCode, permissions)
    }

    fun handleRequestPermissionResult(requestCode: Int, grantResult: IntArray) {
        permissionRequests[requestCode]?.let { permissionRequest ->
            if (hasAllRequiredPermissionsGranted(grantResult)) {
                permissionRequest.onPermissionGranted?.invoke()
            } else {
                permissionRequest.onPermissionDenied?.invoke()
            }
        }

        removePermissionRequest(requestCode)
    }

    private fun hasAllRequiredPermissionsGranted(grantResults: IntArray): Boolean {
        grantResults.forEach { grantResult ->
            if ( grantResult != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun removePermissionRequest(requestCode: Int) {
        permissionRequests.remove(requestCode)
    }

    private fun requestPermission(permissionRequest: PermissionRequest) {
        if (!hasAllRequiredPermissions(permissionRequest.permissions)) {
            requestAllRequiredPermissions(permissionRequest)
        }
    }

    private fun hasAllRequiredPermissions(permissions: Array<String>): Boolean {
        permissions.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestAllRequiredPermissions(permissionRequest: PermissionRequest) {
        addPermissionRequest(permissionRequest)
        ActivityCompat.requestPermissions(activity, permissionRequest.permissions, permissionRequest.requestCode)
    }

    private fun addPermissionRequest(permissionRequest: PermissionRequest) {
        permissionRequests[permissionRequest.requestCode] = permissionRequest
    }


    class PermissionRequest private constructor(
        val requestCode: Int,
        val permissions: Array<String>,
        val onPermissionGranted: (() -> Unit)? = null,
        val onPermissionDenied: (() -> Unit)? = null,
    ) {

        class Builder(
            private val permissionManager: PermissionManager,
            private val requestCode: Int,
            private val permissions: Array<String>,
            private var onPermissionGranted: (() -> Unit)? = null,
            private var onPermissionDenied: (() -> Unit)? = null,
        ) {

            fun onPermissionGranted(callback : () -> Unit) = apply {
                onPermissionGranted = callback
            }

            fun onPermissionDenied(callback: () -> Unit) = apply {
                onPermissionDenied = callback
            }

            fun request() {
                val request = PermissionRequest(requestCode, permissions, onPermissionGranted, onPermissionDenied)
                permissionManager.requestPermission(request)
            }

        }
    }
}
