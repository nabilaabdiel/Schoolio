package com.abdiel.schoolio.ui.main

import android.os.Bundle
import com.abdiel.schoolio.HomeActivity
import com.abdiel.schoolio.R
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.databinding.ActivityMainBinding
import com.abdiel.schoolio.ui.login.LoginActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userLogin = session.getString(Const.USER.PROFILE)

        viewModel.splash {
            if (userLogin == "Login") {
                openActivity<HomeActivity>()
            } else {
                openActivity<LoginActivity>()
            }
            finish()
        }
    }
}