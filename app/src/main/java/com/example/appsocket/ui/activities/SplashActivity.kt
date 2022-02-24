package com.example.appsocket.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.appsocket.R
import com.example.appsocket.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        overridePendingTransition(
            R.anim.fade_in,
            R.anim.fade_out
        )

        binding.imgSplashLogo.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.splash_in
            )
        )


        Handler().postDelayed({
            binding.imgSplashLogo.startAnimation(
                AnimationUtils.loadAnimation(
                    this,
                    R.anim.splash_out
                )
            )
            Handler().postDelayed({
                binding.imgSplashLogo.visibility = View.GONE
                goToLoginActivity()
            }, 500)
        }, 900)


        window.decorView.apply {
            // Hide both the navigation bar and the status bar.
            // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
            // a general rule, you should design your app to hide the status bar whenever you
            // hide the navigation bar.
            systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }

    }

    private fun goToLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        finish()
    }
}