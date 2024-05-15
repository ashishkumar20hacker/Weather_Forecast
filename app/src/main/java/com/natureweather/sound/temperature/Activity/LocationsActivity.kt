package com.natureweather.sound.temperature.Activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.natureweather.sound.temperature.Adapter.LocationsAdapter
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.databinding.ActivityLocationsBinding


class LocationsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationsBinding
    lateinit var preferences: SharePreferences
    lateinit var locationsList: MutableList<String>
    lateinit var adapter : LocationsAdapter
    var latlong: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.makeStatusBarTransparent2(this)
        binding = ActivityLocationsBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        preferences = SharePreferences(this)

        binding.add.setOnClickListener { binding.enterCity.setVisibility(View.VISIBLE) }
        backButtonPressedListener()

        binding.backbt.setOnClickListener{ onBackPressedDispatcher.onBackPressed() }

        binding.enterCity.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
                if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (i) {
                        KeyEvent.KEYCODE_ENTER -> {
                            val enteredLocation: String =
                                binding.enterCity.getText().toString().trim()
                            if (!enteredLocation.isEmpty()) {
                                binding.enterCity.setVisibility(View.GONE)
                                latlong = convertAddress(this@LocationsActivity, enteredLocation)
                                preferences.addStringItem(enteredLocation)
                                preferences.putString(SELECTED_ADDRESS,enteredLocation)
                                loadRecyclerView()
                            }
                            return true
                        }

                        else -> {}
                    }
                }
                return false
            }
        })

        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        locationsList = preferences.getStringList()
        if (locationsList.size != 0) {
            adapter = LocationsAdapter(this@LocationsActivity, locationsList)
            binding.locationRv.setAdapter(adapter)
        }
    }

    private fun backButtonPressedListener() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(applicationContext, DashboardActivity::class.java))
                finish()
            }
        })
    }

}