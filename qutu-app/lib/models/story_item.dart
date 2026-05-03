import 'package:flutter/material.dart';

class StoryItem {
  const StoryItem({
    required this.id,
    required this.title,
    required this.location,
    required this.date,
    required this.weekday,
    required this.content,
    required this.gradientColors,
  });

  final String id;
  final String title;
  final String location;
  final String date;
  final String weekday;
  final String content;
  final List<Color> gradientColors;
}
