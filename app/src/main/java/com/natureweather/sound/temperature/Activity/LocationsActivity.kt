package com.natureweather.sound.temperature.Activity

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.natureweather.sound.temperature.Adapter.LocationsAdapter
import com.natureweather.sound.temperature.Extras.Constants.SELECTED_ADDRESS
import com.natureweather.sound.temperature.Extras.DataFetcher
import com.natureweather.sound.temperature.Extras.SharePreferences
import com.natureweather.sound.temperature.Extras.Utils
import com.natureweather.sound.temperature.Extras.Utils.convertAddress
import com.natureweather.sound.temperature.Extras.Utils.hideKeyboard
import com.natureweather.sound.temperature.Model.LocationModel
import com.natureweather.sound.temperature.databinding.ActivityLocationsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LocationsActivity : AppCompatActivity() {
    lateinit var binding: ActivityLocationsBinding
    lateinit var preferences: SharePreferences
    var locationModels: MutableList<LocationModel> = mutableListOf()
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
                                binding.enterCity.setText("")
                                hideKeyboard(this@LocationsActivity)
                                latlong = convertAddress(this@LocationsActivity, enteredLocation)
                                preferences.addStringItem(enteredLocation)
                                preferences.putString(SELECTED_ADDRESS,enteredLocation)
                                lifecycleScope.launch(Dispatchers.IO) {
                                    fetchDataAndLoadRecyclerView(enteredLocation, latlong!!)
                                }
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

    private fun fetchDataAndLoadRecyclerView(location: String, latlong: String) {
        DataFetcher.searchWeather(latlong) { elements ->
            elements?.let {
                // Process the fetched data and create LocationModel instance for each location
                val temperature = it.select("span[class=CurrentConditions--tempValue--MHmYY]").text()
                val condition = it.select("div[class=CurrentConditions--phraseValue--mZC_p]").text()
                val maxmin = it.select("div[class=CurrentConditions--tempHiLoValue--3T1DG]").text()
                val max = "Max.: ${maxmin.substring(4, 7)}"
                val min = "  Min.: ${maxmin.substring(maxmin.length - 3, maxmin.length)}"

                val model = LocationModel(location, temperature, condition, max, min)
                locationModels.add(model)

                // Update the RecyclerView with the new data
                runOnUiThread {
                    adapter.notifyDataSetChanged()
                }
            } ?: run {
                // Handle error case
                println("Something went wrong!!")
            }
        }
    }

    private fun loadRecyclerView() {
        val locationsList = preferences.getStringList()
        if (locationsList.isNotEmpty()) {
            adapter = LocationsAdapter(this@LocationsActivity, locationModels)
            binding.locationRv.adapter = adapter
            // Fetch data for each location in the list
            for (location in locationsList) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val latlong: String = convertAddress(this@LocationsActivity, location)
                    fetchDataAndLoadRecyclerView(location, latlong)
                }
            }
        }
    }

}