package io.github.servb.eShop.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val gson = Gson()

inline fun <reified T> String?.parse(): T = gson.fromJson(this, object : TypeToken<T>() {}.type)
