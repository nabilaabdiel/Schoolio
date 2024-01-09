package com.abdiel.schoolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.data.mapel.Assignment
import com.abdiel.schoolio.databinding.ActivityAssignmentBinding
import com.abdiel.schoolio.databinding.ListAssignmentBinding
import com.abdiel.schoolio.ui.assignment.AssignmentViewModel
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.createIntent
import kotlinx.coroutines.launch

class AssignmentActivity : BaseActivity<ActivityAssignmentBinding, AssignmentViewModel>(R.layout.activity_assignment) {

    private var listAssignment = ArrayList<Assignment?>()
    private var listAllAssignment = ArrayList<Assignment?>()

    private val adapter by lazy {
        //Adapter
        CoreListAdapter<ListAssignmentBinding, Assignment>(R.layout.list_assignment)
            .initItem(listAssignment) { _, data ->
                activityLauncher.launch(createIntent<DetailAssignmentActivity> {
                    putExtra(Const.LIST.LIST_SUBJECT, data)
                })
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvHome.adapter = adapter

        val data = intent.getStringExtra(Const.LIST.LIST_ID)
        if (data != null) {
            Log.d("cek id",data)
            viewModel.byId(data)
        }
        observe()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.byIdSubject.collect { subject ->
                        listAssignment.clear()
                        listAllAssignment.clear()
                        adapter.notifyDataSetChanged()
                        listAssignment.addAll(subject)
                        listAllAssignment.addAll(subject)
                        adapter.notifyItemInserted(0)
                    }
                }
            }
        }
    }
}