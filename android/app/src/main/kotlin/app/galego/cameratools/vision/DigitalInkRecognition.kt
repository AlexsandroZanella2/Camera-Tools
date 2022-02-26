package app.galego.cameratools.vision

import androidx.annotation.NonNull
import app.galego.cameratools.interfaces.MethodChannelInterface
import app.galego.cameratools.converters.Converters

import android.util.Log
import kotlin.experimental.inv

import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.google.mlkit.vision.digitalink.RecognitionCandidate
import com.google.mlkit.vision.digitalink.RecognitionResult

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.nio.ReadOnlyBufferException

import java.nio.*
import java.util.*

import android.content.Context
import android.content.ContextWrapper

public class DigitalInkRecognition(var _context: Context): MethodChannelInterface {
    private val START: String = "start#DigitalInkRecognition"
    private val CLOSE: String = "close#DigitalInkRecognition"

    private var context: Context? = null

    init {
        this.context = _context
    }

    override fun getMethodsKeys(): List<String> {
        return listOf(START, CLOSE)
    }

    override fun onMethodCall(@NonNull call: MethodCall?, @NonNull result: MethodChannel.Result?){
        val method: String? = call?.method
        if (method == START) {
            handleDetection(call!!, result!!)
        } else {
            result?.error("ImplementionException","Not Implemented Method", null)
        }
    }

    private fun handleDetection(call: MethodCall, result: MethodChannel.Result){

    }

}