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

import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

import android.content.Context
import android.content.ContextWrapper

public class PoseDetection(var _context: Context): MethodChannelInterface {
    private val START: String = "start#PoseDetection"
    private val CLOSE: String = "close#PoseDetection"

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


}