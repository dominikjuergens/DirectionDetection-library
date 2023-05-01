package dominikjuergens.example.directiondetectionlibrary

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat

class GpsDirectionKalman(private val context: Context, private val maxLocations: Int = 10, private val minDistanceMeters: Float = 2f) {

    private lateinit var callback: KalmanListener
    private lateinit var locationListener: LocationListener
    private lateinit var locationManager: LocationManager

    private val locationList: MutableList<Location> = mutableListOf()
    private val kalmanFilter = KalmanFilter(processUncertainty = 10f, initialEstimateUncertainty = 1f)

    fun start(onInteractionListener: KalmanListener) {
        if (!checkGpsPermission()) {
            return
        }

        callback = onInteractionListener
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { currentLocation ->
            val gpsAzimuth = doGpsKalman(currentLocation)
            gpsAzimuth?.let { callback.onKalmanChanged(it) }
        }

        DirectionSensors.startKalmanListener(
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

    fun doGpsKalman(currentLocation: Location): Float? {
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

        val bearings = mutableListOf<Float>()
        for (i in 0 until locationList.size - 1) {
            val rawBearing = locationList[i].bearingTo(locationList[i + 1]).mod(360F)
            val filteredBearing = kalmanFilter.filter(rawBearing)
            bearings.add(filteredBearing)
        }

        return bearings.average().toFloat()
    }

    fun stop() {
        DirectionSensors.stopKalmanListener(
            locationManager,
            locationListener
        )
    }

    interface KalmanListener {
        fun onKalmanChanged(kalmanAzimuth: Float)
    }
}
