package pt.isec.ans.p03sketches

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import pt.isec.ans.p03sketches.databinding.ActivityDrawingAreaBinding

class DrawingAreaActivity : AppCompatActivity() {

    companion object {
        const val TITLE_KEY = "title"
        const val COLOR_KEY = "color"

        fun getIntent(
            context : Context,
            title : String,
            backgroundColor: Int
        ) : Intent {
            val intent = Intent(context, DrawingAreaActivity::class.java)
            intent.putExtra(TITLE_KEY, title)
            intent.putExtra(COLOR_KEY, backgroundColor)
            return intent
        }
    }

    lateinit var drawingArea: DrawingArea
    lateinit var binding: ActivityDrawingAreaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val title = intent.getStringExtra(TITLE_KEY) ?: getString(R.string.str_no_name)
        supportActionBar?.title = getString(R.string.sketches_title) + ": " + title

        val color = intent.getIntExtra(COLOR_KEY, Color.WHITE)

//        val r = intent.getIntExtra("red",255)
//        val g = intent.getIntExtra("green",255)
//        val b = intent.getIntExtra("blue",255)

//        binding.frDrawingArea.setBackgroundColor(color)

        drawingArea = DrawingArea(this, color)
        binding.frDrawingArea.addView(drawingArea)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_drawing, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.mnSave -> {}
            item.groupId == R.id.grpColors -> {
                item.isChecked = true
//                var color = Color.parseColor(item.title.toString())
                drawingArea.lineColor = when(item.itemId){
                    R.id.mnWhite -> Color.WHITE
                    R.id.mnYellow -> Color.YELLOW
                    R.id.mnBlue -> Color.rgb(0, 0, 0x80)
                    else -> Color.BLACK
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}