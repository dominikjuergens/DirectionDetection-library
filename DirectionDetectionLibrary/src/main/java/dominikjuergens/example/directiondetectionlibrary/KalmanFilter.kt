package dominikjuergens.example.directiondetectionlibrary

class KalmanFilter(private val processUncertainty: Float, private val initialEstimateUncertainty: Float) {

    //error covariance
    private var estimateUncertainty = initialEstimateUncertainty

    //current estimation (filtered bearing value)
    private var currentEstimate = 0f

    //weight versus previous estimation
    private var kalmanGain = 0f

    fun filter(rawBearing: Float): Float {
        // Predict step
        estimateUncertainty += processUncertainty

        // Update step
        kalmanGain = estimateUncertainty / (estimateUncertainty + initialEstimateUncertainty)
        currentEstimate += kalmanGain * (rawBearing - currentEstimate)
        estimateUncertainty *= (1 - kalmanGain)
        return currentEstimate
    }
}
