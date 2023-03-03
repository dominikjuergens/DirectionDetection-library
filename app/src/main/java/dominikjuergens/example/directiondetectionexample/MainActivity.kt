package dominikjuergens.example.directiondetectionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dominikjuergens.example.directiondetectionlibrary.GpsDirection
import dominikjuergens.example.directiondetectionlibrary.Somda
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Somda.SomdaListener, GpsDirection.GpsDirectionListener {

    // Views
    private lateinit var rawAzimuth: TextView
    private lateinit var somdaAzimuth: TextView
    private lateinit var stopButton: Button
    private lateinit var recordButton: Button
    private var gps: TextView? = null

    // Values
    private var writer: FileWriter? = null
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    // Sensor
    private lateinit var somda: Somda
    private var gpsDirection: GpsDirection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sensor setup
        somda = Somda(this)
        somda.start(this)
        gpsDirection = GpsDirection(this)
        gpsDirection?.start(this)

        // Views setup
        rawAzimuth = findViewById(R.id.rawAzimuth)
        somdaAzimuth = findViewById(R.id.somdaAzimuth)
        stopButton = findViewById(R.id.stop_button)
        recordButton = findViewById(R.id.record_button)
        gps = findViewById(R.id.gps)

        // Stop button
        stopButton.setOnClickListener {
            if (stopButton.text.toString() == "Stop") {
                somda.stop()
                gpsDirection?.stop()
                stopButton.text = "Start"
            } else if (stopButton.text.toString() == "Start") {
                somda.start(this)
                gpsDirection?.start(this)
                stopButton.text = "Stop"
            }
        }

        // Record button
        recordButton.setOnClickListener {
            if (recordButton.text.toString() == "Start Recording") {
                recordButton.text = "Stop Recording"
                val file = File(this.filesDir, "output.csv")
                writer = FileWriter(file, true)
                writer?.append("Time;Azimuth;After SOMDA")
            } else if (recordButton.text.toString() == "Stop Recording") {
                recordButton.text = "Start Recording"
                writer?.close()
                writer = null
                println("Data exported to CSV file: ${this.filesDir}")
            }
        }
    }

    override fun onSomdaChanged(degree: Float) {
        rawAzimuth.text = somda.azimuth.mod(360F).toString()
        somdaAzimuth.text = degree.toString()
        writer?.append("${format.format(Date())};${rawAzimuth.text};${somdaAzimuth.text}\n")
    }

    override fun onGPSDirectionChanged(gpsAzimuth: Float?) {
        gps?.text = gpsAzimuth.toString()
    }
}
