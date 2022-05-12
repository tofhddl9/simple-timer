package com.lgtm.default_Android_Project_Template.sensor

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