package com.abdiel.schoolio.ui.login

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.HomeActivity
import com.abdiel.schoolio.R
import com.abdiel.schoolio.ui.register.RegisterActivity
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.databinding.ActivityLoginBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FirebaseApp.initializeApp(this@LoginActivity)

        generateFcmToken {
            if (it){
                binding.root.snacked("ada login")
            } else {
                binding.root.snacked("tidak ada device token")
            }
        }

        binding.btnRegister.setOnClickListener {
            openActivity<RegisterActivity>()
        }

        binding.btnLogin.setOnClickListener {
            if (binding.etEmailOrPhone.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPassword.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val emailOrPhone = binding.etEmailOrPhone.textOf()
            val password = binding.etPassword.textOf()

            viewModel.login(emailOrPhone, password)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Loading")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<HomeActivity>()
                                finishAffinity()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }

    private fun generateFcmToken(result: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                result(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            //todo: menerima hasil tugas fcmnya
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token) //todo:untuk menegecek aja
            Log.d(ContentValues.TAG, msg)
            tos(token)
            session.setValue(Const.TOKEN.DEVICE_TOKEN, token)
            result(true)
        })
    }
}