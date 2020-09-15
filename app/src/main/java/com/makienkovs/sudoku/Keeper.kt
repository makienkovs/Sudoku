package com.makienkovs.sudoku

import android.content.Context

class Keeper(context: Context){

    private val preferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)

    fun save(parameter: String, value: String) {
        val editor = preferences.edit()
        editor.putString(parameter, value)
        editor.apply()
    }

    fun load(parameter: String): String? {
        return if (preferences.contains(parameter)) {
            preferences.getString(parameter, "0")
        } else {
            "0"
        }
    }
}