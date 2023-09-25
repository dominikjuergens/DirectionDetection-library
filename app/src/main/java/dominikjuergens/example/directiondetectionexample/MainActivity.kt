package dominikjuergens.example.directiondetectionexample

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dominikjuergens.example.directiondetectionlibrary.GpsDirection
import dominikjuergens.example.directiondetectionlibrary.GpsDirectionKalman
import dominikjuergens.example.directiondetectionlibrary.KalmanFilter
import dominikjuergens.example.directiondetectionlibrary.Somda
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), Somda.SomdaListener, GpsDirection.GpsDirectionListener, GpsDirectionKalman.KalmanListener {

    // Views
    private lateinit var rawAzimuth: TextView
    private lateinit var somdaAzimuth: TextView
    private lateinit var stopButton: Button
    private lateinit var recordButton: Button
    private var gps: TextView? = null
    private var gpsKalman: TextView? = null
    private var somdaKalmanAzimuth: TextView? = null
    private val kalmanFilter = KalmanFilter(processUncertainty = 10f, initialEstimateUncertainty = 1f)
    private var somdaKalman: Float? = null

    // Values
    private var writer: FileWriter? = null
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    // Sensor
    private lateinit var somda: Somda
    private var gpsDirection: GpsDirection? = null
    private var gpsDirectionKalman: GpsDirectionKalman? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Sensor setup
        somda = Somda(this)
        somda.start(this)
        gpsDirection = GpsDirection(this)
        gpsDirectionKalman = GpsDirectionKalman(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            //Permission already granted
            gpsDirection?.start(this)
            gpsDirectionKalman?.start(this)
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
            gpsDirection?.start(this)
            gpsDirectionKalman?.start(this)
        }

        // Views setup
        rawAzimuth = findViewById(R.id.rawAzimuth)
        somdaAzimuth = findViewById(R.id.somdaAzimuth)
        somdaKalmanAzimuth = findViewById(R.id.somdaKalmanAzimuth)
        stopButton = findViewById(R.id.stop_button)
        recordButton = findViewById(R.id.record_button)
        gps = findViewById(R.id.gps)
        gpsKalman = findViewById(R.id.gpsKalman)

        // Stop button
        stopButton.setOnClickListener {
            if (stopButton.text.toString() == "Stop") {
                somda.stop()
                gpsDirection?.stop()
                gpsDirectionKalman?.stop()
                stopButton.text = "Start"
            } else if (stopButton.text.toString() == "Start") {
                somda.start(this)
                gpsDirection?.start(this)
                gpsDirectionKalman?.start(this)
                stopButton.text = "Stop"
            }
        }

        // Record button
        recordButton.setOnClickListener {
            if (recordButton.text.toString() == "Start Recording") {
                recordButton.text = "Stop Recording"
                val file = File(this.filesDir, format.format(Date()) + "_output.csv")
                writer = FileWriter(file, true)
                writer?.append("Time;Azimuth;Pitch;Roll;After SOMDA;SOMDA after Kalman;GPS;GPS after Kalman;Latitude;Longitude\n")
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
        val rawPitch = somda.pitch.mod(360F).toString()
        val rawRoll = somda.roll.mod(360F).toString()
        somdaAzimuth.text = degree.toString()
        somdaKalman = kalmanFilter.filter(degree)
        somdaKalmanAzimuth?.text = somdaKalman.toString()
        writer?.append("${format.format(Date())};${rawAzimuth.text};${rawPitch};${rawRoll};${somdaAzimuth.text};${somdaKalmanAzimuth?.text};${gps?.text};${gpsKalman?.text};${gpsDirection?.getLatitude()};${gpsDirection?.getLongitude()}\n")
    }

    override fun onGPSDirectionChanged(gpsAzimuth: Float) {
        gps?.text = gpsAzimuth.toString()
    }

    override fun onKalmanChanged(kalmanAzimuth: Float) {
        gpsKalman?.text = kalmanAzimuth.toString()
    }
}
