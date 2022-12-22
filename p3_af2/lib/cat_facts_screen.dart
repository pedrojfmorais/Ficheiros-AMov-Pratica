
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

const String _catFactsUrl = 'https://catfact.ninja/facts';

class CatFact {
  CatFact.fromJson(Map<String, dynamic> json)
      : fact = json['fact'],
        length = json['length'];
  final String fact;
  final int length;
}

class CatFactsScreen extends StatefulWidget {
  const CatFactsScreen({Key? key}) : super(key: key);

  static const String routeName = 'CatFactsScreen';

  @override
  State<CatFactsScreen> createState() => _CatFactsScreenState();
}

class _CatFactsScreenState extends State<CatFactsScreen> {

  List<CatFact>? _catFacts;
  bool _fetchingData = false;

  Future<void> _fetchCatFacts() async {
    try {
      setState(() => _fetchingData = true);
      http.Response response = await http.get(Uri.parse(_catFactsUrl));
      if (response.statusCode == HttpStatus.ok) {
        // debugPrint(response.body);
        final Map<String, dynamic> decodedData = json.decode(response.body);
        setState(() => _catFacts = (decodedData['data'] as List)
            .map((fact) => CatFact.fromJson(fact)).toList());
      }
    } catch (ex) {
      debugPrint('Something went wrong: $ex');
    } finally {
      setState(() => _fetchingData = false);
    }
  }

  Future<String> _fetchAnAsyncString() async {
  // Simula um pedido de 5 segundos
    await Future.delayed(const Duration(seconds: 5));
    return Future.value('Hello world, from an aysnc call!');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          'Cat Facts',
        ),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('Miauuu'),
            const SizedBox(height: 20,),
            // FutureBuilder<String>(
            //   //initialData: 'Teste',
            //   future: _fetchAnAsyncString(),
            //   builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
            //     if (snapshot.hasData) {
            //       return Text(snapshot.data!);
            //     } else if (snapshot.hasError) {
            //       return const Text('Oops, something happened');
            //     } else {
            //       return const CircularProgressIndicator();
            //     }
            //   },
            // ),
            // FutureBuilder<http.Response>(
            //   future: http.get(Uri.parse(_catFactsUrl)),
            //   builder: (BuildContext context, AsyncSnapshot<http.Response> snapshot) {
            //     if (snapshot.hasData) {
            //       return Expanded(
            //           child: SingleChildScrollView(
            //               child: Text(snapshot.data!.body)
            //           )
            //       );
            //     } else if (snapshot.hasError) {
            //       return const Text('Oops, something happened');
            //     } else {
            //       return const CircularProgressIndicator();
            //     }
            //   },
            // )
            ElevatedButton(
              onPressed: _fetchCatFacts,
              child: const Text('Fetch cat facts'),
            ),
            if (_fetchingData) const CircularProgressIndicator(),
            if (!_fetchingData && _catFacts != null && _catFacts!.isNotEmpty)
              Expanded(
                child: ListView.separated(
                  itemCount: _catFacts!.length,
                  separatorBuilder: (_, __) => const Divider(thickness: 2.0),
                  itemBuilder: (BuildContext context, int index) => ListTile(
                    title: Text('Cat fact #$index'),
                    subtitle: Text(_catFacts![index].fact),
                  ),
                ),
              )
          ],
        ),
      ),
    );
  }
}
