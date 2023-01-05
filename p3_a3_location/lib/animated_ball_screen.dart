import 'package:flutter/material.dart';

class AnimatedBallScreen extends StatefulWidget {
  const AnimatedBallScreen({Key? key}) : super(key: key);

  @override
  State<AnimatedBallScreen> createState() => _AnimatedBallScreenState();
}

class _AnimatedBallScreenState extends State<AnimatedBallScreen>
    with TickerProviderStateMixin {
  late AnimationController _controller;
  final Tween<double> _sizeTween = Tween<double>(begin: 0, end: 100);
  late final Animation<double> animation;

  bool _jump = false;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 1),
    );

    animation = _sizeTween
        .animate(CurvedAnimation(parent: _controller, curve: Curves.ease));
    _controller.addListener(() {
      setState(() {});
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Hero(tag: 'ball screen',child: Text('Bouncing ball')),
      ),
      body: Center(
        child: SafeArea(
          child: Column(
            children: [
              Expanded(
                child: Container(
                  margin: _jump
                      ? EdgeInsets.only(top: animation.value)
                      : EdgeInsets.zero,
                  decoration: const BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.green,
                  ),
                  width: 40.0 + (_jump ? 0 : animation.value),
                  height: 40.0 + (_jump ? 0 : animation.value),
                ),
              ),
              ElevatedButton(
                onPressed: _controller.forward,
                child: const Text('Increase size'),
              ),
              ElevatedButton(
                onPressed: _controller.reverse,
                child: const Text('Reduce size'),
              ),
              ElevatedButton(
                onPressed: () {
                  if (_jump) {
                    _controller.stop();
                    _jump = false;
                    return;
                  }
                  _jump = true;
                  _controller.repeat(reverse: true);
                },
                child: Text(
                    _jump ? 'Stop jumping' : 'Jump'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}