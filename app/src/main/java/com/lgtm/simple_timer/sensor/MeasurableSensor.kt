package com.lgtm.simple_timer.sensor


// https://github.com/philipplackner/SensorGuide
// This is an abstract sensor class without any android dependencies
// Inject concrete sensor as MeasurableSensor to the ViewModel
abstract class MeasurableSensor(
    protected val sensorType: Int
) {

    protected var onSensorValueChanged: ((List<Float>) -> Unit)? = null

    abstract val doesSensorExist: Boolean

    abstract fun startListening()
    abstract fun stopListening()

    fun setOnSensorValuesChangedListener(listener: (List<Float>) -> Unit) {
        onSensorValueChanged = listener
    }

}