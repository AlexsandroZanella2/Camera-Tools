package app.galego.cameratools

import io.flutter.embedding.android.FlutterActivity


import app.galego.cameratools.interfaces.MethodChannelInterface

import android.os.StrictMode.ThreadPolicy.Builder
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode
import android.os.Bundle

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

import androidx.annotation.NonNull

import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.engine.FlutterEngine


import java.util.Arrays

class MainActivity: FlutterActivity() {
    private val CHANNEL = "mlkit#kotlin"

    private var channel: MethodChannel? = null

    override protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy: ThreadPolicy = Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL)
        channel!!.setMethodCallHandler(MethodCallHandler(applicationContext))
    }
}