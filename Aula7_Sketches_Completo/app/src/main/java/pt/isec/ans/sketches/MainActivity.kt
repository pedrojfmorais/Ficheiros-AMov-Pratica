package pt.isec.ans.sketches

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.sketches.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSolidColor.setOnClickListener {
            val intent = Intent(this, ConfigColorActivity::class.java)
            startActivity(intent)
        }
        binding.btnGallery.setOnClickListener {
            startActivity(ConfigImageActivity.getGalleryIntent(this))
        }
        binding.btnPhoto.setOnClickListener {
            startActivity(ConfigImageActivity.getCameraIntent(this))
        }
        binding.btnHistory.setOnClickListener {
            showHistory()
        }
    }

    private fun showHistory() {
        val map: Map<String, File>? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?.listFiles { file -> file.extension == DrawingAreaActivity.IMAGE_EXTENSION }
            ?.associateBy { it.nameWithoutExtension }
        if (map == null || map.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.msg_no_sketches), Snackbar.LENGTH_LONG)
                .show()
            return
        }
        //It is possible to use a DialogFragment
        val dlgHistory = AlertDialog.Builder(this)
            .setTitle(R.string.msg_choose_history)
            .setItems(map.keys.toTypedArray()) { _, which ->
                val title = map.keys.toTypedArray()[which]
                val filename = map[title]?.absolutePath
                val intent = DrawingAreaActivity.getIntent(
                    this,
                    title,
                    filename
                )
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .create()

        dlgHistory.show()
    }
}