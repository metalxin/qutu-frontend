import 'package:flutter/material.dart';

class GuideSearchBar extends StatelessWidget {
  const GuideSearchBar({super.key, required this.hintText});

  final String hintText;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 46,
      padding: const EdgeInsets.symmetric(horizontal: 14),
      decoration: BoxDecoration(
        color: const Color(0xFFF0F2F6),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: const Color(0xFFE3E6EE)),
      ),
      child: Row(
        children: [
          const Icon(Icons.search, color: Color(0xFF9AA0AA)),
          const SizedBox(width: 8),
          Text(
            hintText,
            style: const TextStyle(color: Color(0xFF9AA0AA)),
          ),
        ],
      ),
    );
  }
}
