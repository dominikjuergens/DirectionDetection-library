package dominikjuergens.example.directiondetectionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dominikjuergens.example.directiondetectionlibrary.Somda

abstract class MainActivity : AppCompatActivity(), Somda.SomdaListener {

    private lateinit var rawAzimuth: TextView
    private lateinit var somdaAzimuth: TextView
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = Somda(this)
        s.start(this)
        //TODO Button to stop Somda (s.stop())

        rawAzimuth = findViewById(R.id.rawAzimuth)
        somdaAzimuth = findViewById(R.id.somdaAzimuth)
        stopButton = findViewById(R.id.stop_button)

        stopButton.setOnClickListener {
            s.stop()
        }

    }

    override fun onSomdaChanged(degree: Float) {
        TODO("Not yet implemented")
    }
}