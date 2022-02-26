import 'dart:developer';

import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:vision_qrcode_scanner/screens/dashboard.dart';

List<CameraDescription> cameras = [];

void main() async {
  try {
    WidgetsFlutterBinding.ensureInitialized();
    cameras = await availableCameras();
  } on CameraException catch (e) {
    log("code: ${e.code} ,description: ${e.description}");
  }
  runApp(MaterialApp(
    home: Dashboard(),
  ));
}