import 'package:flutter/material.dart';
import 'pages/home/home_page.dart';

class QutuApp extends StatelessWidget {
  const QutuApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '趣途云迹',
      theme: ThemeData(
        useMaterial3: false,
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF2D5BFF)),
        scaffoldBackgroundColor: const Color(0xFFF6F7F9),
      ),
      home: const HomePage(),
    );
  }
}
