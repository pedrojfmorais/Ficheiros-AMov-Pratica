import 'dart:math';

import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:location/location.dart';

import 'package:flutter_localizations/flutter_localizations.dart';
import 'animated_ball_screen.dart';
import 'generated/l10n.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      localizationsDelegates: const [
        S.delegate,
        GlobalMaterialLocalizations.delegate,
        GlobalWidgetsLocalizations.delegate,
        GlobalCupertinoLocalizations.delegate,
      ],
      supportedLocales: S.delegate.supportedLocales,
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  double latitude = 0.0;
  double longitude = 0.0;

  Location location = Location();

  bool _serviceEnabled = false;
  PermissionStatus _permissionGranted = PermissionStatus.denied;
  late LocationData _locationData;

  bool _automaticUpdates = false;

  @override
  void initState() {
    super.initState();

    initLication();
  }

  Future<void> initLication() async {
    _serviceEnabled = await location.serviceEnabled();
    if (!_serviceEnabled) {
      _serviceEnabled = await location.requestService();
      if (!_serviceEnabled) {
        return;
      }
    }
    setState(() {});
    _permissionGranted = await location.hasPermission();
    if (_permissionGranted == PermissionStatus.denied) {
      _permissionGranted = await location.requestPermission();
      if (_permissionGranted != PermissionStatus.granted) {
        return;
      }
    }
    setState(() {});

    location.onLocationChanged.listen((LocationData currentLocation) {
      if (!_automaticUpdates) {
        return;
      }

      setState(() {
        latitude = currentLocation.latitude ?? 0.0;
        longitude = currentLocation.longitude ?? 0.0;
      });
    });
  }

  Future<void> _getCoordinates() async {
    if (!_serviceEnabled || _permissionGranted != PermissionStatus.granted) {
      return;
    }

    _locationData = await location.getLocation();

    setState(() {
      // latitude = Random().nextDouble()*180-90;
      // longitude = Random().nextDouble()*360-180;

      latitude = _locationData.latitude ?? 0.0;
      longitude = _locationData.longitude ?? 0.0;
    });
  }

  void _incrementCounter() {
    setState(() {
      _counter++;
    });
  }

  Widget _locationWidget(BuildContext context) {
    if (!_serviceEnabled) {
      return Text(S.of(context).locationServiceNotAvailable);
    }

    if (_permissionGranted != PermissionStatus.granted) {
      return Text(S.of(context).locationPermissionsNotGranted);
    }

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text(S.of(context).latitudeLatitude(latitude)),
        Text(S.of(context).longitudeLongitude(longitude)),
        Switch(
            value: _automaticUpdates,
            onChanged: (value) => setState(() {
                  _automaticUpdates = value;
                })),
        ElevatedButton(
          onPressed: _getCoordinates,
          child: Text(S.of(context).refresh),
        ),
        const SizedBox(
          height: 30,
        ),
        StreamBuilder(
          stream: location.onLocationChanged,
          builder: (context, snapshot) {
            if (!snapshot.hasData) {
              return const CircularProgressIndicator();
            }
            return Text('Latitude: ${snapshot.data!.latitude}\n'
                'Longitude: ${snapshot.data!.longitude}');
          },
        )
      ],
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Text(
              S.of(context).youHavePushedTheButtonThisManyTimes,
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headline4,
            ),
            const SizedBox(
              height: 30,
            ),
            _locationWidget(context),
            const SizedBox(
              height: 30,
            ),
            Text('Locale: ${Intl.getCurrentLocale()}'),
            Switch(
                value: Intl.getCurrentLocale() == 'en',
                onChanged: (value) {
                  S.load(value ? const Locale('en') : const Locale('pt'));
                  setState(() => {});
                }),
            Hero(
              tag: 'ball screen',
              child: ElevatedButton(
                  onPressed: () => Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const AnimatedBallScreen(),
                      )),
                  child: const Text('Ball screen')),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _incrementCounter,
        tooltip: S.of(context).increment,
        child: const Icon(Icons.add),
      ),
    );
  }
}
