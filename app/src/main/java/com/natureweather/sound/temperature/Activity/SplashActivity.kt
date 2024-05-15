package com.natureweather.sound.temperature.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.natureweather.sound.temperature.Extras.Constants
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var preferences: SharePreferences

    companion object {

        var condition: String = ""
        var temperature: String = ""
        var max: String = ""
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
        Handler().postDelayed({
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
        }, 2000)
    }

}