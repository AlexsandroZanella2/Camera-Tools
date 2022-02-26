import 'dart:typed_data';

import 'package:camera/camera.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:vision_qrcode_scanner/main.dart';
import 'package:vision_qrcode_scanner/mlkit/vision.dart';

import '../mlkit/functions.dart';

class CameraExampleHome extends StatefulWidget {
  @override
  _CameraExampleHomeState createState() {
    return _CameraExampleHomeState();
  }
}

void logError(String code, String? message) {
  if(!kDebugMode) return;

  if (message != null) {
    print('Error: $code\nError Message: $message');
  } else {
    print('Error: $code');
  }
}

class _CameraExampleHomeState extends State<CameraExampleHome>
    with WidgetsBindingObserver, TickerProviderStateMixin {

  CameraController? controller;
  XFile? imageFile;
  double _minAvailableZoom = 1.0;
  double _maxAvailableZoom = 1.0;
  double _currentScale = 1.0;
  double _baseScale = 1.0;
  bool _scanQrMode = true;
  bool _isDetecting = false;
  String _lastQrCodeScanned = "";
  //Venda? _vendaAtual;
  int _pointers = 0;

  @override
  void initState() {
    super.initState();
    _ambiguate(WidgetsBinding.instance)?.addObserver(this);

    try {
      if (cameras.length > 0) onNewCameraSelected(cameras.first);
    } catch (_) {}
  }

  @override
  void dispose() {
    _ambiguate(WidgetsBinding.instance)?.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    final CameraController? cameraController = controller;

    if (cameraController == null || !cameraController.value.isInitialized) {
      return;
    }

    if (state == AppLifecycleState.inactive) {
      cameraController.dispose();
    } else if (state == AppLifecycleState.resumed) {
      onNewCameraSelected(cameraController.description);
    }
  }

  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    // dados = Provider.of<VendaAtualBloc>(context);
    return Scaffold(
      key: _scaffoldKey,
      body: Stack(
        children: <Widget>[
          Center(
            child: _cameraPreviewWidget(),
          ),
          Center(
            child: _scanQrMode
                ? Container(
              height: double.maxFinite,
              width: double.maxFinite,
              color: Colors.black26,
              child: _getOverlay(context),
            )
                : null,
          ),
          Positioned(
            top: 20,
            left: 40,
            right: 40,
            child: Center(
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                mainAxisSize: MainAxisSize.max,
                children: [
                  InkWell(
                    child: Container(
                      width: 90,
                      child: Column(
                        children: [
                          Icon(
                            Icons.light_mode_rounded,
                            color:
                            controller?.value.flashMode == FlashMode.torch
                                ? Colors.orange
                                : Colors.white,
                          ),
                          Text(
                            "Light",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              color:
                              controller?.value.flashMode == FlashMode.torch
                                  ? Colors.orange
                                  : Colors.white,
                            ),
                          )
                        ],
                      ),
                    ),
                    onTap: () async {
                      setFlashMode(
                          controller?.value?.flashMode == FlashMode.torch
                              ? FlashMode.off
                              : FlashMode.torch);
                    },
                  ),
                  InkWell(
                    child: Container(
                      width: 90,
                      child: Column(
                        children: [
                          Icon(
                            _scanQrMode
                                ? Icons.qr_code_scanner
                                : Icons.document_scanner,
                            color: Colors.white,
                          ),
                          Text(
                            _scanQrMode ? "QRCode" : "Documento",
                            textAlign: TextAlign.center,
                            style: TextStyle(color: Colors.white),
                          ),
                        ],
                      ),
                    ),
                    onTap: () async {
                      try {
                        _scanQrMode = !_scanQrMode;
                        if (_scanQrMode) {
                          await controller?.startImageStream(onImageStream);
                        } else {
                          await _stopStream();
                        }
                        if (mounted) setState(() {});
                      } catch (_) {
                        if(kDebugMode) print(_);
                      }
                    },
                  ),
                  InkWell(
                    child: Container(
                      width: 90,
                      child: Column(
                        children: [
                          Icon(
                            controller?.description?.name == cameras[1].name
                                ? Icons.camera_front
                                : Icons.camera_alt,
                            color:
                            controller?.description?.name == cameras[1].name
                                ? Colors.orange
                                : Colors.white,
                          ),
                          Text(
                            controller?.description?.name == cameras[1].name
                                ? "Selfie"
                                : "Camera",
                            textAlign: TextAlign.center,
                            style: TextStyle(
                              color: controller?.description?.name ==
                                  cameras[1].name
                                  ? Colors.orange
                                  : Colors.white,
                            ),
                          )
                        ],
                      ),
                    ),
                    onTap: () {
                      if (cameras.length > 1)
                        onNewCameraSelected(
                            controller?.description?.name == cameras.first.name
                                ? cameras[1]
                                : cameras.first);
                    },
                  ),
                ],
              ),
            ),
          ),
          Positioned(
            bottom: 20,
            right: 0,
            left: 0,
            child: Center(
              child: _scanQrMode
                  ? null
                  : InkWell(
                child: Icon(
                  Icons.circle_outlined,
                  size: 60,
                  color: Colors.white,
                ),
                onTap:
                controller != null && controller!.value.isInitialized
                    ? onTakePictureButtonPressed
                    : null,
              ),
            ),
          ),
        ],
      ),
    );



  }

  Future<void> _stopStream()async{
    try{
      await controller?.stopImageStream();
    }catch(_){}
  }

  /// Display the preview from the camera (or a message if the preview is not available).
  Widget _cameraPreviewWidget() {
    final CameraController? cameraController = controller;

    if (cameraController == null ||
        (cameraController.value.isInitialized != true)) {
      return _LoadingWidget();
    }

    final size = MediaQuery.of(context).size;
    var scale = size.aspectRatio * (controller!.value.aspectRatio);
    if (scale < 1) scale = 1 / scale;
    return Transform.scale(
      scale: scale,
      child: Listener(
        onPointerDown: (_) => _pointers++,
        onPointerUp: (_) => _pointers--,
        child: CameraPreview(
          controller!,
          child: LayoutBuilder(
              builder: (BuildContext context, BoxConstraints constraints) {
                return GestureDetector(
                  behavior: HitTestBehavior.opaque,
                  onScaleStart: _handleScaleStart,
                  onScaleUpdate: _handleScaleUpdate,
                  onTapDown: (details) => onViewFinderTap(details, constraints),
                );
              }),
        ),
      ),
    );
  }

  Widget _LoadingWidget() {
    return Container(
      padding: EdgeInsets.all(0.0),
      height: double.maxFinite,
      width: double.maxFinite,
      color: Colors.black,
    );
  }

  void _handleScaleStart(ScaleStartDetails details) {
    _baseScale = _currentScale;
  }

  Future<void> _handleScaleUpdate(ScaleUpdateDetails details) async {
    // When there are not exactly two fingers on screen don't scale
    if (controller == null || _pointers != 2) {
      return;
    }

    // _currentScale = (_baseScale * details.scale)
    //     .clamp(_minAvailableZoom, _maxAvailableZoom);
    //
    // await controller!.setZoomLevel(_currentScale);
  }

  String timestamp() => DateTime.now().millisecondsSinceEpoch.toString();

  void showInSnackBar(String message) {
    // ignore: deprecated_member_use
    _scaffoldKey.currentState?.showSnackBar(SnackBar(content: Text(message)));
  }

  void onViewFinderTap(TapDownDetails details, BoxConstraints constraints) {
    if (controller == null) {
      return;
    }

    final CameraController cameraController = controller!;

    final offset = Offset(
      details.localPosition.dx / constraints.maxWidth,
      details.localPosition.dy / constraints.maxHeight,
    );
    cameraController.setExposurePoint(offset);
    cameraController.setFocusPoint(offset);
  }

  void onNewCameraSelected(CameraDescription cameraDescription) async {
    if (await Vision.IsLockedScreen()) return;

    if (!mounted) return;
    final CameraController cameraController = CameraController(
      cameraDescription,
      _scanQrMode ? ResolutionPreset.medium : ResolutionPreset.high,
      enableAudio: false,
      imageFormatGroup: ImageFormatGroup.yuv420,
    );

    // If the controller is updated then update the UI.
    cameraController.addListener(() {
      if (mounted) setState(() {});
      if (cameraController.value.hasError) {
        showInSnackBar(
            'Camera error ${cameraController.value.errorDescription}');
      }
    });

    try {
      if (controller != null) await controller!.dispose();
      controller = cameraController;

      await cameraController.initialize();

      await Future.wait([
        cameraController
            .getMaxZoomLevel()
            .then((value) => _maxAvailableZoom = value),
        cameraController
            .getMinZoomLevel()
            .then((value) => _minAvailableZoom = value),
      ]);

      if (mounted) {
        setState(() {
          if (_scanQrMode)
            Future.delayed(Duration(seconds: 2))
                .then((value) => controller?.startImageStream(onImageStream));
        });
      }
    } on CameraException catch (e) {
      _showCameraException(e);
    }
  }

  void onImageStream(CameraImage image) async {
    if (!mounted) return;
    if (!_scanQrMode) {
      await _stopStream();
    }
    if (_isDetecting) return;
    _isDetecting = true;
    try {
      var imageObject = await Functions.GetImage(
          image.width,
          image.height,
          controller?.description?.sensorOrientation ?? 0,
          image.planes[0].bytes,
          image.planes[0].bytes,
          image.planes[0].bytes,
          image.planes[0].bytesPerRow,
          image.planes[0].bytesPerRow,
          image.planes[0].bytesPerRow,
          image.planes[0].bytesPerPixel ?? 0,
          image.planes[0].bytesPerPixel ?? 0,
          image.planes[0].bytesPerPixel ?? 0);
      var scan = await Vision.StartScanBarcode(imageObject);
      if (scan.isNotEmpty) {
        // if (_lastQrCodeScanned != scan) {
          if(kDebugMode) showInSnackBar(scan);
          _lastQrCodeScanned = scan;

          // onNewCameraSelected(controller!.description);
          // await _stopStream();
        // }
      }
    } catch (erro) {
      if(kDebugMode) print(erro);
    } finally {
      _isDetecting = false;
    }
  }

  Future<Uint8List> getImageArray(CameraImage image) async {
    final WriteBuffer allBytes = WriteBuffer();
    for (Plane plane in image.planes) {
      allBytes.putUint8List(plane.bytes);
    }
    return allBytes.done().buffer.asUint8List();
  }

  void onTakePictureButtonPressed() {
    takePicture().then((XFile? file) {
      if (mounted) {
        setState(() {
          imageFile = file;
        });
        if (file != null) {
          teste();
        }
      }
    });
  }

  void teste() async {
    try {
      var previsao = await Vision.getTextFromImage(imageFile?.path ?? "");

      controller?.pausePreview();
      await showDialog(
          context: context, builder: (_) => Text(previsao)
      );
    } catch (error) {
      if(kDebugMode) print(error);
    } finally {
      controller?.resumePreview();
      return;
    }
  }

  Future<void> setFlashMode(FlashMode mode) async {
    if (controller == null) {
      return;
    }

    try {
      await controller!.setFlashMode(mode);
    } on CameraException catch (e) {
      _showCameraException(e);
      rethrow;
    }
  }

  Future<void> setExposureMode(ExposureMode mode) async {
    if (controller == null) {
      return;
    }

    try {
      await controller!.setExposureMode(mode);
    } on CameraException catch (e) {
      _showCameraException(e);
      rethrow;
    }
  }

  Future<XFile?> takePicture() async {
    final CameraController? cameraController = controller;
    if (cameraController == null || !cameraController.value.isInitialized) {
      showInSnackBar('Error: select a camera first.');
      return null;
    }

    if (cameraController.value.isTakingPicture) {
      // A capture is already pending, do nothing.
      return null;
    }

    try {
      XFile file = await cameraController.takePicture();
      return file;
    } on CameraException catch (e) {
      _showCameraException(e);
      return null;
    }
  }

  void _showCameraException(CameraException e) {
    logError(e.code, e.description);
    if(kDebugMode) showInSnackBar('Error: ${e.code}\n${e.description}');
  }
}

