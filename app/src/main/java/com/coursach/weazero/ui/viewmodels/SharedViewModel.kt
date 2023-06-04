package com.coursach.weazero.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val locationCity: MutableLiveData<String> = MutableLiveData<String>()

}
