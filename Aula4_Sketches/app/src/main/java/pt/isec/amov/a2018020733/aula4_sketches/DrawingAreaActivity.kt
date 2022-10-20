package pt.isec.amov.a2018020733.aula4_sketches

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.amov.a2018020733.aula4_sketches.databinding.ActivityDrawingAreaBinding

class DrawingAreaActivity : AppCompatActivity() {

    lateinit var binding : ActivityDrawingAreaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var title = intent.getStringExtra("title") ?: getString(R.string.str_no_name)

        supportActionBar?.title = getString(R.string.sketches_title) + ":" + title

        var r = intent.getIntExtra("red", 255)
        var g = intent.getIntExtra("green", 255)
        var b = intent.getIntExtra("intent", 255)

        binding.frDrawingArea.setBackgroundColor(Color.rgb(r, g, b))
    }
}