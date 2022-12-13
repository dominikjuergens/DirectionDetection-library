package dominikjuergens.example.directiondetectionlibrary

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class GPSDirection (private val context: Context) {

    private var callback: GPSDirectionListener? = null
    private var sensorEventListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null

    fun start(onInteractionListener: GPSDirectionListener) {
        callback = onInteractionListener
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Create a SensorEventListener
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val correctedAzimuth = doGPSDirectionDetection(event)
                callback?.onGPSDirectionChanged(correctedAzimuth)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle accuracy changes here
            }
        }

        DirectionSensors.startGPSDirectionListener(
            sensorManager!!, sensorEventListener as SensorEventListener
        )
    }

    private fun doGPSDirectionDetection(event: SensorEvent): Float {
        TODO("Not yet implemented")
    }

    fun stop() {
        DirectionSensors.stopGPSDirectionListener(
            sensorManager!!,
            sensorEventListener as SensorEventListener
        )
        callback = null
        sensorManager = null
        sensorEventListener = null
    }

    interface GPSDirectionListener {
        fun onGPSDirectionChanged(degree: Float)
    }
}