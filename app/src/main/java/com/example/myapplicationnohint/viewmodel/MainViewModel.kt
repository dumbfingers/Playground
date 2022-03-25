package com.example.myapplicationnohint.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationnohint.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.math.log

class MainViewModel(
    val repository: MainRepository,
    val viewState: MainViewState = MainViewState()
): ViewModel() {
    private val _state = MutableStateFlow(viewState)
    val state: StateFlow<MainViewState>
        get() = _state.asStateFlow()

    private val actions = MutableSharedFlow<MainViewAction>()

    private val randomFlow = MutableStateFlow(arrayListOf(1, 2))

    init {
        viewModelScope.launch {
            actions.collect {
                when(it) {
                    is Load -> load()
                    is SaveContent1 -> onSave("content1", it.content1)
                    is SaveContent2 -> onSave("content2", it.content2)
                }
            }
        }

        viewModelScope.launch {
            _state.collect()
        }

        viewModelScope.launch(Dispatchers.IO) {
            randomFlow.collect {
                onFlowChanged(it)
            }
        }
    }

    fun dispatch(action: MainViewAction) {
        viewModelScope.launch {
            actions.emit(action)
        }
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                text1 = repository.readFromPref1(),
                text2 = repository.readFromPref2()
            )
        }
    }

    private fun onSave(key: String, value: String) {
        when(key) {
            "content1" -> repository.writeToPref1(value)
            "content2" -> repository.writeToPref2(value)
        }
    }

    fun onArrayUpdated(list: ArrayList<Int>) {
        Log.d("Test", "onArrayUpdated")
        printArray(list)
        randomFlow.value = list
    }

    private suspend fun onFlowChanged(list: ArrayList<Int>) {
        Log.d("Test", "onFlowChanged")
        // launch some time consuming flow
        repository.repoObserve(list).collect {
            repository.repoAnotherObserve().collect()
        }
    }

    private fun printArray(list: ArrayList<Int>) {
        list.forEach {
            Log.d("Test", "item: $it")
        }
    }
}