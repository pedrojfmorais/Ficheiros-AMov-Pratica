package pt.isec.ans.p03sketches

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.p03sketches.databinding.ActivityConfigImageBinding
import java.io.File

class ConfigImageActivity : AppCompatActivity() {
    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 1234
        private const val GALLERY = 1
        private const val CAMERA  = 2
        private const val MODE_KEY = "mode"

        fun getGalleryIntent(context: Context) : Intent {
            val intent = Intent(context,ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, GALLERY)
            return intent
        }
        fun getCameraIntent(context: Context) : Intent {
            val intent = Intent(context,ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, CAMERA)
            return intent
        }
    }

    private var mode = GALLERY

    lateinit var binding: ActivityConfigImageBinding

    private var permissionsGranted = false
        set(value){
            field = value
            binding.btnImage.isEnabled = value
        }

    private var imagePath : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mode = intent.getIntExtra(MODE_KEY, GALLERY)
        when (mode) {
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

        verify_permissions()
        updatePreview()

    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ grantResults ->
            permissionsGranted = grantResults.values.all { it }
    }

    private fun verify_permissions(){

        requestPermissionsLauncher.launch(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
        /*
        if ( ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsGranted = false;
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_CODE
            )
        } else
            permissionsGranted = true
        */
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if(requestCode == PERMISSIONS_REQUEST_CODE){

            permissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            return
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun chooseImage() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.setType("image/*")
////        startActivityForResult(intent, 1111)
//        startActivityForResult.launch(intent)
        startActivityForResult.launch("image/*")
    }

    val startActivityForResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        imagePath = uri?.let { createFileFromUri(this, uri) }
        updatePreview()
    }


/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, info: Intent?) {

        if (requestCode == 1111 && resultCode == RESULT_OK && info != null){
            info.data?.let { uri ->
                imagePath = createFileFromUri(this, uri)
                updatePreview()
            }
        }

        super.onActivityResult(requestCode, resultCode, info)
    }
*/
    fun updatePreview(){
        if(imagePath != null)
            setPic(binding.frPreview, imagePath!!)
        else
            binding.frPreview.background = ResourcesCompat.getDrawable(resources,
                android.R.drawable.ic_menu_report_image, null
            )
    }

    var startActivityForTakePhotoResult = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success)
            imagePath = null
        updatePreview()
    }

    private fun takePhoto() {
        imagePath = getTempFilename(this)
        startActivityForTakePhotoResult.launch(
            FileProvider.getUriForFile(this,
                "pt.isec.amov.sketches.android.fileprovider",
                File(imagePath)
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.mnCreate && imagePath != null) {
            if (binding.edTitle.text.trim().isBlank()) {
                Snackbar.make(binding.edTitle,
                    R.string.msg_empty_title, Snackbar.LENGTH_LONG)
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
                imagePath
            )
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}