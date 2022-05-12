package com.lgtm.default_Android_Project_Template.utils

import android.content.Context
import android.location.LocationManager
import android.view.WindowManager

val Context.locationManager : LocationManager
    get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager

val Context.windowManager : WindowManager
    get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager