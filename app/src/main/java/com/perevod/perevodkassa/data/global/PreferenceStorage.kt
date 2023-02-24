package com.perevod.perevodkassa.data.global

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Kamil' Abdulatipov on 25/02/23.
 */
interface PreferenceStorage {
    var isFirstLaunch: Boolean
    var token: String?
    var isLogged: Boolean
    var deviceId: String?
}

class SharedPreferenceStorage(context: Context) : PreferenceStorage {

    private val gson = Gson()

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override var isFirstLaunch by BooleanPreference(prefs, PREF_FIRST_LAUNCH, true)

    override var token by StringPreference(prefs, PREF_TOKEN, null)

    override var isLogged by BooleanPreference(prefs, PREF_IS_LOGGED, false)

    override var deviceId by StringPreference(prefs, PREF_DEVICE_ID, null)

    companion object {
        const val PREF_NAME = "SHARED_PREFERENCES_FILE_COMMON"
        const val PREF_FIRST_LAUNCH = "PREF_FIRST_LAUNCH"
        const val PREF_IS_LOGGED = "PREF_IS_LOGGED"
        const val PREF_TOKEN = "PREF_TOKEN"
        const val PREF_DEVICE_ID = "PREF_DEVICE_ID"
    }
}


class StringPreference(
    private val pref: SharedPreferences,
    private val key: String,
    private val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return pref.getString(key, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        pref.edit().run {
            value?.let { putString(key, it).apply() } ?: remove(key).apply()
        }
    }
}


class BooleanPreference(
    private val pref: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) =
        pref.edit().putBoolean(key, value).apply()

    override fun getValue(thisRef: Any, property: KProperty<*>) =
        pref.getBoolean(key, defaultValue)
}


class ListPreference<T>(
    private val pref: SharedPreferences,
    private val key: String,
    private val gson: Gson
) : ReadWriteProperty<Any, MutableList<T>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): MutableList<T> {
        val listType = object : TypeToken<List<T>>() {}.type
        return gson.fromJson(pref.getString(key, ""), listType) ?: mutableListOf()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: MutableList<T>) {
        pref.edit().putString(key, gson.toJson(value)).apply()
    }
}