package dominikjuergens.example.directiondetectionlibrary

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager

class DirectionSensors {
    //initializes sensors needed for SOMDA

    companion object {
        private var rotVecSensor: Sensor? = null
        private var accSensor: Sensor? = null

        private var azimuth = 0F
        private var pitch = 0F
        private var roll = 0F

        /**
         * need to call this function to register the rotation and accelerometer sensor needed for
         * somda
         */
        fun startSomdaSensorListener(mSensorManager: SensorManager, sensorEventListener: SensorEventListener) {
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
        fun stopSomdaSensorListener(mSensorManager: SensorManager, sensorEventListener: SensorEventListener) {
            mSensorManager.unregisterListener(sensorEventListener, rotVecSensor)
            mSensorManager.unregisterListener(sensorEventListener, accSensor)
        }

        @SuppressLint("MissingPermission")
        fun startGPSDirectionListener(mLocationManager: LocationManager, locationListener: LocationListener) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }

        @SuppressLint("MissingPermission")
        fun stopGPSDirectionListener(mLocationManager: LocationManager, locationListener: LocationListener) {
            mLocationManager.removeUpdates(locationListener)
        }

        @SuppressLint("MissingPermission")
        fun startKalmanListener(mLocationManager: LocationManager, locationListener: LocationListener) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }

        @SuppressLint("MissingPermission")
        fun stopKalmanListener(mLocationManager: LocationManager, locationListener: LocationListener) {
            mLocationManager.removeUpdates(locationListener)
        }



        /**
         * returns the three euler angles as a Triple
         * you need to input the event from the onSensorChanged function of the Fragment where the
         * function is called
         *
         * You have to differentiate between event types before calling getEulerAngles or
         * getZAcceleration
         */
        fun getEulerAngles(event: SensorEvent): EulerAngles {
            if(rotVecSensor == null) {
                sensorNotInitialized()
                return EulerAngles(-1F, -1F, -1F)
            }
            val rotMat = FloatArray(9)
            //Calculate the rotation matrix with the values from the event
            SensorManager.getRotationMatrixFromVector(rotMat, event.values)

            val orientation = FloatArray(3)
            //Calculate the orientations with the rotation matrix
            SensorManager.getOrientation(rotMat, orientation)

            azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
            roll = Math.toDegrees(orientation[2].toDouble()).toFloat()

            //return the three euler angles
            return EulerAngles(azimuth, pitch, roll)
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