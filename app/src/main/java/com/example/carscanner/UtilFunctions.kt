package com.example.carscanner

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.content.Context
import android.content.Context.MODE_PRIVATE
import org.json.JSONArray

object Anim {
    fun blink(view: View, color1:Int, color2:Int, duration: Long){
        ObjectAnimator.ofArgb(view, "backgroundColor", color1, color2).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()

        }
        /*ValueAnimator.ofInt(0,1).apply {
            this.duration = duration
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE

            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                val color = if (value == 0) color1 else color2
                view.setBackgroundColor(color)
            }
            start()
        }*/
    }
}

object LocalStore {
    private const val NAME = "local_prefs"

    private fun prefs(ctx: Context) =
        ctx.getSharedPreferences(NAME, MODE_PRIVATE)


    fun putInt(ctx: Context, key: String, value: Int) {
        prefs(ctx).edit().putInt(key, value).apply()
    }
    fun getInt(ctx: Context, key: String, default: Int = 0): Int {
        return prefs(ctx).getInt(key, default)
    }

    fun putBoolean(ctx: Context, key: String, value: Boolean) {
        prefs(ctx).edit().putBoolean(key, value).apply()
    }
    fun getBoolean(ctx: Context, key: String, default: Boolean = false): Boolean {
        return prefs(ctx).getBoolean(key, default)
    }

    fun putStringArray(ctx: Context, key: String, array: List<String>) {
        val jsonArray = JSONArray()
        array.forEach { jsonArray.put(it) }
        prefs(ctx).edit().putString(key, jsonArray.toString()).apply()
    }
    fun getStringArray(ctx: Context, key: String): List<String> {
        val json = prefs(ctx).getString(key, null) ?: return emptyList()
        val jsonArray = JSONArray(json)
        val list = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getString(i))
        }
        return list
    }
    fun addStringToArray(ctx: Context, key: String, value: String) {
        val current = getStringArray(ctx, key).toMutableList()
        current.add(value)
        putStringArray(ctx, key, current)
    }
    fun removeStringFromArray(ctx: Context, key: String, value: String) {
        val current = getStringArray(ctx, key).toMutableList()
        current.remove(value)
        putStringArray(ctx, key, current)
    }
}