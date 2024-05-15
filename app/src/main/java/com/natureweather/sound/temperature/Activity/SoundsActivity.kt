package com.natureweather.sound.temperature.Activity

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.natureweather.sound.temperature.Adapter.SoundsAdapter
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Model.SoundsModel
import com.natureweather.sound.temperature.R
import com.natureweather.sound.temperature.databinding.ActivitySoundsBinding


class SoundsActivity : AppCompatActivity(), SoundsAdapter.OnClickListener {
    lateinit var binding: ActivitySoundsBinding
    var media: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivitySoundsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        backButtonPressedListener()

        media = MediaPlayer()

        fillList()

        binding.backbt.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun fillList() {
        val soundsModelArrayList = ArrayList<SoundsModel>()
        soundsModelArrayList.add(SoundsModel(R.drawable.waves, R.raw.ocean_waves, "Ocean Waves"))
        soundsModelArrayList.add(SoundsModel(R.drawable.forest, R.raw.forest, "Forest"))
        soundsModelArrayList.add(SoundsModel(R.drawable.rainfall, R.raw.rainfall, "Rainfall"))
        soundsModelArrayList.add(SoundsModel(R.drawable.waterfall, R.raw.waterfall, "Waterfalls"))
        soundsModelArrayList.add(
            SoundsModel(
                R.drawable.dove,
                R.raw.morning_birds,
                "Morning Birds"
            )
        )
        soundsModelArrayList!!.add(SoundsModel(R.drawable.thunder, R.raw.thunder, "Thunderstorms"))
        soundsModelArrayList!!.add(SoundsModel(R.drawable.desert, R.raw.desert, "Desert Wind"))
        soundsModelArrayList!!.add(
            SoundsModel(
                R.drawable.mountain,
                R.raw.mountains,
                "Mountain Streams"
            )
        )
        soundsModelArrayList.add(SoundsModel(R.drawable.whale, R.raw.whale, "Whales"))
        soundsModelArrayList.add(SoundsModel(R.drawable.wind_gif, R.raw.wind, "Wind Chimes"))
        val soundsAdapter = SoundsAdapter(this, soundsModelArrayList, this)
        binding.soundsRv.setAdapter(soundsAdapter)
    }

    private fun backButtonPressedListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                //        if (media != null) {
                if (media!!.isPlaying) {
                    media!!.stop()
                    media!!.release()
                }
                //        }
                finish()
            }
        })
    }

    override fun onplay(audio: Int) {
        if (audio != 0) {
            if (media!!.isPlaying) {
                media!!.stop()
                media = MediaPlayer.create(applicationContext, audio)
                media!!.start()
                media!!.setLooping(true)
            } else {
                media = MediaPlayer.create(applicationContext, audio)
                media!!.start()
                media!!.setLooping(true)
            }
        }
    }

    override fun onpause() {
        media!!.stop()
    }
}