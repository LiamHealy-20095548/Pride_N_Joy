package ie.wit.pridenjoy.helpers

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri

import ie.wit.pridenjoy.R
import ie.wit.pridenjoy.fragments.EditFragment
import java.io.IOException

fun showImagePicker(parent: Activity, id: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    val chooser = Intent.createChooser(intent, R.string.select_event_image.toString())
    parent.startActivityForResult(chooser, id)
}

fun readImage(activity: Activity, resultCode: Int, data: Intent?): Bitmap? {
    var bitmap: Bitmap? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            //bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
            bitmap = ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(activity.contentResolver, data.data!!
                    ))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun readImageFromPath(context: Context, path: String): Bitmap? {
    var bitmap: Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    return bitmap
}