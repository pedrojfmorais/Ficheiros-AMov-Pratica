package pt.isec.ans.sketches

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.min

fun setPic(view: View, path: String) {
    getPic(view,path)?.also {
        when {
            // No caso seguinte notar que o "as ImageView" é desnecessário
            view is ImageView -> (view as ImageView).setImageBitmap(it)
            //else -> view.background = bitmap.toDrawable(view.resources)
            else -> view.background = BitmapDrawable(view.resources, it)
        }
    }
}

fun getPic(view: View, path: String) : Bitmap? {
    val targetW = view.width
    val targetH = view.height
    if (targetH < 1 || targetW < 1)
        return null
    val bmpOptions = BitmapFactory.Options()
    bmpOptions.inJustDecodeBounds = true
    BitmapFactory.decodeFile(path, bmpOptions)
    val photoW = bmpOptions.outWidth
    val photoH = bmpOptions.outHeight
    Log.i("ImageUtils", "getPic: $photoH $photoW")
    val scale = max(1, min(photoW / targetW, photoH / targetH))
    bmpOptions.inSampleSize = scale
    bmpOptions.inJustDecodeBounds = false
    return BitmapFactory.decodeFile(path, bmpOptions)
}

fun getTempFilename(context: Context,
                    prefix : String = "image",
                    extension : String = ".img"
) : String =
    File.createTempFile(
        prefix, extension,
        context.externalCacheDir //context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    ).absolutePath

fun createFileFromUri(
    context: Context,
    uri : Uri,
    filename : String = getTempFilename(context)
) : String {
    FileOutputStream(filename).use { outputStream ->
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.copyTo(outputStream)
        }
    }
    return filename
}