Widget _getOverlay(BuildContext context) {
  return CustomPaint(
      size: MediaQuery.of(context).size, painter: OverlayWithHolePainter());
}

class OverlayWithHolePainter extends CustomPainter {
  OverlayWithHolePainter({this.borderThinkness = 3, this.sizeQr = 0.7});
  var borderThinkness;
  var sizeQr;

  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()..color = Colors.black54;
    final paintLines = Paint()..color = Color(0xffFFAC38);
    canvas.drawPath(
        Path.combine(
          PathOperation.difference,
          Path()..addRect(Rect.fromLTWH(0, 0, size.width, size.height)),
          Path()
            ..addRect(Rect.fromCenter(
                center: Offset(size.width / 2, size.height / 2),
                height: size.width * sizeQr,
                width: size.width * sizeQr))
            ..close(),
        ),
        paint);

    final posStartWidth = (size.width * ((1 - sizeQr) / 2)) - borderThinkness;
    final posStartHeight =
        (size.height / 2 - size.width * (sizeQr / 2)) - borderThinkness;
    final posEndWidth =
        (posStartWidth + (size.width * sizeQr)) + borderThinkness;
    final posEndHeight =
        (posStartHeight + (size.width * sizeQr)) + borderThinkness;

    final refBorder = size.width * ((1 - sizeQr) / 2);

