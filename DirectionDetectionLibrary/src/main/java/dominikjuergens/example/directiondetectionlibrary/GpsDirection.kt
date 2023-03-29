package dominikjuergens.example.directiondetectionlibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class GpsDirection(private val context: Context, private val maxLocations: Int = 10) {

    private lateinit var callback: GpsDirectionListener
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    private val locationList: MutableList<Location> = mutableListOf()

    fun start(onInteractionListener: GpsDirectionListener) {
        if (!checkGpsPermission()) {
            return
        }

        callback = onInteractionListener
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { currentLocation ->
            val gpsAzimuth = doGpsDirectionDetection(currentLocation)
            gpsAzimuth?.let { callback.onGPSDirectionChanged(it) }
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
        if (locationList.size >= maxLocations) {
            locationList.removeAt(0)
        }
        locationList.add(currentLocation)

        if (locationList.size < 2) {
            return null
        }

        val sumBearings = locationList.mapIndexed { index, location ->
            location.bearingTo(locationList[index + 1]).mod(360F)
        }.sum()

        return sumBearings / (locationList.size - 1)
    }

    fun stop() {
        DirectionSensors.stopGPSDirectionListener(
            locationManager,
            locationListener
        )
    }

    interface GpsDirectionListener {
        fun onGPSDirectionChanged(gpsAzimuth: Float)
    }
}
