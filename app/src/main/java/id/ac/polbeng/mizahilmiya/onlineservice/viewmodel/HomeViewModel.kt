package id.ac.polbeng.mizahilmiya.onlineservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ini adalah halaman home Fragment"
    }
    val text: LiveData<String> = _text
}