package app.galego.cameratools.interfaces

import androidx.annotation.NonNull

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


interface MethodChannelInterface {
    fun getMethodsKeys(): List<String?>?

    fun onMethodCall(@NonNull call: MethodCall?, @NonNull result: MethodChannel.Result?)
}