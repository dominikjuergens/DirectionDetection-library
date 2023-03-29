package dominikjuergens.example.directiondetectionlibrary

class KalmanFilter(private val r: Float, private val q: Float) {
    private var p = 1f
    private var x = 0f
    private var k = 0f

    fun filter(z: Float): Float {
        k = p / (p + r)
        x += k * (z - x)
        p = (1 - k) * p + q
        return x
    }
}