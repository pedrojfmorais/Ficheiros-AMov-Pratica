package pt.isec.ans.p03sketches

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.p03sketches.databinding.ActivityConfigColorBinding

class ConfigColorActivity : AppCompatActivity() {
    lateinit var binding: ActivityConfigColorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.seekRed.max = 255
        binding.seekRed.progress = 255
        binding.seekRed.setOnSeekBarChangeListener(processSeekBar)

        binding.seekGreen.run {
            max = 255
            progress = 255
            setOnSeekBarChangeListener(processSeekBar)
        }
        binding.seekBlue.run {
            max = 255
            progress = 255
            setOnSeekBarChangeListener(processSeekBar)
        }
        updateView()
    }

    private val processSeekBar = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            updateView()
        }
        override fun onStartTrackingTouch(p0: SeekBar?) { }
        override fun onStopTrackingTouch(p0: SeekBar?) {    }
    }
    fun updateView() {
        binding.frPreview.setBackgroundColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create,menu)
        return true
    }
    var color : Int = Color.WHITE
        get() = Color.rgb(
            binding.seekRed.progress,
            binding.seekGreen.progress,
            binding.seekBlue.progress
        )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mnCreate) {
            if (binding.edTitle.text.trim().isBlank()) {
                Snackbar.make(binding.edTitle,
                        R.string.msg_empty_title,Snackbar.LENGTH_LONG)
                    .show()
                binding.edTitle.requestFocus()
                return true
            }
            //val intent = Intent(this,DrawingAreaActivity::class.java)
            //intent.putExtra(DrawingAreaActivity.TITLE_KEY,binding.edTitle.text.trim().toString())
            //intent.putExtra("red",binding.seekRed.progress)
            //intent.putExtra("green",binding.seekGreen.progress)
            //intent.putExtra("blue",binding.seekBlue.progress)

            val intent = DrawingAreaActivity.getIntent(
                this,
                binding.edTitle.text.trim().toString(),
                color
            )
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}