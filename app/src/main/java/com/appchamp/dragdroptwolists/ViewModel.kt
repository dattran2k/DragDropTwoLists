package com.appchamp.dragdroptwolists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object ViewModel {
    var screenWidth : Int = 0
    val screenSize =  MutableLiveData(0)
    val isEnableEditModel = MutableLiveData(false)

    fun updateScreenSize(){
        screenSize.value = if(isEnableEditModel.value == true) screenWidth /2 else screenWidth
    }
}