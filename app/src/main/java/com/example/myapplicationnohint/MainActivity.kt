package com.example.myapplicationnohint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationnohint.repository.MainRepository
import com.example.myapplicationnohint.ui.MainUi
import com.example.myapplicationnohint.ui.theme.MyApplicationNoHintTheme
import com.example.myapplicationnohint.viewmodel.Factory
import com.example.myapplicationnohint.viewmodel.Load
import com.example.myapplicationnohint.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var vm: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = ViewModelProvider(
            this,
            Factory(MainRepository(this))
        ).get(MainViewModel::class.java)

        vm.dispatch(Load)

        setContent {
            MyApplicationNoHintTheme {
                MainUi(viewModel = vm)
            }
        }
    }
}