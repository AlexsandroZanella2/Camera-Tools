import 'dart:typed_data';

class Functions {
  static dynamic GetImage(int width, int height, int rotation , Uint8List plane0, Uint8List plane1, Uint8List plane2, int prs0, int prs1, int prs2, int pps0, int pps1, int pps2){
    return <String, dynamic>{"width": width, "height": height, "rotation": rotation,
      "plane0": plane0, "plane1": plane1, "plane2": plane2,
      "prs0":prs0, "prs1":prs1, "prs2":prs2,
      "pps0": pps0, "pps1": pps1, "pps2": pps2};
  }
}