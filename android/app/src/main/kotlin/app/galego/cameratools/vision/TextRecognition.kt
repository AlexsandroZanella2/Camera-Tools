package app.galego.cameratools.vision

import androidx.annotation.NonNull

import app.galego.cameratools.interfaces.MethodChannelInterface

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import com.google.mlkit.vision.text.TextRecognizer
//import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

import com.google.mlkit.vision.common.InputImage

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

import android.content.Context
import android.net.Uri
import android.util.Log

import java.io.File

public class TextRecognition(var _context: Context): MethodChannelInterface {
    private val START: String = "start#TextRecognition"
    private val CLOSE: String = "close#TextRecognition"

    var recognizer : TextRecognizer? = null

    private var context: Context? = null

    init {
        this.context = _context
    }

    override fun getMethodsKeys(): List<String> {
        return listOf(START, CLOSE)
    }

    override fun onMethodCall(@NonNull call: MethodCall?, @NonNull result: MethodChannel.Result?){
        val method: String? = call?.method
        if (method == "") {
            handleDetection(call!!, result!!)
        } else if (method == "") {
            result?.success(null)
        } else {
            result?.error("ImplementionException","Not Implemented Method", null)
        }
    }

    private fun handleDetection(call: MethodCall, result: MethodChannel.Result){
        try{
            val filePath = call.argument<String>("path")

            var image = InputImage.fromFilePath(context!!, Uri.fromFile(java.io.File(filePath!!)))

            if(recognizer === null)
                recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer!!.process(image)
                    .addOnSuccessListener { visionText ->
                        result.success(visionText.getText())
                    }
                    .addOnFailureListener {
                        OnFailureListener() {
                            fun onFailure(@NonNull e: Exception) {
                                result.error("TextDetectorError", e.toString(), null)
                            }
                        }
                    }
        }catch (e: Exception){
            Log.e("handleDetection", "Erro ao detectar texto", e)
            result.error("handleDetection", e.toString(), null)
        }

    }


}