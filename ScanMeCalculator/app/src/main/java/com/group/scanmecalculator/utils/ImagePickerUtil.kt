package com.group.scanmecalculator.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File

fun Activity.pickFromCamera(uploadType: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val f = File(getExternalFilesDir(Environment.DIRECTORY_DCIM), "image.jpg")
    FileProvider.getUriForFile(this, applicationContext?.packageName + ".provider", f)
    val photoURI =
        FileProvider.getUriForFile(this, applicationContext?.packageName + ".provider", f)

    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    startActivityForResult(intent, uploadType)
}

fun Fragment.pickFromCamera(activity: Activity, uploadType: Int) {
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val f = File(activity.getExternalFilesDir(Environment.DIRECTORY_DCIM), "image.jpg")
    this.context?.let {
        FileProvider.getUriForFile(
            it,
            activity.applicationContext?.packageName + ".provider",
            f
        )
    }
    val photoURI = this.context?.let {
        FileProvider.getUriForFile(
            it,
            activity.applicationContext?.packageName + ".provider",
            f
        )
    }
    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
    startActivityForResult(intent, uploadType)
}

fun Fragment.pickFromGallery(uploadType: Int, isMultipleUpload: Boolean) {
    this.context?.let {
        ActivityCompat.checkSelfPermission(
            it,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    val permission = this.context?.let {
        ActivityCompat.checkSelfPermission(
            it,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    if (permission != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) run {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)
        }
        return
    }
    val intent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, isMultipleUpload)
    intent.type = "image/*"
    startActivityForResult(intent, uploadType)
}

fun Activity.pickFromGallery(uploadType: Int) {
    ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val permission =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    if (permission != PackageManager.PERMISSION_GRANTED) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 123)
        }

        return
    }
    val intent = Intent(
        Intent.ACTION_PICK,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    )
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    intent.type = "image/*"
    startActivityForResult(intent, uploadType)
}
