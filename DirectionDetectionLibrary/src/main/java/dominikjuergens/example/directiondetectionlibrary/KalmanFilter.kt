package dominikjuergens.example.directiondetectionlibrary

class KalmanFilter(private val measurementUncertainty: Float) {

    //error covariance
    private var estimateUncertainty = 1f

    //current estimation (filtered bearing value)
    private var currentEstimate = 0f

    //weight versus previous estimation
    private var kalmanGain = 0f

    fun filter(rawBearing: Float): Float {
        kalmanGain = estimateUncertainty / (estimateUncertainty + measurementUncertainty)
        currentEstimate += kalmanGain * (rawBearing - currentEstimate)
        estimateUncertainty *= (1 - kalmanGain)
        return currentEstimate
    }
}