package com.abdiel.schoolio.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.abdiel.schoolio.R
import com.abdiel.schoolio.base.activity.BaseActivity
import com.abdiel.schoolio.databinding.ActivityRegisterBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity : BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnRegister.setOnClickListener {
            if (binding.etName.isEmptyRequired(R.string.label_must_fill) ||
                binding.etEmail.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPhone.isEmptyRequired(R.string.label_must_fill) ||
                binding.etPassword.isEmptyRequired(R.string.label_must_fill) ||
                binding.etConfirmPassword.isEmptyRequired(R.string.label_must_fill))
            {
                return@setOnClickListener
            }

            val name = binding.etName.textOf()
            val email = binding.etEmail.textOf()
            val phone = binding.etPhone.textOf()
            val password = binding.etPassword.textOf()
            val passwordConfirmation = binding.etConfirmPassword.textOf()

            if (name.length < 3) {
                binding.root.snacked("name minimum 3 characters")

                return@setOnClickListener
            }

            if (password.length < 8 && passwordConfirmation.length < 8) {
                binding.root.snacked("Password minimum 8 characters")

                return@setOnClickListener
            }

            if (password.length > 20 && passwordConfirmation.length < 8) {
                binding.root.snacked("Password maximum 20 characters")

                return@setOnClickListener
            }

            viewModel.register(name, email, phone, password, passwordConfirmation)
        }

        binding.btnBack.setOnClickListener{
            finish()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show("Loading...")
                            ApiStatus.SUCCESS -> {
                                tos("register success")
                                loadingDialog.dismiss()
                                finish()
                                }
                            ApiStatus.ERROR -> {
                                tos("register failed")
                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }
                    }
                }
            }
        }
    }
}