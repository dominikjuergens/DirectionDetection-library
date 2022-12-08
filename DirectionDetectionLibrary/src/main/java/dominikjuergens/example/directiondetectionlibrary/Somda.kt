package dominikjuergens.example.directiondetectionlibrary

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Somda(context: Context) {

    private var callback: SomdaListener? = null

    fun start(onInteractionListener: SomdaListener) {
        callback = onInteractionListener
        // TODO do your lib stuff
        callback?.onSomdaChanged(42.0F)
    }

    fun stop() {
        callback = null
        // TODO stop your lib stuff
    }

    interface SomdaListener {
        fun onSomdaChanged(degree: Float)
    }

    private var azimuth: Float = 0F
    private var pitch: Float = 0F
    private var roll: Float = 0F
    private var zAcc: Float = 0F

    /**
     * takes the fragments sensorManager, the fragment as the sensorEventListener and the
     * sensorEvent from the onSensorChanged function
     */
    fun doSomdaAlgorithm(mSensorManager: SensorManager, sensorEventListener: SensorEventListener,
                event: SensorEvent): Float {
        //get sensor values
        getSensorValues(event)
        //roll correction
        val correctedAzimuth = rollCorrection()
        //TODO windowed peak algorithm for further correction
        return 42F
    }

    private fun getSensorValues(event: SensorEvent){
        if(event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) { //get euler angles
            val orientations = DirectionSensors.getEulerAngles(event)
            azimuth = orientations.first
            pitch = orientations.second
            roll = orientations.third
        } else if(event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) { //get linear z-Axis Acceleration
            zAcc = DirectionSensors.getZAcceleration(event)
        }
    }

    private fun rollCorrection(): Float {
        if(pitch < 0) {
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
        if(angle >= 360) {
            return (angle - 360)
        } else if (angle < 0) {
            return (angle + 360)
        } else {
            return angle
        }
    }
}