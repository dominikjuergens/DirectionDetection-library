package dominikjuergens.example.directiondetectionlibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class GpsDirection (private val context: Context) {

    private lateinit var callback: GpsDirectionListener
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    private var previousLocation: Location? = null

    fun start(onInteractionListener: GpsDirectionListener) {

        //request necessary permissions
        if(!checkGpsPermission()){
            return
        }

        callback = onInteractionListener
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Create a SensorEventListener
        locationListener = object : LocationListener {
            override fun onLocationChanged(currentLocation: Location) {
                val gpsAzimuth = doGpsDirectionDetection(currentLocation)
                callback.onGPSDirectionChanged(gpsAzimuth)
            }

        }

        DirectionSensors.startGPSDirectionListener(
            locationManager, locationListener
        )
    }

    private fun checkGpsPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("GPS permission", "GPS permission not granted")
            throw Exception("GPS permission not granted")
        }

        return true
    }

    fun doGpsDirectionDetection(currentLocation: Location): Float? {
        if (previousLocation == null) {
            previousLocation = currentLocation
            return null
        }

        val bearing = previousLocation?.bearingTo(currentLocation)
        previousLocation = currentLocation

        return bearing
    }

    fun stop() {
        DirectionSensors.stopGPSDirectionListener(
            locationManager,
            locationListener
        )
    }

    interface GpsDirectionListener {
        fun onGPSDirectionChanged(gpsAzimuth: Float?)
    }
}