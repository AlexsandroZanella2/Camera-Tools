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
//            val width = call.argument<Int>("width")
//            val height = call.argument<Int>("height")
//            val rotation = call.argument<Int>("rotation")
//            val p0 = call.argument<ByteArray>("plane0")
//            val p1 = call.argument<ByteArray>("plane1")
//            val p2 = call.argument<ByteArray>("plane2")
//            val prs0 = call.argument<Int>("prs0")
//            val prs1 = call.argument<Int>("prs1")
//            val prs2 = call.argument<Int>("prs2")
//            val pps0 = call.argument<Int>("pps0")
//            val pps1 = call.argument<Int>("pps1")
//            val pps2 = call.argument<Int>("pps2")
//            val dataImage = YUV_420_888toNV21(width!!, height!!, p0!!, p1!!, p2!!, prs0!!, prs1!!, prs2!!, pps0!!, pps1!!, pps2!!)
            val image = Converters.InputImageFromMethodCall(call)// InputImageFromByteArray(dataImage!!, width!!, height!!, rotation!!)

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