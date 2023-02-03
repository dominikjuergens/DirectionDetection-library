package dominikjuergens.example.directiondetectionlibrary

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import java.util.*
import kotlin.math.abs

class Somda(private val context: Context) {

    private lateinit var callback: SomdaListener
    private lateinit var sensorEventListener: SensorEventListener
    private lateinit var sensorManager: SensorManager

    var azimuth: Float = 0F
    private var pitch: Float = 0F
    private var roll: Float = 0F

    private val zAccData = LinkedList<Float>()
    private val sensorSamplingFrequency = (1/0.06) //every 60ms --> 16.67Hz
    private val windowSize = (1.6 * sensorSamplingFrequency).toInt() //1.6s window size
    private val threshold = 2.3 //2.3m/s^2

    fun start(onInteractionListener: SomdaListener) {
        callback = onInteractionListener
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Create a SensorEventListener
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val correctedAzimuth = doSomdaAlgorithm(event)
                callback.onSomdaChanged(correctedAzimuth)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle accuracy changes here
            }
        }

        DirectionSensors.startSomdaSensorListener(
            sensorManager, sensorEventListener
        )
    }

    fun stop() {
        DirectionSensors.stopSomdaSensorListener(
            sensorManager,
            sensorEventListener
        )
    }

    interface SomdaListener {
        fun onSomdaChanged(degree: Float)
    }

    /**
     * takes the sensorEvent from the onSensorChanged function and does the somda algorithm
     */
    fun doSomdaAlgorithm(event: SensorEvent): Float {
        //get sensor values
        refreshSensorValues(event)
        //roll correction
        val correctedAzimuth = rollCorrection()
        val zAccPeak = findPeak()
        var finalAzimuth = 0F
        if((pitch < 0 && zAccPeak < 0) || (pitch > 0 && zAccPeak > 0)) {
            finalAzimuth = correctedAzimuth
        } else if((pitch < 0 && zAccPeak > 0) || (pitch > 0 && zAccPeak < 0)) {
            finalAzimuth = calculateAngle(correctedAzimuth + 180)
        }
        return finalAzimuth
    }

    private fun refreshSensorValues(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) { //get euler angles
            val eulerAngles = DirectionSensors.getEulerAngles(event)
            azimuth = calculateAngle(eulerAngles.azimuth)
            pitch = eulerAngles.pitch
            roll = eulerAngles.roll
        } else if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) { //get linear z-Axis Acceleration
            val zAcc = DirectionSensors.getZAcceleration(event)
            //add the newest value to the list for the windowed peak algorithm
            zAccData.addLast(zAcc)

            //ensures, that the data list only contains the values inside the 1.6s window
            if(zAccData.size > windowSize) {
                zAccData.removeFirst()
            }
        }
    }

    private fun rollCorrection(): Float {
        return calculateAngle(azimuth + if (pitch < 0) roll else -roll)
    }

    /**
     * because angles can only be between 0 and 359, it has to be checked for undefined angles
     * after the addition of two angles
     */
    private fun calculateAngle(angle: Float): Float {
        return angle.mod(360F)
    }

    /**
     * windowed peak algorithm
     */
    private fun findPeak(): Float {
        // Check the input data and window size
        if (zAccData.isEmpty()) {
            return 0.0F
        }

        // Keep track of the current peak
        var peak = 0.0F
        var peakIndex = -1

        // Iterate over the data and find the peak
        val window = zAccData
        //transform values in window to their absolute values
        for(i in 0 until window.size) {
            window[i] = abs(window[i])
        }
        //find index of max value
        for (i in 0 until zAccData.size) {
            // Update the peak if a larger value is found
            if (window[i] > peak && window[i] > threshold) {
                peak = window[i]
                peakIndex = i
            }
        }
        return if(peakIndex == -1) {
            0.0F
        } else {
            zAccData[peakIndex]
        }
    }
}