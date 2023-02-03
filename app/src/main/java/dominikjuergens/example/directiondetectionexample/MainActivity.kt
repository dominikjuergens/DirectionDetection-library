package dominikjuergens.example.directiondetectionexample

import android.hardware.SensorEventListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dominikjuergens.example.directiondetectionlibrary.DirectionSensors
import dominikjuergens.example.directiondetectionlibrary.Somda

class MainActivity : AppCompatActivity(), Somda.SomdaListener {

    private lateinit var rawAzimuth: TextView
    private lateinit var somdaAzimuth: TextView
    private lateinit var stopButton: Button

    private lateinit var s: Somda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s = Somda(this)
        s.start(this)

        rawAzimuth = findViewById(R.id.rawAzimuth)
        somdaAzimuth = findViewById(R.id.somdaAzimuth)
        stopButton = findViewById(R.id.stop_button)

        stopButton.setOnClickListener {
            s.stop()
        }

    }

    override fun onSomdaChanged(degree: Float) {
        rawAzimuth.text = s.azimuth.toString()
        somdaAzimuth.text = degree.toString()
    }
}