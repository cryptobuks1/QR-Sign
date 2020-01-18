package danvim.qrsign.ui.scan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import danvim.qrsign.utils.SignedMessage
import danvim.qrsign.utils.Validator
import danvim.qrsign.utils.VerifiedStatus

class ScanViewModel : ViewModel() {
    val scanResult = MutableLiveData<String>().apply { value = "" }
    val foundQRCode = MutableLiveData<Boolean>().apply { value = false }
    val message = MutableLiveData<SignedMessage?>()
    val validationResult = MutableLiveData<Validator.ValidationResult?>()
    val containsValidMessage = Transformations.map(message) { message -> message != null}
    val showVerifiedStatus = Transformations.map(message) { message -> VerifiedStatus.showVerifiedStatus.contains(message?.message?.keyType)}
    val publicKeyFound = Transformations.map(validationResult) {validationResult -> validationResult != null}
    val isVerified = Transformations.map(validationResult) {validationResult -> validationResult?.isVerified}
    val isWellSigned = Transformations.map(validationResult) {validationResult -> validationResult?.isWellSigned}
}