    final x1 = posStartWidth;
    final x2 = x1 + borderThinkness;
    final x3 = x1 + refBorder;
    final x4 = posEndWidth - refBorder;
    final x5 = posEndWidth;
    final x6 = posEndWidth + borderThinkness;
    final y1 = posStartHeight;
    final y2 = posStartHeight + borderThinkness;
    final y3 = posStartHeight + refBorder;
    final y4 = posEndHeight - refBorder;
    final y5 = posEndHeight;
    final y6 = posEndHeight + borderThinkness;

    final SupEsq = [
      Offset(x1, y1),
      Offset(x3, y1),
      Offset(x3, y2),
      Offset(x2, y2),
      Offset(x2, y3),
      Offset(x1, y3),
    ];
    final SupDir = [
      Offset(x6, y1),
      Offset(x4, y1),
      Offset(x4, y2),
      Offset(x5, y2),
      Offset(x5, y3),
      Offset(x6, y3),
    ];
    final InfDir = [
      Offset(x6, y6),
      Offset(x4, y6),
      Offset(x4, y5),
      Offset(x5, y5),
      Offset(x5, y4),
      Offset(x6, y4),
    ];
    final InfEsq = [
      Offset(x1, y6),
      Offset(x1, y4),
      Offset(x2, y4),
      Offset(x2, y5),
      Offset(x3, y5),
      Offset(x3, y6),
    ];
    canvas.drawPath(Path()..addPolygon(SupEsq, false), paintLines);
    canvas.drawPath(Path()..addPolygon(SupDir, false), paintLines);
    canvas.drawPath(Path()..addPolygon(InfDir, false), paintLines);
    canvas.drawPath(Path()..addPolygon(InfEsq, false), paintLines);
  }

  @override
  bool shouldRepaint(CustomPainter oldDelegate) {
    return false;
  }
}

T? _ambiguate<T>(T? value) => value;