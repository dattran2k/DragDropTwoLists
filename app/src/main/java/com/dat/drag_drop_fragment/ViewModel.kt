package com.dat.drag_drop_fragment

import androidx.lifecycle.MutableLiveData

object ViewModel {
    var screenWidth : Int = 0
    val screenSize =  MutableLiveData(0)
    val isEnableEditModel = MutableLiveData(false)

    fun updateScreenSize(){
        screenSize.value = if(isEnableEditModel.value == true) screenWidth /2 else screenWidth
    }
}