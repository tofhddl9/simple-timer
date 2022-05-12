package com.lgtm.simple_timer.utils

import android.content.Context
import android.location.LocationManager
import android.view.WindowManager

val Context.locationManager : LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

val Context.windowManager : WindowManager
    get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager