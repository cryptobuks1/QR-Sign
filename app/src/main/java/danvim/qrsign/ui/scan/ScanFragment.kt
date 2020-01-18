package danvim.qrsign.ui.scan

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import danvim.qrsign.R
import danvim.qrsign.exceptions.InvalidMessageFormatException
import danvim.qrsign.exceptions.PublicKeyNotFoundException
import danvim.qrsign.utils.Validator
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.net.URLDecoder

class ScanFragment : Fragment(), ZXingScannerView.ResultHandler, CoroutineScope {
    private lateinit var scanViewModel: ScanViewModel
    override val coroutineContext = Dispatchers.Main

    override fun handleResult(rawResult: Result?) {
        val text = rawResult!!.text
        val urlPartition = text.split("/s?")
        if (urlPartition.size != 2) {
            Log.e("QR Sign", "Not a valid code")
            scanViewModel.message.value = null
            scanViewModel.validationResult.value = null
            scanViewModel.foundQRCode.value = true
            return
        }
        val content = URLDecoder.decode(urlPartition[1], "UTF-8")
        scanViewModel.scanResult.value = content
        launch {
            val (message, validationResult) = withContext(Dispatchers.IO) {
                val validator = Validator()
                try {
                    val message = validator.getMessage(content)
                    try {
                        val scrapeResult = validator.scrapePublicKey(message)
                        Pair(
                            message,
                            validator.validateMessage(
                                message,
                                scrapeResult.key,
                                scrapeResult.isVerified
                            )
                        )
                    } catch (e: PublicKeyNotFoundException) {
                        Log.e("QR Sign", e.toString())
                        Pair(message, null)
                    }
                } catch (e: InvalidMessageFormatException) {
                    Log.e("QR Sign", e.toString())
                    Pair(null, null)
                }
            }
            scanViewModel.message.value = message
            scanViewModel.validationResult.value = validationResult
            scanViewModel.foundQRCode.value = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        scanViewModel =
            ViewModelProviders.of(this).get(ScanViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_scan, container, false)
        val textScanResult: TextView = root.findViewById(R.id.text_scan_result)
        scanViewModel.scanResult.observe(this, Observer { textScanResult.text = it })
        scanViewModel.foundQRCode.observe(this, Observer { text_found_qr_code.text = if (it) "true" else "false" })

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Permissions
        Dexter.withActivity(this.activity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    zxing.setResultHandler(this@ScanFragment)
                    zxing.setFormats(listOf(BarcodeFormat.QR_CODE))
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(this@ScanFragment.activity, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show()
                }

            })
            .check()
    }

    override fun onStart() {
        super.onStart()
        zxing.startCamera()
    }

    override fun onStop() {
        super.onStop()
        zxing.stopCamera()
    }
}