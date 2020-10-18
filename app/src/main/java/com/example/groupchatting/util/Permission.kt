package com.example.groupchatting.util

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Permission {

    fun hasPermission(context : Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
}