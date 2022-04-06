package com.group.scanmecalculator.presentation

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.group.scanmecalculator.databinding.FragmentMainBinding
import com.group.scanmecalculator.utils.pickFromCamera
import com.group.scanmecalculator.utils.pickFromGallery
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val SELECT_FROM_GALLERY = 111
    private val TAKE_PHOTO = 222
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSecond.setOnClickListener {
            when (com.group.scanmecalculator.BuildConfig.FLAVOR) {
                ProductFlavor.greenCamera.name, ProductFlavor.redCamera.name -> {
                    activity?.let { it1 -> pickFromCamera(it1, TAKE_PHOTO) }
                }
                else -> {
                    pickFromGallery(SELECT_FROM_GALLERY, false)
                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_FROM_GALLERY -> {
                    val selectedImageUri = when (data?.clipData == null) {
                        true -> data?.data
                        false -> data?.clipData?.getItemAt(0)?.uri
                    }
                    val image = context?.let { con ->
                        selectedImageUri?.let { uri ->
                            InputImage.fromFilePath(con, uri)
                        }
                    }
                    image?.let {
                        recognizer.process(it)
                            .addOnSuccessListener { visionText ->
                                displayTexts(visionText.text)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Error", e.localizedMessage)
                            }
                    }

                }
                TAKE_PHOTO -> {
                    var f: File? = context?.getExternalFilesDir(Environment.DIRECTORY_DCIM)
                    for (temp: File? in f?.listFiles()!!) {
                        if (temp?.name == "image.jpg") {
                            f = temp
                            break
                        }
                    }
                    val bmp = BitmapFactory.decodeFile(f?.absolutePath)
                    val fOut: OutputStream = FileOutputStream(f)
                    bmp.compress(Bitmap.CompressFormat.JPEG, 60, fOut)
                    fOut.flush()
                    fOut.close()
                    val imageUri = Uri.fromFile(File(f.path))
                    val image = context?.let {
                        imageUri?.let { it1 ->
                            InputImage.fromFilePath(
                                it,
                                it1
                            )
                        }
                    }
                    image?.let {
                        recognizer.process(it)
                            .addOnSuccessListener { visionText ->
                                displayTexts(visionText.text)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Error", e.localizedMessage)
                            }
                    }
                }
            }
        }
    }

    private fun displayTexts(text: String) {
        binding.textScanned.text = text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

enum class ProductFlavor { greenCamera, greenFileSystem, redCamera, redFileSystem }