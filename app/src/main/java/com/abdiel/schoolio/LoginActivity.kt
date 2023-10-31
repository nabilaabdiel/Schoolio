package com.abdiel.schoolio

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.abdiel.schoolio.data.constant.Const
import com.abdiel.schoolio.databinding.ActivityLoginBinding
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.tos
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : NoViewModelActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var session: CoreSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this@LoginActivity)

        generateFcmToken {
            if (it){
                binding.root.snacked("ada login")
            } else {
                binding.root.snacked("tidak ada device token")
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