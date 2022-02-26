package app.galego.cameratools

import app.galego.cameratools.interfaces.MethodChannelInterface

import app.galego.cameratools.vision.BarcodeScanner
import app.galego.cameratools.vision.DigitalInkRecognition
import app.galego.cameratools.vision.FaceDetection
import app.galego.cameratools.vision.ImageLabeling
import app.galego.cameratools.vision.ObjectDetection
import app.galego.cameratools.vision.PoseDetection
import app.galego.cameratools.vision.SelfieSegmentation
import app.galego.cameratools.vision.TextRecognition
import app.galego.cameratools.natural.ExtractEntities
import app.galego.cameratools.natural.IdentifyLanguage
import app.galego.cameratools.natural.SmartReplies
import app.galego.cameratools.natural.TranslateText
import app.galego.cameratools.keyguard.KeyGuard
import androidx.annotation.NonNull

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

import java.util.Arrays

import android.content.Context
import android.content.ContextWrapper

public class MethodCallHandler(context: Context) : MethodChannel.MethodCallHandler {

    private val handlers: MutableMap<String, MethodChannelInterface>
    public override fun onMethodCall(@NonNull p0: MethodCall, @NonNull p1: MethodChannel.Result) {
        val handler: MethodChannelInterface? = handlers[p0.method]
        if (handler != null) {
            handler.onMethodCall(p0, p1)
        } else {
            p1.notImplemented()
        }
    }

    init {
        val detectors: List<MethodChannelInterface> = ArrayList<MethodChannelInterface>(
                Arrays.asList(
                        BarcodeScanner(context),
                        KeyGuard(context)
                ))
        handlers = HashMap<String, MethodChannelInterface>()
        for (detector in detectors) {
            for (method in detector.getMethodsKeys()!!) {
                handlers.put(method!!, detector)
            }
        }
    }
}