package id.ac.polbeng.mizahilmiya.onlineservice.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Ini adalah halaman profile Fragment"
    }
    val text: LiveData<String> = _text
}
