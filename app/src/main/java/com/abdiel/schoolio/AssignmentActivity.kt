package com.abdiel.schoolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.data.mapel.Assignment
import com.abdiel.schoolio.data.mapel.Mapel
import com.abdiel.schoolio.databinding.ActivityAssignmentBinding
import com.abdiel.schoolio.databinding.ListAssignmentBinding
import com.abdiel.schoolio.ui.assignment.AssignmentViewModel
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.createIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignmentActivity : BaseActivity<ActivityAssignmentBinding, AssignmentViewModel>(R.layout.activity_assignment) {

    private var assignment: Mapel? = null
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

        assignment = intent.getParcelableExtra(Const.LIST.LIST_SUBJECT)
        if (assignment != null) {
            Log.d("cek id", assignment?.id ?: "")
            viewModel.byId(assignment?.id ?: return)
        }

        observe()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.byIdSubject.collect { subject ->
                        binding.data = subject
                        listAssignment.clear()
                        listAllAssignment.clear()
                        adapter.notifyDataSetChanged()
                        listAssignment.addAll(subject.assignment ?: emptyList())
                        listAllAssignment.addAll(subject.assignment ?: emptyList())
                        adapter.notifyItemInserted(0)
                        binding.tvEmpty.isVisible = subject.assignment?.isEmpty() == true
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
        }
    }

//    private fun initSwipe() {
//        binding.swipeRefresh.setProgressViewOffset(false, 0, 280)
//        binding.swipeRefresh.setOnRefreshListener {
//            viewModel.byId()
//        }
//    }
}