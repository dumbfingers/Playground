package com.example.myapplicationnohint.viewmodel

sealed class MainViewAction
object Load : MainViewAction()
data class SaveContent1(val content1: String) : MainViewAction()
data class SaveContent2(val content2: String) : MainViewAction()

