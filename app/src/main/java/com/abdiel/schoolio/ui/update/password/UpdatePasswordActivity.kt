package com.abdiel.schoolio.ui.update.password

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.R
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.databinding.ActivityUpdatePasswordBinding
import com.abdiel.schoolio.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdatePasswordActivity :
    BaseActivity<ActivityUpdatePasswordBinding, UpdatePasswordViewModel>(R.layout.activity_update_password) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSave.setOnClickListener {
            if (binding.newPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.passwordConfirmation.isEmptyRequired(R.string.label_must_fill)
            ) {
                return@setOnClickListener
            }

            val newPassword = binding.newPassword.textOf()
            val confirmPassword = binding.passwordConfirmation.textOf()

            viewModel.changePassword(newPassword, confirmPassword)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Loading")
                            ApiStatus.SUCCESS -> {
                                loadingDialog.dismiss()
                                openActivity<ProfileActivity>()
                                finishAffinity()
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }
}