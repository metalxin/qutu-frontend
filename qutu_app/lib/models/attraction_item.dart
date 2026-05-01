import 'package:flutter/material.dart';

class AttractionItem {
  const AttractionItem({
    required this.name,
    required this.tag,
    required this.rating,
    required this.address,
    required this.time,
    required this.price,
    required this.gradientColors,
  });

  final String name;
  final String tag;
  final double rating;
  final String address;
  final String time;
  final String price;
  final List<Color> gradientColors;
}
