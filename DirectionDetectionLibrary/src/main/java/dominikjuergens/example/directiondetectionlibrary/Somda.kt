package dominikjuergens.example.directiondetectionlibrary

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Somda(private val context: Context) {

    private var callback: SomdaListener? = null
    private var sensorEventListener: SensorEventListener? = null
    private var sensorManager: SensorManager? = null

    private var azimuth: Float = 0F
    private var pitch: Float = 0F
    private var roll: Float = 0F
    private var zAcc: Float = 0F

    fun start(onInteractionListener: SomdaListener) {
        callback = onInteractionListener
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Create a SensorEventListener
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val correctedAzimuth = doSomdaAlgorithm(event)
                callback?.onSomdaChanged(correctedAzimuth)
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                // Handle accuracy changes here
            }
        }

        DirectionSensors.startSensorListener(
            sensorManager!!, sensorEventListener as SensorEventListener
        )
    }

    fun stop() {
        DirectionSensors.stopSensorListener(
            sensorManager!!,
            sensorEventListener as SensorEventListener
        )
        callback = null
        sensorManager = null
        sensorEventListener = null
    }

    interface SomdaListener {
        fun onSomdaChanged(degree: Float)
    }

    /**
     * takes the fragments sensorManager, the fragment as the sensorEventListener and the
     * sensorEvent from the onSensorChanged function
     */
    fun doSomdaAlgorithm(event: SensorEvent): Float {
        //get sensor values
        refreshSensorValues(event)
        //roll correction
        val correctedAzimuth = rollCorrection()
        //TODO windowed peak algorithm for further correction
        return 42F
    }

    private fun refreshSensorValues(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) { //get euler angles
            val eulerAngles = DirectionSensors.getEulerAngles(event)
            azimuth = eulerAngles.azimuth
            pitch = eulerAngles.pitch
            roll = eulerAngles.roll
        } else if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) { //get linear z-Axis Acceleration
            zAcc = DirectionSensors.getZAcceleration(event)
        }
    }

    private fun rollCorrection(): Float {
        if (pitch < 0) {
            return (calculateAngle(azimuth + roll))
        } else { // (pitch >= 0)
            return (calculateAngle(azimuth - roll))
        }
    }

    /**
     * because angles can only be between 0 and 359, it has to be checked for undefined angles
     * after the addition of two angles
     */
    private fun calculateAngle(angle: Float): Float {
        return angle % 360
    }
}