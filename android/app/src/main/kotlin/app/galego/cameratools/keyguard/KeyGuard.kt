package app.galego.cameratools.keyguard

import androidx.annotation.NonNull
import android.app.KeyguardManager
import android.os.PowerManager
import android.content.Context
import android.os.Build

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

import app.galego.cameratools.interfaces.MethodChannelInterface

public class KeyGuard(val registrarContext: Context? = null): MethodChannelInterface{

    private val isLockedScreen: String = "isLockedScreen";

    private var bindingContext : Context? = null

    override fun getMethodsKeys(): List<String> {
        return listOf(isLockedScreen)
    }

    override fun onMethodCall(@NonNull call: MethodCall?, @NonNull result: MethodChannel.Result?){
        val method: String? = call?.method
        if (method == isLockedScreen) {
            val context = bindingContext ?: registrarContext
            if(context == null){
                result?.error("NullContext", "Cannot access system service as context is null", null)
                return
            }


            //?:

            val keyguardManager: KeyguardManager = context?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val inKeyguardRestrictedInputMode: Boolean = keyguardManager.inKeyguardRestrictedInputMode()

            val isLocked = if (inKeyguardRestrictedInputMode) {
                true
            } else {
                val powerManager: PowerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    !powerManager.isInteractive
                } else {
                    !powerManager.isScreenOn
                }
            }
            result?.success(isLocked)

        } else {
            result?.error("Method not implemented", "Method $method dont exists", null)
        }
    }

}