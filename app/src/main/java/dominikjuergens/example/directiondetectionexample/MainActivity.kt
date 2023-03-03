package dominikjuergens.example.directiondetectionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dominikjuergens.example.directiondetectionlibrary.Somda
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Somda.SomdaListener {

    // Views
    private lateinit var rawAzimuth: TextView
    private lateinit var somdaAzimuth: TextView
    private lateinit var stopButton: Button
    private lateinit var recordButton: Button

    // Values
    private val csvFile = File("output.csv")
    private var writer = FileWriter(csvFile, true)
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    // Sensor
    private lateinit var somda: Somda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sensor setup
        somda = Somda(this)
        somda.start(this)

        // Views setup
        rawAzimuth = findViewById(R.id.rawAzimuth)
        somdaAzimuth = findViewById(R.id.somdaAzimuth)
        stopButton = findViewById(R.id.stop_button)
        recordButton = findViewById(R.id.record_button)

        // Stop button
        stopButton.setOnClickListener {
            if (stopButton.text.toString() == "Stop") {
                somda.stop()
                stopButton.text = "Start"
            } else if (stopButton.text.toString() == "Start") {
                somda.start(this)
                stopButton.text = "Stop"
            }
        }

        // Record button
        recordButton.setOnClickListener {
            if (recordButton.text.toString() == "Start Recording") {
                recordButton.text = "Stop Recording"
                writer = FileWriter(csvFile, true)
                writer.append("Time;Azimuth;After SOMDA")
            } else if (recordButton.text.toString() == "Stop Recording") {
                recordButton.text = "Start Recording"
                writer.close()
                println("Data exported to CSV file: ${csvFile.absolutePath}")
            }
        }
    }

    override fun onSomdaChanged(degree: Float) {
        rawAzimuth.text = somda.azimuth.mod(360F).toString()
        somdaAzimuth.text = degree.toString()
        writer.append("${format.format(Date())};${rawAzimuth.text};${somdaAzimuth.text}\n")
    }
}
