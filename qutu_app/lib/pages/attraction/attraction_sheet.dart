import 'package:flutter/material.dart';
import '../../data/attraction_data.dart';
import '../../models/attraction_item.dart';
import '../../utils/navigation.dart';
import 'attraction_detail_page.dart';

class AttractionSheet extends StatelessWidget {
  const AttractionSheet({super.key, required this.city});

  final String city;

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.of(context).padding.bottom;
    final height = MediaQuery.of(context).size.height * 0.82;
    final attractions = attractionData[city] ?? const <AttractionItem>[];
    return Container(
      height: height,
      padding: EdgeInsets.fromLTRB(20, 12, 20, 16 + bottomPadding),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Expanded(
                child: Text(
                  city,
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                    color: Color(0xFF1D1E22),
                  ),
                ),
              ),
              Ink(
                width: 32,
                height: 32,
                decoration: const BoxDecoration(
                  color: Color(0xFFF1F2F5),
                  shape: BoxShape.circle,
                ),
                child: InkWell(
                  customBorder: const CircleBorder(),
                  onTap: () => Navigator.of(context).pop(),
                  child: const Icon(Icons.close, size: 18),
                ),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            '${attractions.length}个景点',
            style: const TextStyle(
              fontSize: 12,
              color: Color(0xFF8C9099),
            ),
          ),
          const SizedBox(height: 12),
          Expanded(
            child: ListView.separated(
              physics: const BouncingScrollPhysics(),
              itemBuilder: (context, index) => _AttractionCard(
                item: attractions[index],
                onTap: () => pushPage(
                  context,
                  AttractionDetailPage(item: attractions[index]),
                ),
              ),
              separatorBuilder: (_, _) => const SizedBox(height: 12),
              itemCount: attractions.length,
            ),
          ),
        ],
      ),
    );
  }
}

class _AttractionCard extends StatelessWidget {
  const _AttractionCard({required this.item, required this.onTap});

  final AttractionItem item;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(16),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: const Color(0xFFF6F7F9),
          borderRadius: BorderRadius.circular(16),
        ),
        child: Row(
          children: [
            Container(
              width: 72,
              height: 72,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: item.gradientColors,
                ),
              ),
              child: const Icon(Icons.photo_camera_outlined,
                  color: Colors.white70),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        item.name,
                        style: const TextStyle(
                          fontSize: 15,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 8, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFFFEEE8),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          item.tag,
                          style: const TextStyle(
                            fontSize: 10,
                            color: Color(0xFFFF6A5F),
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 6),
                  Text(
                    item.address,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Color(0xFF8C9099),
                    ),
                  ),
                  const SizedBox(height: 6),
                  Row(
                    children: [
                      const Icon(Icons.star,
                          size: 14, color: Color(0xFFFFB74D)),
                      const SizedBox(width: 4),
                      Text(
                        item.rating.toStringAsFixed(1),
                        style: const TextStyle(
                          fontSize: 12,
                          color: Color(0xFF5E6168),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Text(
                        item.price,
                        style: const TextStyle(
                          fontSize: 12,
                          color: Color(0xFF2D5BFF),
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Column(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text(
                  item.time,
                  style: const TextStyle(
                    fontSize: 11,
                    color: Color(0xFF8C9099),
                  ),
                ),
                const SizedBox(height: 4),
                const Text(
                  '距你12.5km',
                  style: TextStyle(
                    fontSize: 11,
                    color: Color(0xFF8C9099),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
