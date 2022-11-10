package pt.isec.ans.sketches

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.core.view.drawToBitmap
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.sketches.databinding.ActivityDrawingAreaBinding
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class DrawingAreaActivity : AppCompatActivity() {
    companion object {
        private const val TITLE_KEY = "title"
        private const val COLOR_KEY = "color"
        private const val IMAGE_KEY = "imagefile"

        const val IMAGE_EXTENSION   = "skt"

        fun getIntent(
            context: Context,
            title: String,
            backgroundColor: Int
        ): Intent {
            val intent = Intent(context, DrawingAreaActivity::class.java)
            intent.putExtra(TITLE_KEY, title)
            intent.putExtra(COLOR_KEY, backgroundColor)
            return intent
        }

        fun getIntent(context: Context, title: String, imagePath: String?): Intent {
            val intent = Intent(context, DrawingAreaActivity::class.java)
            intent.putExtra(TITLE_KEY, title)
            intent.putExtra(IMAGE_KEY, imagePath)
            return intent
        }
    }

    lateinit var title : String
    lateinit var binding : ActivityDrawingAreaBinding
    lateinit var drawingArea: DrawingArea

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrawingAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = intent.getStringExtra(TITLE_KEY) ?: getString(R.string.str_no_name)
        supportActionBar?.title = getString(R.string.sketches_title) + ": " + title

        val color = intent.getIntExtra(COLOR_KEY,Color.WHITE)
        val imageFile = intent.getStringExtra(IMAGE_KEY)

        /*drawingArea = DrawingArea(this)
        binding.frDrawingArea.setBackgroundColor(color)
        binding.frDrawingArea.addView(drawingArea)*/
        if (imageFile == null)
            drawingArea = DrawingArea(this,color)
        else
            drawingArea = DrawingArea(this,imageFile)

        binding.frDrawingArea.addView(drawingArea)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_drawing,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when {
            item.itemId == R.id.mnSave -> saveSketch()
            item.groupId == R.id.grpColors -> {
                item.isChecked = true
                drawingArea.lineColor = when (item.itemId) {
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

    fun saveSketch() {
        val filename = String.format("%s/%s.%s",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            title,
            IMAGE_EXTENSION)
        FileOutputStream(filename).use { fos ->
            drawingArea.drawToBitmap()
                .compress(Bitmap.CompressFormat.PNG,100,fos)
        }
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        externalCacheDir?.deleteRecursively()
    }
}