package com.abdiel.schoolio

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.data.mapel.Mapel
import com.abdiel.schoolio.databinding.ActivityHomeBinding
import com.abdiel.schoolio.databinding.ListMapelBinding
import com.abdiel.schoolio.ui.HomeViewModel
import com.abdiel.schoolio.ui.profile.ProfileActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.ImagePreviewHelper
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding, HomeViewModel>(R.layout.activity_home) {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    private var listSubjects = ArrayList<Mapel?>()
    private var subjectList = ArrayList<Mapel?>()

    private val adapter by lazy {
        //Adapter
        CoreListAdapter<ListMapelBinding, Mapel>(R.layout.list_mapel)
            .initItem(listSubjects) { _, data ->
                activityLauncher.launch(createIntent<AssignmentActivity> {
                    putExtra(Const.LIST.LIST_SUBJECT, data)
                })
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.rvHome.adapter = adapter

        viewModel.listSubject()
        observe()
        initSwipe()

        drawerLayout = binding.drawerLayout
        navView = binding.navMenu

        binding.btnMenu.setOnClickListener {
            sideMenu()
        }
    }

    @Suppress("DEPRECATION")
    private fun sideMenu() {

        val inflater = LayoutInflater.from(this)
        val headerView = inflater.inflate(R.layout.header, navView, false)
        val ivProfile = headerView.findViewById<ImageView>(R.id.iv_profile)

        if (headerView == null) {
            // inflate a new header view and add it to the navigation view
            val headersView = inflater.inflate(R.layout.header, navView, false)
            navView.addHeaderView(headersView)

        }else{
            navView.removeHeaderView(navView.getHeaderView(0))
            navView.addHeaderView(headerView)
        }

        val user = session.getUser()

        Glide.with(this)
            .load(user?.photo)
            .apply(RequestOptions.circleCropTransform())
            .into(ivProfile)

        ivProfile.setOnClickListener {
            activityLauncher.launch(createIntent<ProfileActivity>()) {
                if (it.resultCode == 29) {
                    getUser().apply {
                        sideMenu()
                    }
                    observe()
                }
            }
        }

        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                //Do nothing
            }

            override fun onDrawerOpened(drawerView: View) {
                // Set the status bar color to the drawer open color when the drawer is opened
                window.statusBarColor =
                    ContextCompat.getColor(this@HomeActivity, R.color.white)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            override fun onDrawerClosed(drawerView: View) {
                window.statusBarColor =
                    ContextCompat.getColor(this@HomeActivity, R.color.primer_third)
                window.decorView.systemUiVisibility = 0

            }

            override fun onDrawerStateChanged(newState: Int) {
                // Do nothing
            }
        })

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    drawerLayout.closeDrawer(GravityCompat.START)
                    viewModel.logout()
                    tos(":( you logged out")
                    true
                }

                R.id.assignment -> {
                    // Handle Settings item selection
                    drawerLayout.closeDrawer(GravityCompat.START)
                    // Implement your logic for Settings item here
                    openActivity<AssignmentActivity>()
                    true
                }
                else -> false
            }
        }

        binding.btnMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    // Handle ActionBarDrawerToggle click event
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
            return true

        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUser() {
        val users = session.getUser()
        binding.user = users
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.listSubject.collect { subject ->
                        listSubjects.clear()
                        subjectList.clear()
                        adapter.notifyDataSetChanged()
                        listSubjects.addAll(subject)
                        subjectList.addAll(subject)
                        adapter.notifyItemInserted(0)
                        binding.tvEmpty.isVisible = subject.isEmpty()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }

                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                sideMenu()
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

    private fun initSwipe() {
        binding.swipeRefresh.setProgressViewOffset(false, 0, 280)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.listSubject()
        }
    }
}