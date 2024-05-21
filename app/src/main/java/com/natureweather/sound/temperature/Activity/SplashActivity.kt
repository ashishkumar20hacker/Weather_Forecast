package com.natureweather.sound.temperature.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.natureweather.sound.temperature.Extras.Constants
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.databinding.ActivitySplashBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var preferences: SharePreferences

    companion object {
        var currentCondition: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferences = SharePreferences(this)

        nextActivity()

    }

    private fun nextActivity() {
        lifecycleScope.launch(Dispatchers.Main){
            delay(2000)
            if (preferences.getBoolean(Constants.isFirstRun, true)) {
                val intent = Intent(
                    this@SplashActivity,
                    OnboardingActivity::class.java
                )
                startActivity(intent)
                finish()
            } else {
                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                finish()
            }
        }
    }

}