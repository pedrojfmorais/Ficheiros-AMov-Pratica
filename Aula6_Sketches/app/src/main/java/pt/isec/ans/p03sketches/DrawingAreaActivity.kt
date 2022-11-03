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
        const val IMAGE_FILE = "imageFile"

        fun getIntent(
            context: Context,
            title : String,
            backgroundColor : Int
        ) : Intent {
            val intent = Intent(context,DrawingAreaActivity::class.java)
            intent.putExtra(TITLE_KEY,title)
            intent.putExtra(COLOR_KEY,backgroundColor)
            return intent
        }

        fun getIntent(
            context: Context,
            title : String,
            imagePath : String?
        ) : Intent {
            val intent = Intent(context,DrawingAreaActivity::class.java)
            intent.putExtra(TITLE_KEY,title)
            intent.putExtra(IMAGE_FILE,imagePath)
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

        val color = intent.getIntExtra(COLOR_KEY,Color.WHITE)
        val imageFile = intent.getStringExtra(IMAGE_FILE)

        //binding.frDrawingArea.setBackgroundColor(color)

        if(imageFile == null)
            drawingArea = DrawingArea(this,color)
        else
            drawingArea = DrawingArea(this, imageFile)

        binding.frDrawingArea.addView(drawingArea)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_drawing,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.mnSave -> {}
            item.groupId == R.id.grpColors -> {
                item.isChecked = true
                //color  = Color.parseColor(item.title.toString())
                drawingArea.lineColor = when(item.itemId) {
                    R.id.mnWhite -> Color.WHITE
                    R.id.mnYellow -> Color.YELLOW
                    R.id.mnBlue -> Color.rgb(0,0,0x80)
                    else -> Color.BLACK
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

}