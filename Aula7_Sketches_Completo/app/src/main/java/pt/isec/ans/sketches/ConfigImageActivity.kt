package pt.isec.ans.sketches

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.snackbar.Snackbar
import pt.isec.ans.sketches.databinding.ActivityConfigImageBinding
import java.io.File

class ConfigImageActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ConfigImageActivity"
        private const val ACTIVITY_REQUEST_CODE_GALLERY = 10
        private const val ACTIVITY_REQUEST_CODE_CAMERA  = 20
        private const val PERMISSIONS_REQUEST_CODE = 1

        private const val GALLERY = 1
        private const val CAMERA  = 2
        private const val MODE_KEY = "mode"

        fun getGalleryIntent(context : Context) : Intent {
            val intent = Intent(context,ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, GALLERY)
            return intent
        }
        fun getCameraIntent(context : Context) : Intent {
            val intent = Intent(context,ConfigImageActivity::class.java)
            intent.putExtra(MODE_KEY, CAMERA)
            return intent
        }
    }

    private lateinit var binding : ActivityConfigImageBinding

    private var mode = GALLERY
    private var imagePath : String? = null
    private var permissionsGranted = false
        set(value) {
            field = value
            binding.btnImage.isEnabled = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getIntExtra(MODE_KEY, GALLERY)
        when (mode) {
            GALLERY ->
                binding.btnImage.apply {
                    text = getString(R.string.btn_choose_image)
                    setOnClickListener { chooseImage_v3() }
                }
            CAMERA ->
                binding.btnImage.apply {
                    text = getString(R.string.btn_take_photo)
                    setOnClickListener { takePhoto_v2() }
                }
            else -> finish() //podia-se também colocar um toast
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        verifyPermissions_v3()
        updatePreview()
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
            if (imagePath == null) {
                Snackbar.make(binding.edTitle,
                    R.string.msg_no_image,
                    Snackbar.LENGTH_LONG).show()
                return true;
            }
            val intent = Intent(
                DrawingAreaActivity.getIntent(
                    this,
                    binding.edTitle.text.trim().toString(),
                    imagePath
                )
            )
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun chooseImage_v1() {
        Log.i(TAG, "chooseImage_v1: ")
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE_GALLERY)
    }
    // para a v1 da chooseImage e depois para a v1 do takePhoto
    override fun onActivityResult(requestCode: Int, resultCode: Int, info: Intent?) {
        Log.i(TAG, "onActivityResult: ")
        if (requestCode == ACTIVITY_REQUEST_CODE_GALLERY && resultCode == RESULT_OK && info != null) {
            /*info.data?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
            }*/
            info.data?.let { uri ->
                imagePath = createFileFromUri(this,uri)
                updatePreview()
            }
            return
        }
        if (requestCode == ACTIVITY_REQUEST_CODE_CAMERA) {
            if (resultCode != RESULT_OK)
                imagePath = null
            updatePreview()
            return
        }
        super.onActivityResult(requestCode, resultCode, info)

    }

/*    var startActivityForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult() ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultIntent = result.data
            val uri = resultIntent?.data?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
            }

        }
    }*/

    //para a v2
    var startActivityForGalleryResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
            Log.i(TAG, "startActivityForGalleryResult: ")
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.data?.let { uri ->
                    imagePath = createFileFromUri(this,uri)
                    updatePreview()
                }
            }
    }

    fun chooseImage_v2() {
        Log.i(TAG, "chooseImage_v2: ")
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForGalleryResult.launch(intent)
    }

    // para a v3
    var startActivityForContentResult = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        Log.i(TAG, "startActivityForContentResult: ")
        /*uri?.apply {
                val cursor = contentResolver.query(this,
                    arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                if (cursor !=null && cursor.moveToFirst())
                    imagePath = cursor.getString(0)
                updatePreview()
        }*/
        imagePath = uri?.let { createFileFromUri(this, it) }
        updatePreview()
    }

    fun chooseImage_v3() {
        Log.i(TAG, "chooseImage_v3: ")
        startActivityForContentResult.launch("image/*")

    }

    // para a v4 => androidx.activity:activity-ktx:1.6.1
    var pickPictureActionResult = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        Log.i(TAG, "pickPictureActionResult: ")
        imagePath = uri?.let { createFileFromUri(this, it) }
        updatePreview()
    }

    fun chooseImage_v4() {
        Log.i(TAG, "chooseImage_v4: ")
        pickPictureActionResult.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }

    fun takePhoto_v1() {
        imagePath = getTempFilename(this)
        Log.i(TAG, "takePhoto: $imagePath")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            val fileUri = FileProvider.getUriForFile( this,
                "pt.isec.ans.sketches.android.fileprovider", File(imagePath))
            it.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        }
        startActivityForResult(intent, 20)
    }

    var startActivityForTakePhotoResult = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        Log.i(TAG, "startActivityForTakePhotoResult: $success")
        if (!success)
            imagePath = null
        updatePreview()
    }
    // a versão v2 poderia ser a implementação simples do contrato StartActivityForResult
    // mas não vale a pena fazer todos os exemplos
    fun takePhoto_v2() {
        imagePath = getTempFilename(this)
        Log.i(TAG, "takePhoto: $imagePath")
        startActivityForTakePhotoResult.launch(FileProvider.getUriForFile( this,
            "pt.isec.ans.sketches.android.fileprovider", File(imagePath)))
    }

    fun updatePreview() {
        if (imagePath != null)
            setPic(binding.frPreview, imagePath!!)
        else
            binding.frPreview.background = ResourcesCompat.getDrawable(resources,
                R.drawable.joydivision, //android.R.drawable.ic_menu_report_image,
                null)
    }

    fun verifyPermissions_v1() {
        Log.i(TAG, "verifyPermissions_v1: ")
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(
                        //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    PERMISSIONS_REQUEST_CODE
                )
                return
            }
        }*/

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = false
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSIONS_REQUEST_CODE
            )
        } else
            permissionsGranted = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            permissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun verifyPermissions_v2() {
        Log.i(TAG, "verifyPermissions_v2: ")
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = false
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        } else
            permissionsGranted = true
    }

    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionsGranted = isGranted
    }

    //versão alterada para suportar API mais recentes
    fun verifyPermissions_v3() {
        Log.i(TAG, "verifyPermissions_v3: ")
        if (mode == CAMERA) {
            permissionsGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            if (!permissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            return
        }
        //mode == GALLERY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsGranted = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionsGranted)
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            return
        }
        // GALLERY, versões < API33
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED /*||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED */
        ) {
            permissionsGranted = false
            requestPermissionsLauncher.launch(
                arrayOf(
                    //android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        } else
            permissionsGranted = true
    }

    val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
        permissionsGranted = grantResults.values.any { it }
    }

}