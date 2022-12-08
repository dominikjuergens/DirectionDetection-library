package dominikjuergens.example.directiondetectionexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dominikjuergens.example.directiondetectionlibrary.Somda

abstract class MainActivity : AppCompatActivity(), Somda.SomdaListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = Somda(this)
        s.start(this)
    }

    override fun onSomdaChanged(degree: Float) {
        TODO("Not yet implemented")
    }
}