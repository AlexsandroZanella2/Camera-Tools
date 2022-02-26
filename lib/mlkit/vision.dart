import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Vision {
  Vision._();
  static const MethodChannel channel = MethodChannel('mlkit#kotlin');

  // Creates an instance of [GoogleMlKit] by calling the private constructor
  static final Vision instance = Vision._();

  static Future<String> StartScanBarcode(dynamic image) async{
    return await channel.invokeMethod('start#BarcodeScanner', image);
  }

  static void StopScanBarcode() async{
    await channel.invokeMethod('close#BarcodeScanner');
  }

  static Future<String> getTextFromImage(dynamic image)async{
    return await channel.invokeMethod('start#TextRecognition', image);
  }

  static Future<bool> IsLockedScreen() async{
    return await channel.invokeMethod('isLockedScreen');
  }


}