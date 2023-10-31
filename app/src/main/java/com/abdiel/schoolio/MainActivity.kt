package com.abdiel.schoolio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.abdiel.schoolio.databinding.ActivityMainBinding
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity

class MainActivity : NoViewModelActivity <ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.tvSchoolio.setOnClickListener {
            openActivity<LoginActivity>()
        }
    }
}