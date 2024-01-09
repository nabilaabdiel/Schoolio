package com.abdiel.schoolio.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.R
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.databinding.ActivityProfileBinding
import com.abdiel.schoolio.ui.login.LoginActivity
import com.abdiel.schoolio.ui.update.password.UpdatePasswordActivity
import com.abdiel.schoolio.ui.update.profile.UpdateProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.createIntent
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.getProfile() {
            getUser()
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.btnChangeProfile.setOnClickListener {
            activityLauncher.launch(createIntent<UpdateProfileActivity>()) {
                getUser()
                if (it.resultCode == 7) {
                    viewModel.getProfile() {
                        getUser()
                    }
                    observe()
                }
            }
        }

        binding.btnChangePassword.setOnClickListener {
            openActivity<UpdatePasswordActivity>()
        }

//        binding.logout.setOnClickListener {
//            viewModel.logout()
//        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        tos("tooo")
        finish()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect { logout ->
                        if (logout.status == ApiStatus.SUCCESS) {
                            openActivity<LoginActivity>()
                            finishAffinity()
                        }
                    }
                }
            }
        }
    }

    private fun getUser() {
        val users = session.getUser()
        binding.user = users
        setResult(29)
    }

    private fun alertDialog() {
        val alertDialog = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog_logout, findViewById(R.id.alert_dialog_logout))

        val alertDialogBuilder = AlertDialog.Builder(this)
            .setView(alertDialog)

        val dialog = alertDialogBuilder.show()
        dialog.window?.setBackgroundDrawableResource(R.drawable.alert_background)
        dialog.window?.setLayout(840, 420)

        val btnContinue = alertDialog.findViewById<AppCompatButton>(R.id.sure)
        val btnDiscard = alertDialog.findViewById<AppCompatButton>(R.id.maybe_no)

        btnContinue.setOnClickListener {
            viewModel.logout()
            dialog.dismiss()
        }

        btnDiscard.setOnClickListener {
            dialog.dismiss()
            finish()
        }
    }
}