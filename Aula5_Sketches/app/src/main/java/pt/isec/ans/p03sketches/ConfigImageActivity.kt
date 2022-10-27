package pt.isec.ans.p03sketches

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pt.isec.ans.p03sketches.databinding.ActivityConfigImageBinding

class ConfigImageActivity : AppCompatActivity() {

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val MODE_KEY = "mode"

        fun getGalleryIntent(context: Context) : Intent {
            val intent = Intent(context, ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, GALLERY)
            return intent
        }

        fun getCameraIntent(context: Context) : Intent {
            val intent = Intent(context, ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, CAMERA)
            return intent
        }
    }

    lateinit var binding : ActivityConfigImageBinding
    private var mode = GALLERY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mode = intent.getIntExtra(MODE_KEY, GALLERY)
        when(mode){
            GALLERY -> binding.btnImage.apply {
                text = getString(R.string.btn_choose_image)
                setOnClickListener { chooseImage() }
            }
            CAMERA -> binding.btnImage.apply {
                text = getString(R.string.btn_take_photo)
                setOnClickListener { takePhoto() }
            }
            else -> finish()
        }
    }

    private fun takePhoto() {
        TODO("Not yet implemented")
    }

    private fun chooseImage() {
        TODO("Not yet implemented")
    }
}