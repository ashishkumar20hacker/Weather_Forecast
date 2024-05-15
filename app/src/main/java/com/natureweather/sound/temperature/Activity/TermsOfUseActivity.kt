package com.natureweather.sound.temperature.Activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.gotoUrl
import com.natureweather.sound.temperature.Extras.Utils.nextActivity
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityTermsOfUseBinding

class TermsOfUseActivity : AppCompatActivity() {

    lateinit var binding: ActivityTermsOfUseBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityTermsOfUseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.agreebtn.setOnClickListener {
            nextActivity(
                this@TermsOfUseActivity,
                DashboardActivity::class.java
            )
        }

        binding.termsAndConditionTv.setOnClickListener {
            startActivity(
                Intent(
                    this@TermsOfUseActivity,
                    TermsConditions::class.java
                )
            )
        }

        binding.ppTv.setOnClickListener {
            gotoUrl(this)
        }
    }
}