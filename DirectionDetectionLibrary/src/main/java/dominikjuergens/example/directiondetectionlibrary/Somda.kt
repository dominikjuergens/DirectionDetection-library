package dominikjuergens.example.directiondetectionlibrary

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Somda {

    companion object {

        private var azimuth: Float = 0F
        private var pitch: Float = 0F
        private var roll: Float = 0F
        private var zAcc: Float = 0F

        /**
         * takes the fragments sensorManager, the fragment as the sensorEventListener and the
         * sensorEvent from the onSensorChanged function
         */
        fun doSomda(mSensorManager: SensorManager, sensorEventListener: SensorEventListener,
                    event: SensorEvent): Int {
            //register and start sensors
            DirectionSensors.startSensorListener(mSensorManager, sensorEventListener)
            //get sensor values
            getSensorValues(event)


            return 42
        }

        fun stopSomda(mSensorManager: SensorManager, sensorEventListener: SensorEventListener) {
            DirectionSensors.stopSensorListener(mSensorManager, sensorEventListener)
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
    }
}