package danvim.qrsign.ui.keys

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KeysViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is keys Fragment"
    }
    val text: LiveData<String> = _text
}