package com.abdiel.schoolio.ui.update.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.R
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.databinding.ActivityUpdateProfileBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.DateTimeHelper
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

@AndroidEntryPoint
class UpdateProfileActivity :
    BaseActivity<ActivityUpdateProfileBinding, UpdateProfileViewModel>(R.layout.activity_update_profile) {

    private var photoFile: File? = null
    private var userName: String? = null
//    private var profilePicture : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getUser()


        binding.photoProfile.setOnClickListener {
            if (checkPermissionGallery()) {
                openGallery()
            } else {
                requestPermissionGallery()
            }
        }

        binding.btnSave.setOnClickListener {
            validateForm()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("On Progress")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                setResult(7)
                                finish()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        if (photoFile != null) {
            unsavedAlert()
            return
        }

        if (binding.tvName.textOf().isNotEmpty() && binding.tvName.textOf() != userName) {
            unsavedAlert()
            return
        }
        finish()
    }

    private fun validateForm() {
        val name = binding.tvName.textOf()
        val profilePicture = session.getUser()?.photo

        if (photoFile == null) {
            if (name.isEmpty()) {
                binding.root.snacked("Name is empty")
            } else {
                Log.d("foto", "$profilePicture")
                viewModel.updateProfile(name)
            }
        } else {
            if (name.isEmpty()) {
                binding.root.snacked("Name is empty")
            } else {
                Log.d("foto", "photo not null")
                viewModel.updateProfileImg(name, photoFile!!)
            }
        }
    }

    //data user
    private fun getUser() {
        val users = session.getUser()
        binding.user = users
        userName = users?.name
//        profilePicture = users?.profile_photo_path
//        Log.d("cek picture",profilePicture.toString())

    }

    private var activityLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            result.data?.data?.let {
                generateFileImage(it)
            }
        }

    private fun checkPermissionGallery(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activityLauncherGallery.launch(galleryIntent)
    }

    private fun requestPermissionGallery() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
            110
        )
    }

    private fun generateFileImage(uri: Uri) {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()

            val orientation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                getOrientation2(uri)
            } else {
                getOrientation(uri)
            }

            val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                createImageFile()
            } else {
                File(externalCacheDir?.absolutePath, getNewFileName())
            }

            val fos = FileOutputStream(file)
            var bitmap = image

            if (orientation != -1 && orientation != 0) {

                val matrix = Matrix()
                when (orientation) {
                    6 -> matrix.postRotate(90f)
                    3 -> matrix.postRotate(180f)
                    8 -> matrix.postRotate(270f)
                    else -> matrix.postRotate(orientation.toFloat())
                }
                bitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            }

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            binding.cvProfile.setImageBitmap(bitmap)
            val requestOption = RequestOptions()
                .circleCrop()
            Glide
                .with(this)
                .load(bitmap)
                .transition(DrawableTransitionOptions.withCrossFade())
                .apply(requestOption)
                .into(binding.photoProfile)
            lifecycleScope.launch {
                loadingDialog.show("Compressing")
                photoFile = compressFile(file)
                loadingDialog.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.snacked("File ini tidak dapat digunakan")
        }
    }

    @SuppressLint("Range")
    private fun getOrientation(shareUri: Uri): Int {
        val orientationColumn = arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(
            shareUri,
            orientationColumn,
            null,
            null,
            null
        )

        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            if (cur.columnCount > 0) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
            }
            cur.close()
        }
        return orientation
    }

    @SuppressLint("NewApi")
    private fun getOrientation2(shareUri: Uri): Int {
        val inputStream = contentResolver.openInputStream(shareUri)
        return getOrientation3(inputStream)
    }

    @SuppressLint("NewApi")
    private fun getOrientation3(inputStream: InputStream?): Int {
        val exif: ExifInterface
        var orientation = -1
        inputStream?.let {
            try {
                exif = ExifInterface(it)
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return orientation
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = DateTimeHelper().createAtLong().toString()
        val storageDir =
            getAppSpecificAlbumStorageDir(Environment.DIRECTORY_DOCUMENTS, "Attachment")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun getNewFileName(isPdf: Boolean = false): String {
        val timeStamp = DateTimeHelper().createAtLong().toString()
        return if (isPdf) "PDF_${timeStamp}_.pdf" else "JPEG_${timeStamp}_.jpg"
    }

    private fun getAppSpecificAlbumStorageDir(albumName: String, subAlbumName: String): File {
        val file = File(getExternalFilesDir(albumName), subAlbumName)
        if (!file.mkdirs()) {
        }
        return file
    }

    private fun unsavedAlert() {
        val alertDialog = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog_discard, findViewById(R.id.alert_dialog))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(alertDialog)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.alert_background)
        dialog.window?.setLayout(800, 420)

        val btnContinue = alertDialog.findViewById<AppCompatButton>(R.id.btn_cancel)
        val btnDiscard = alertDialog.findViewById<AppCompatButton>(R.id.btn_confirm)

        btnContinue.setOnClickListener {
            dialog.dismiss()
        }

        btnDiscard.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }

    suspend fun compressFile(filePhoto: File): File? {
        try {
            return Compressor.compress(this, filePhoto) {
                resolution(720, 720)
                quality(80)
                format(Bitmap.CompressFormat.PNG)
                size(515)
            }
        } catch (e: Exception) {
            tos("Compress failed, please choose another photo")
            return null
        }
    }

}