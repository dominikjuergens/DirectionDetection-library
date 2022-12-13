package dominikjuergens.example.directiondetectionlibrary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GpsDirection (private val context: Context) {

    private var callback: GpsDirectionListener? = null
    private var sensorEventListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null

    fun start(onInteractionListener: GpsDirectionListener) {

        //request necessary permissions
        requestGpsPermission()

        callback = onInteractionListener
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Create a SensorEventListener
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val gpsAzimuth = doGpsDirectionDetection(event)
                callback?.onGPSDirectionChanged(gpsAzimuth)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle accuracy changes here
            }
        }

        DirectionSensors.startGPSDirectionListener(
            sensorManager!!, sensorEventListener as SensorEventListener
        )
    }

    private val requestCodeGpsPermission = 1

    private fun requestGpsPermission() {
        val activity = context as Activity

        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(context, coarseLocationPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, fineLocationPermission) != PackageManager.PERMISSION_GRANTED) {
            // Permission has not been granted, request it
            ActivityCompat.requestPermissions(activity, arrayOf(coarseLocationPermission, fineLocationPermission), requestCodeGpsPermission)
        } else {
            // Permission has already been granted, we can access the GPS
        }
    }





    private fun doGpsDirectionDetection(event: SensorEvent): Float {
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

    interface GpsDirectionListener {
        fun onGPSDirectionChanged(degree: Float)
    }
}