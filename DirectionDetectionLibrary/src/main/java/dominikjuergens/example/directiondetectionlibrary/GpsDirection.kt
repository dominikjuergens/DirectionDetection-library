package dominikjuergens.example.directiondetectionlibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class GpsDirection(private val context: Context, private val maxLocations: Int = 10, private val minDistanceMeters: Float = 2f) {

    private lateinit var callback: GpsDirectionListener
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    private var latitude: Double? = null
    private var longitude: Double? = null

    private val locationList: MutableList<Location> = mutableListOf()

    fun start(onInteractionListener: GpsDirectionListener) {
        if (!checkGpsPermission()) {
            return
        }

        callback = onInteractionListener
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { currentLocation ->
            val gpsAzimuth = doGpsDirectionDetection(currentLocation)
            latitude = currentLocation.latitude
            longitude = currentLocation.longitude
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
        if (locationList.isNotEmpty()) {
            val lastLocation = locationList.last()
            val distance = lastLocation.distanceTo(currentLocation)
            if (distance >= minDistanceMeters) {
                if (locationList.size >= maxLocations) {
                    locationList.removeAt(0)
                }
                locationList.add(currentLocation)
            }
        } else {
            locationList.add(currentLocation)
        }

        if (locationList.size < 2) {
            return null
        }

        val sumBearings = locationList.subList(0, locationList.size - 1).mapIndexed { index, location ->
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

    fun getLatitude(): Double? {
        return this.latitude
    }

    fun getLongitude(): Double? {
        return this.longitude
    }

    interface GpsDirectionListener {
        fun onGPSDirectionChanged(gpsAzimuth: Float)
    }
}
