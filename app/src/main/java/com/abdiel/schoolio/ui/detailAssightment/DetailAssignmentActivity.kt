package com.abdiel.schoolio.ui.detailAssightment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.databinding.ActivityDetailAssignmentBinding
import com.crocodic.core.api.ApiStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailAssignmentActivity :
    BaseActivity<ActivityDetailAssignmentBinding, DetailAssignmentViewModel>(R.layout.activity_detail_assignment) {

    private val FILE_REQUEST_CODE = 123

    private var assignmentUrl: String? = null
    private var assignmentFile : File? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getAssignment()
        observe()



        binding.btnAddAssignment.setOnClickListener {
            showAddAssignmentDialog()
            addNewItem()
        }

        binding.btnHandItOver.setOnClickListener {
            Log.d("cek teks tugas","$assignmentUrl")
            Log.d("cek file tugas","$assignmentFile")
        }

    }

    private fun addNewItem() {
        // Inflate the item layout
        val inflater = LayoutInflater.from(this)
        val newItemLayout = inflater.inflate(R.layout.item_assignment_attachment, binding.containerLayout, false)

        // Customize the new item layout if needed
        val itemEditText = newItemLayout.findViewById<TextView>(R.id.tv_nameFileOrUrl)
        //Di Olah lagi
        itemEditText.setText(assignmentUrl+assignmentFile)

        // Add the new item layout to the container
        binding.containerLayout.addView(newItemLayout)
    }

    private fun showAddAssignmentDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Assignment")

        val options = arrayOf("Text", "File")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> showTextDialog()
                1 -> openFileManager()
            }
        }

        builder.create().show()
    }

    private fun showTextDialog() {
        val editText = EditText(this)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(editText)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Text Assignment")
        builder.setView(layout)

        builder.setPositiveButton("Add") { _, _ ->
            val text = editText.text.toString()
            // Handle the text assignment here
//            binding.tvNameFileOrUrl.visibility = View.VISIBLE
//            binding.tvNameFileOrUrl.text = text
            assignmentUrl = text
            Log.d("cek teks tugas","$assignmentUrl")
            Toast.makeText(this, "Text Assignment added: $text", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        builder.create().show()
    }


    private fun openFileManager() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  // You can set specific MIME types for files
        startActivityForResult(intent, FILE_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { fileUri ->
                // Handle the selected file here
                val selectedFile = File(fileUri.path)
//                binding.tvNameFileOrUrl.visibility = View.VISIBLE
//                binding.tvNameFileOrUrl.text = selectedFile.name
                assignmentFile = selectedFile
                Log.d("cek file tugas","$assignmentFile")
                Toast.makeText(this, "File selected: ${selectedFile.name}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    //ME Function Call API AssignmentById
    private fun getAssignment() {
        val assignmentID = intent.getStringExtra(Const.LIST.ASSIGNMENT_ID)
        if (assignmentID != null) {
            viewModel.byIdAssignment(assignmentID)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.byIdSubject.collect { subject ->
                        binding.data = subject
                    }
                }

                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                            }

                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }


}