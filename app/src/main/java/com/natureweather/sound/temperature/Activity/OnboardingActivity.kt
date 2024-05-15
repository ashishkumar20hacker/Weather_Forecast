package com.natureweather.sound.temperature.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.natureweather.sound.temperature.Adapter.OnboardingAdapter
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.nextActivity
import com.natureweather.sound.temperature.Model.OnboardModel
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnboardingBinding
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = OnboardingAdapter(object : OnboardingAdapter.OnNextBtnClickListener {
            override fun onClick() {
                if (binding.viewPager.currentItem + 1 < adapter.itemCount) {
                    binding.viewPager.currentItem += 1
                } else {
                    nextActivity(this@OnboardingActivity, TermsOfUseActivity::class.java)
                }
            }
        })

        adapter.submitList(getBoardList())
        binding.viewPager.adapter = adapter
    }

    private fun getBoardList(): List<OnboardModel> {
        return listOf(
            OnboardModel(
                image = R.drawable.one,
                dotsImage = R.drawable.dot_one,
                title = getString(R.string.ob_one_title),
                description = getString(R.string.ob_one_desc)
            ),
            OnboardModel(
                image = R.drawable.two,
                dotsImage = R.drawable.dot_two,
                title = getString(R.string.ob_two_title),
                description = getString(R.string.ob_two_desc)
            ),
            OnboardModel(
                image = R.drawable.three,
                dotsImage = R.drawable.dot_three,
                title = getString(R.string.ob_three_title),
                description = getString(R.string.ob_three_desc)
            )
        )
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.viewPager.currentItem -= 1
        }
    }
}