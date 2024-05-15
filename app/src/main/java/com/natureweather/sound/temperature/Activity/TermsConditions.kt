package com.natureweather.sound.temperature.Activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityTermsConditionsBinding

class TermsConditions : AppCompatActivity() {

    lateinit var binding: ActivityTermsConditionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityTermsConditionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {

//                AdUtils.showBackPressAd(activity) { isLoaded: Boolean ->
                    finish()
//                }
            }
        })
        binding.backbt.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

    }

}