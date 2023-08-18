package com.example.ethwallet.mainscreen

sealed class MainScreenAction{
    data class NavigateToScreen(val route:String):MainScreenAction()
}
