package pt.isec.ans.p03sketches

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.p03sketches.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnColor.setOnClickListener {
            //Toast.makeText(this,getString(R.string.msg_soon),Toast.LENGTH_LONG).show()
            intent = Intent(this,ConfigColorActivity::class.java)
            startActivity(intent)
        }
        binding.btnGallery.setOnClickListener {
            startActivity(ConfigImageActivity.getGalleryIntent(this))
        }
        binding.btnPhoto.setOnClickListener {
            startActivity(ConfigImageActivity.getCameraIntent(this))
        }
        binding.btnHistory.setOnClickListener {
            Snackbar.make(it,R.string.msg_soon,Snackbar.LENGTH_LONG).show()
        }
    }
}