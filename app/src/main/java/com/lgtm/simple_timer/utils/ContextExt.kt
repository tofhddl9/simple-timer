package com.lgtm.simple_timer.utils

import android.content.Context
import android.location.LocationManager
import android.view.View
import android.view.WindowManager
import com.google.android.material.snackbar.Snackbar

val Context.locationManager : LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

val Context.windowManager : WindowManager
    get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
