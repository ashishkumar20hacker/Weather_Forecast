package com.natureweather.sound.temperature.Extras

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.natureweather.sound.temperature.R
import java.lang.reflect.Type


class SharePreferences(private val applicationContext: Context) {
    private val gson: Gson
    private val sharedPreferences: SharedPreferences
    private val LocationsListKey = "LocationsListKey"

    init {
        gson = Gson()
        val preferencesName = applicationContext.getString(R.string.app_name)
        sharedPreferences =
            applicationContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    }

    fun putString(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String?, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun putInt(key: String?, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    fun getInt(key: String?, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    fun putBoolean(key: String?, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }


    fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun putStringList(list: List<String>?) {
        val editor = sharedPreferences.edit()
        val dataModelListJson = gson.toJson(list)
        println("Json uploading$dataModelListJson")
        editor.putString(LocationsListKey, dataModelListJson)
        editor.apply()
    }

    fun getStringList(): MutableList<String> {
        val dataModelListJson = sharedPreferences.getString(LocationsListKey, null)
        return if (dataModelListJson != null) {
            println("Json fetching$dataModelListJson")
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
            gson.fromJson<MutableList<String>>(dataModelListJson, type)
        } else {
            ArrayList<String>()
        }
    }

    // Method to remove a String from the list and update SharedPreferences
    fun removeStringItem(stringToRemove: String) {
        val list: MutableList<String> = getStringList()
        if (!list.isEmpty()) {

            // Find and remove the specified String from the list
            for (str in list) {
                if (str.equals(stringToRemove)
                ) {
                    list.remove(str)
                    break // Stop looping once the String is found and removed
                }
            }
        }

        // Update the modified list in SharedPreferences
        putStringList(list)
    }

    // Method to remove a String from the list and update SharedPreferences
    fun addStringItem(stringToAdd: String) {
        val list: MutableList<String> = getStringList()
        if (!list.isEmpty()) {

            // Find and remove the specified String from the list
            for (str in list) {
                if (str.equals(stringToAdd)
                ) {
                    return // Stop looping once the String is found
                }
            }
        }

        list.add(stringToAdd)

        // Update the modified list in SharedPreferences
        putStringList(list)
    }

}

