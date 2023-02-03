package dominikjuergens.example.directiondetectionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dominikjuergens.example.directiondetectionlibrary.Somda

abstract class MainActivity : AppCompatActivity(), Somda.SomdaListener {

    private lateinit var value1Text: TextView
    private lateinit var value2Text: TextView
    private lateinit var stopButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = Somda(this)
        s.start(this)
        //TODO Button to stop Somda (s.stop())

        value1Text = findViewById(R.id.rawAzimuth)
        value2Text = findViewById(R.id.somdaAzimuth)
        stopButton = findViewById(R.id.stop_button)

        stopButton.setOnClickListener {
            s.stop()
        }

    }

    override fun onSomdaChanged(degree: Float) {
        TODO("Not yet implemented")
    }
}