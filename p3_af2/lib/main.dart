import 'dart:math';

import 'package:flutter/material.dart';
import 'package:p3_af1/second_screen.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'cat_facts_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      //home: const MyHomePage(title: 'Flutter Demo Home Page'),
      initialRoute: MyHomePage.routeName,
      routes: {
        MyHomePage.routeName: (context) => const MyHomePage(title: 'Flutter Demo Home Page'),
        SecondScreen.routeName: (context) => const SecondScreen(),
        CatFactsScreen.routeName: (_) => const CatFactsScreen(),
      },
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  static const String routeName = '/';

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;
  Color? _backgroundColor;
  final TextEditingController _tec = TextEditingController();

  void initState() {
    super.initState();
    _tec.addListener(() {

    });
    //_tec.text = '123';
    initInc();
  }
  void dispose() {
    _tec.dispose();
    super.dispose();
  }

  int _inc=1;
  // void changeInc(int inc) { setState(() {_inc = inc;}); }

  Future<void> initInc() async {
    var prefs = await SharedPreferences.getInstance();
    setState(() {_inc = prefs.getInt('increment') ?? 1;});
  }

  Future<void> changeInc(int inc) async {
    setState(() {_inc = inc;});
    var prefs = await SharedPreferences.getInstance();
    await prefs.setInt('increment', _inc);
  }

  Future<void> _incrementCounter() async {
    setState(() {
      _counter += _inc;
      _updateBackgroundColor();
    });
    if(_counter == 5){
      var result = await Navigator.of(context).pushNamed(SecondScreen.routeName, arguments: _counter);
      if(result is int){
        _counter = result;
        setState(() { });
      }
    }
  }

  void _decrementCounter() {
    setState(() {
      _counter -= _inc;
      _updateBackgroundColor();
    });
  }

  void _updateBackgroundColor() {
    _backgroundColor = Color.fromRGBO(Random().nextInt(255),
        Random().nextInt(255), Random().nextInt(255), 1.0);
  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return Scaffold(
      backgroundColor: _backgroundColor ?? Colors.yellow,
      appBar: AppBar(
        // Here we take the value from the MyHomePage object that was created by
        // the App.build method, and use it to set our appbar title.
        title: Text(widget.title),
      ),
      body: Center(
        // Center is a layout widget. It takes a single child and positions it
        // in the middle of the parent.
        child: SafeArea(
          child: SingleChildScrollView(
            child: Column(
              // Column is also a layout widget. It takes a list of children and
              // arranges them vertically. By default, it sizes itself to fit its
              // children horizontally, and tries to be as tall as its parent.
              //
              // Invoke "debug painting" (press "p" in the console, choose the
              // "Toggle Debug Paint" action from the Flutter Inspector in Android
              // Studio, or the "Toggle Debug Paint" command in Visual Studio Code)
              // to see the wireframe for each widget.
              //
              // Column has various properties to control how it sizes itself and
              // how it positions its children. Here we use mainAxisAlignment to
              // center the children vertically; the main axis here is the vertical
              // axis because Columns are vertical (the cross axis would be
              // horizontal).
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                FlutterLogo(
                  size: _counter >= 10 ? 200 : 0,
                ),
                const Text(
                  'You have pushed the button this many times:',
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    const Text('>>>>>'),
                    Text(
                      '$_counter',
                      style: Theme.of(context).textTheme.headline4,
                    ),
                    const Text('<<<<<'),
                  ],
                ),
                // SizedBox(
                //   width: 200,
                //   child: TextField(
                //     decoration: const InputDecoration(
                //       labelText: 'Increment:',
                //       hintText: 'Value to increment',
                //     ),
                //     onChanged: (value) => changeInc(int.tryParse(value) ?? 1),
                //   ),
                // ),
                SizedBox(
                  width: 200,
                  child: TextFormField(
                    decoration: const InputDecoration(
                      labelText: 'Increment:',
                      hintText: 'Value to increment',
                      border: OutlineInputBorder(),
                    ),
                    controller: _tec,
                    //key: Key("$_inc"),
                    //initialValue: "$_inc",
                    onChanged: (value) => changeInc(int.tryParse(value) ?? 1),
                  ),
                ),
                IntrinsicWidth(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      ElevatedButton(
                          onPressed: () => setState(() {
                            _counter = 1000;
                          }),
                          child: const Text('Vamos ao 1000')),
                      Padding(
                        padding: const EdgeInsets.all(30.0),
                        child: ElevatedButton(
                            onPressed: () => setState(() {
                              _counter = 0;
                            }),
                            child: const Text(
                              'Reset',
                              style: TextStyle(color: Colors.yellow),
                            )
                        ),
                      ),
                      Hero(
                        tag: 'AMov1',
                        child: ElevatedButton(
                          onPressed: () => Navigator.push(context,
                            MaterialPageRoute(
                              builder: (context) => const SecondScreen(),
                              settings: RouteSettings(arguments: _counter),
                            ),
                          ),
                          child: const Text('Second Screen (push)',
                              style: TextStyle(color: Colors.yellow)),
                        ),
                      ),
                      ElevatedButton(
                        onPressed: () => Navigator.pushNamed(
                          context,
                          SecondScreen.routeName,
                          arguments: _counter,
                        ),
                        child: const Text('Second Screen (pushNamed)',
                            style: TextStyle(color: Colors.yellow)),
                      ),
                      ElevatedButton(
                        onPressed: () => Navigator.pushNamed(
                          context,
                          CatFactsScreen.routeName,
                        ),
                        child: const Text('Cat Facts Screen',
                            style: TextStyle(color: Colors.yellow)),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
      floatingActionButton: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(25.0, 0, 0, 0),
            child: FloatingActionButton(
              heroTag: "AMovFB1",
              onPressed: _decrementCounter,
              tooltip: 'Decrement',
              child: const Icon(Icons.remove),
            ),
          ),
          FloatingActionButton(
            heroTag: "AMovFB2",
            onPressed: _incrementCounter,
            tooltip: 'Increment',
            child: const Icon(Icons.add),
          ),
        ],
      ), // This trailing comma makes auto-formatting nicer for build methods.
    );
  }
}
