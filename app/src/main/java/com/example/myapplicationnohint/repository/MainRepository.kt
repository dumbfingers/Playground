package com.example.myapplicationnohint.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class MainRepository(
    context: Context
) {

    val sharedPreferences by lazy {
        context.getSharedPreferences("Test", Context.MODE_PRIVATE)
    }

    fun writeToPref1(content1: String) {
        sharedPreferences.edit().putString("content1", content1).apply()
        Log.d("repository", "content1 saved: $content1")
    }

    fun writeToPref2(content2: String) {
        sharedPreferences.edit().putString("content2", content2).apply()
        Log.d("repository", "content2 saved: $content2")
    }

    fun readFromPref1(): String {
        return sharedPreferences.getString("content1", "null1") ?: "null1"
    }

    fun readFromPref2(): String {
        return sharedPreferences.getString("content1", "null2") ?: "null2"
    }

    fun repoAnotherObserve(): Flow<Unit> {
        return flow {
            delay(5_000)
            Log.d("Test", "repoAnotherObserve delayed")
        }
    }

    fun repoObserve(list: ArrayList<Int>): Flow<Unit> {
        return flow<Unit> {
            delay(5_000)
            Log.d("Test", "repoObserve delayed")
            printArray(list)
        }
    }

    private fun printArray(list: ArrayList<Int>) {
        list.forEach {
            Log.d("Test", "item: $it")
        }
    }
}