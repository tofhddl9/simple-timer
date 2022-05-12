package com.lgtm.simple_timer.sensor

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor

/**
 * Example of using AndroidSensor
 * */
class LightSensor(
    context: Context,
) : AndroidSensor(
    context = context,
    sensorFeature = PackageManager.FEATURE_SENSOR_LIGHT,
    sensorType = Sensor.TYPE_LIGHT)