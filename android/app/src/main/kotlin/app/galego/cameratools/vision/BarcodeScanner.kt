package app.galego.cameratools.vision

import androidx.annotation.NonNull
import app.galego.cameratools.interfaces.MethodChannelInterface
import app.galego.cameratools.converters.Converters

import android.util.Log
import kotlin.experimental.inv

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.nio.ReadOnlyBufferException

import java.nio.*
import java.util.*

//import android.graphics.Bitmap
//import android.graphics.ImageFormat
//import android.media.Image
//import android.media.ImageReader
//import java.math.BigInteger

import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.barcode.BarcodeScanning

import android.content.Context
import android.content.ContextWrapper

public  class BarcodeScanner(var _context: Context): MethodChannelInterface{
    private val START: String = "start#BarcodeScanner"
    private val CLOSE: String = "close#BarcodeScanner"

    var barcodeScanner: BarcodeScanner? = null

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
        try{
            val image = Converters.InputImageFromMethodCall(call)

            val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                            Barcode.FORMAT_QR_CODE,
                            Barcode.FORMAT_EAN_13,
                            Barcode.FORMAT_EAN_8,
                            Barcode.FORMAT_CODE_128,
                            Barcode.FORMAT_CODE_39,
                            Barcode.FORMAT_CODE_93,
                            Barcode.FORMAT_CODABAR,
                            Barcode.FORMAT_ITF,
                            Barcode.FORMAT_UPC_A,
                            Barcode.FORMAT_UPC_E,
                            Barcode.FORMAT_PDF417,
                            Barcode.FORMAT_AZTEC,
                            Barcode.FORMAT_DATA_MATRIX,
                    )
                    .build()

            if(barcodeScanner === null)
                barcodeScanner = BarcodeScanning.getClient(options)
            barcodeScanner!!.process(image)
                    .addOnSuccessListener { barcodes ->
                        var res = ""
                        for (barcode in barcodes) {
                            var resul = barcode.getDisplayValue()
                            if(resul !== null){
                                res = resul
                            }
                            break
                        }
                        result.success(res)
                    }
                    .addOnFailureListener {
                        OnFailureListener() {
                            fun onFailure(@NonNull e: Exception) {
                                result.error("BarcodeDetectorError", e.toString(), null)
                            }
                        }
                    }
        }catch (e: Exception){
            Log.e("handleDetection", "Erro ao converter imagem", e)
            result.error("handleDetection", e.toString(), null)
        }

    }


}