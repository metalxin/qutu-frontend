import 'package:flutter/material.dart';

class GuideItem {
  const GuideItem({
    required this.id,
    required this.title,
    required this.subtitle,
    required this.summary,
    required this.gradientColors,
  });

  final String id;
  final String title;
  final String subtitle;
  final String summary;
  final List<Color> gradientColors;
}
