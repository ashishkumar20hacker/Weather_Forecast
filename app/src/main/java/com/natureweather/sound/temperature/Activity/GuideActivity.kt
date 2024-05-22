package com.natureweather.sound.temperature.Activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.nextActivity
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {

    lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.skip1.setOnClickListener{
            nextActivity(this@GuideActivity, DashboardActivity::class.java)
        }

        binding.skip2.setOnClickListener{
            nextActivity(this@GuideActivity, DashboardActivity::class.java)
        }

        binding.skip3.setOnClickListener{
            nextActivity(this@GuideActivity, DashboardActivity::class.java)
        }

        binding.gotIt1.setOnClickListener{
            binding.ll1.visibility = View.GONE
            binding.ll3.visibility = View.GONE
            binding.ll2.visibility = View.VISIBLE
            binding.main.setBackgroundResource(R.drawable.guide_two)
        }

        binding.gotIt2.setOnClickListener{
            binding.ll1.visibility = View.GONE
            binding.ll2.visibility = View.GONE
            binding.ll3.visibility = View.VISIBLE
            binding.main.setBackgroundResource(R.drawable.guide_three)
        }

        binding.gotIt3.setOnClickListener{
            nextActivity(this@GuideActivity, DashboardActivity::class.java)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                if (binding.ll3.visibility == View.VISIBLE) {
                    binding.ll1.visibility = View.GONE
                    binding.ll3.visibility = View.GONE
                    binding.ll2.visibility = View.VISIBLE
                    binding.main.setBackgroundResource(R.drawable.guide_two)
                } else if (binding.ll2.visibility == View.VISIBLE) {
                    binding.ll2.visibility = View.GONE
                    binding.ll3.visibility = View.GONE
                    binding.ll1.visibility = View.VISIBLE
                    binding.main.setBackgroundResource(R.drawable.guide_one)
                } else {
                    finish()
                }
            }
        })

    }
}