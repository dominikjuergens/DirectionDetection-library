package dominikjuergens.example.directiondetectionlibrary

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DirectionSensors {
    //initializes sensors needed for SOMDA

    companion object {
        private var rotVecSensor: Sensor? = null
        private var accSensor: Sensor? = null

        /**
         * need to call this function to register the rotation and accelerometer sensor needed for
         * somda
         */
        fun startSensorListener(mSensorManager: SensorManager, sensorEventListener: SensorEventListener) {
            rotVecSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            if(rotVecSensor == null) {
                sensorNotAvailable()
                return
            } else if(accSensor == null) {
                sensorNotAvailable()
                return
            }
            mSensorManager.registerListener(sensorEventListener, rotVecSensor, SensorManager.SENSOR_DELAY_UI)
            mSensorManager.registerListener(sensorEventListener, accSensor, SensorManager.SENSOR_DELAY_UI)
        }

        /**
         * need to call this function to unregister the rotation and accelerometer sensor after use
         */
        fun stopSensorListener(mSensorManager: SensorManager, sensorEventListener: SensorEventListener) {
            mSensorManager.unregisterListener(sensorEventListener, rotVecSensor)
            mSensorManager.unregisterListener(sensorEventListener, accSensor)
        }

        /**
         * returns the three euler angles as a Triple
         * you need to input the event from the onSensorChanged function of the Fragment where the
         * function is called
         *
         * You have to differentiate between event types before calling getEulerAngles or
         * getZAcceleration
         */
        fun getEulerAngles(event: SensorEvent): Triple<Float, Float, Float> {
            if(rotVecSensor == null) {
                sensorNotInitialized()
                return Triple(-1F, -1F, -1F)
            }
            val rotMat = FloatArray(9)
            //Calculate the rotation matrix with the values from the event
            SensorManager.getRotationMatrixFromVector(rotMat, event.values)

            val orientation = FloatArray(3)
            //Calculate the orientations with the rotation matrix
            SensorManager.getOrientation(rotMat, orientation)

            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
            val roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

            //return the three euler angles
            return Triple(azimuth, pitch, roll)
        }

        /**
         * returns linear z-Acceleration as a float value
         */
        fun getZAcceleration(event: SensorEvent): Float {
            if (accSensor == null) {
                sensorNotInitialized()
                return -1F
            }
            return event.values[2]
        }

        private fun sensorNotAvailable() {
            println("Error getting sensor")
        }

        private fun sensorNotInitialized() {
            println("Sensor not initialized")
        }
    }
}