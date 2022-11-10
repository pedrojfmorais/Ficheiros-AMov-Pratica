package pt.isec.ans.sketches

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.sketches.databinding.ActivityConfigColorBinding

class ConfigColorActivity : AppCompatActivity() {

    lateinit var binding : ActivityConfigColorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigColorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.seekRed.apply {
            max = 255
            progress = 255
            setOnSeekBarChangeListener(processSeekBar)
        }
        binding.seekGreen.apply {
            max = 255
            progress = 255
            setOnSeekBarChangeListener(processSeekBar)
        }
        binding.seekBlue.apply {
            max = 255
            progress = 255
            setOnSeekBarChangeListener(processSeekBar)
        }
        updatePreview()
    }

    private val processSeekBar = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            updatePreview()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) { }
        override fun onStopTrackingTouch(seekBar: SeekBar?) { }
    }

    private fun updatePreview() {
        val color = Color.rgb(binding.seekRed.progress,binding.seekGreen.progress,binding.seekBlue.progress)
        binding.frPreview.setBackgroundColor(color)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mnCreate) {
            if (binding.edTitle.text.trim().isEmpty()) {
                Snackbar.make(binding.edTitle,
                    R.string.msg_empty_title,
                    Snackbar.LENGTH_LONG).show()
                binding.edTitle.requestFocus()
                return true;
            }
            val intent = Intent(
                DrawingAreaActivity.getIntent(
                    this,
                    binding.edTitle.text.trim().toString(),
                    Color.rgb(
                        binding.seekRed.progress,
                        binding.seekGreen.progress,
                        binding.seekBlue.progress
                    )
                )
            )
            /* val intent = Intent(this,DrawingAreaActivity::class.java)
            intent.putExtra("title",binding.edTitle.text.toString())
            intent.putExtra("red",binding.seekRed.progress)
            intent.putExtra("green",binding.seekGreen.progress)
            intent.putExtra("blue",binding.seekBlue.progress)*/
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}