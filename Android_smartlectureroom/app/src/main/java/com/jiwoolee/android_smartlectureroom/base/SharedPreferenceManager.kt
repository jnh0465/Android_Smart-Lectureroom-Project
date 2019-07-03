package com.jiwoolee.android_smartlectureroom.base

import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceManager {

    private const val PREFERENCES_NAME = "rebuild_preference"
    private const val DEFAULT_VALUE_STRING = ""
    private const val DEFAULT_VALUE_BOOLEAN = false
    private const val DEFAULT_VALUE_Int= 0

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    /* String 저장 */
    fun setString(context: Context, key: String, value: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /* Int 저장 */
    fun setInt(context: Context, key: String, value: Int) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /* boolean 저장 */
    fun setBoolean(context: Context, key: String, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /* token 저장 */
    fun setToken(context: Context, key: String, value: Boolean) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /* String 로드 */
    fun getString(context: Context, key: String): String? {
        val prefs = getPreferences(context)
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /* Int 로드 */
    fun getInt(context: Context, key: String): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(key, DEFAULT_VALUE_Int)
    }

    /* boolean 로드 */
    fun getBoolean(context: Context, key: String): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /* token 로드 */
    fun getToken(context: Context, key: String): Boolean {
        val prefs = getPreferences(context)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /* 키 값 삭제 */
    fun removeKey(context: Context, key: String) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.remove(key)
        edit.apply()
    }

    /*모든 데이터 삭제*/
    fun clear(context: Context) {
        val prefs = getPreferences(context)
        val edit = prefs.edit()
        edit.clear()
        edit.apply()
    }
}