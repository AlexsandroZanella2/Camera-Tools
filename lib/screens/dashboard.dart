import 'package:flutter/material.dart';

import '../design/palette.dart';
import '../tabs/tab_camera.dart';

/*
* You can use Palette.kToDark too for green/orange Theme
* */

class Dashboard extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: DefaultTabController(
        length: 4,
        child: Scaffold(
          appBar: AppBar(
            toolbarHeight: 60,
            title: const TabBar(
              tabs: [
                Tab(icon: Icon(Icons.qr_code, size: 24,), text: "Scan", height: 50,),
                Tab(icon: Icon(Icons.edit, size: 24,), text: "Create", height: 50),
                Tab(icon: Icon(Icons.history, size: 24,), text: "History", height: 50),
                Tab(icon: Icon(Icons.settings, size: 24,), text: "Settings", height: 50),
              ],
            ),
            //title: const Text('Tabs Demo'),
          ),
          body: TabBarView(
            children: <Widget>[
              CameraExampleHome(),
              Icon(Icons.directions_transit),
              Icon(Icons.directions_car),
              Icon(Icons.directions_car),
              //QRViewExample(),
              // HistoricoTab(),
              // Icon(Icons.directions_transit),
            ],
          ),
        ),
      ),
    );
  }
}