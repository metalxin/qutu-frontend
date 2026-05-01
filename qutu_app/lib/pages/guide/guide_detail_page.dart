import 'package:flutter/material.dart';
import '../../models/guide_item.dart';

class GuideDetailPage extends StatefulWidget {
  const GuideDetailPage({super.key, required this.item});

  final GuideItem item;

  @override
  State<GuideDetailPage> createState() => _GuideDetailPageState();
}

class _GuideDetailPageState extends State<GuideDetailPage> {
  int _tabIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF2F3F6),
      body: Stack(
        children: [
          SingleChildScrollView(
            physics: const BouncingScrollPhysics(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Hero(
                  tag: widget.item.id,
                  child: Container(
                    height: 240,
                    decoration: BoxDecoration(
                      gradient: LinearGradient(
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                        colors: widget.item.gradientColors,
                      ),
                    ),
                    child: const Center(
                      child: Icon(
                        Icons.photo_camera_outlined,
                        color: Colors.white70,
                        size: 44,
                      ),
                    ),
                  ),
                ),
                Container(
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
                  ),
                  padding: const EdgeInsets.fromLTRB(20, 20, 20, 24),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.item.title,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        widget.item.summary,
                        style: const TextStyle(
                          fontSize: 13,
                          color: Color(0xFF8C9099),
                        ),
                      ),
                      const SizedBox(height: 16),
                      Row(
                        children: [
                          _ActionPill(
                            label: '行程概述',
                            selected: _tabIndex == 0,
                            onTap: () => setState(() => _tabIndex = 0),
                          ),
                          const SizedBox(width: 10),
                          _ActionPill(
                            label: '行程安排',
                            selected: _tabIndex == 1,
                            onTap: () => setState(() => _tabIndex = 1),
                          ),
                          const SizedBox(width: 10),
                          _ActionPill(
                            label: '美食推荐',
                            selected: _tabIndex == 2,
                            onTap: () => setState(() => _tabIndex = 2),
                          ),
                        ],
                      ),
                      const SizedBox(height: 18),
                      ..._buildDetailSections().map(
                        (section) => Padding(
                          padding: const EdgeInsets.only(bottom: 12),
                          child: _DetailInfoCard(
                            icon: section.icon,
                            title: section.title,
                            content: section.content,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          Positioned(
            top: 12,
            left: 12,
            child: SafeArea(
              child: Ink(
                width: 36,
                height: 36,
                decoration: const BoxDecoration(
                  color: Colors.white,
                  shape: BoxShape.circle,
                ),
                child: InkWell(
                  customBorder: const CircleBorder(),
                  onTap: () => Navigator.of(context).pop(),
                  child: const Icon(Icons.arrow_back_ios_new, size: 18),
                ),
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: SafeArea(
        top: false,
        child: Container(
          padding: const EdgeInsets.fromLTRB(16, 12, 16, 16),
          color: Colors.white,
          child: Row(
            children: [
              _ActionPill(
                label: '收藏',
                selected: false,
                onTap: () {},
                leading: Icons.favorite_border,
              ),
              const SizedBox(width: 12),
              Expanded(
                child: SizedBox(
                  height: 48,
                  child: ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFF2D5BFF),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(24),
                      ),
                    ),
                    child: const Text(
                      '开始计划',
                      style: TextStyle(fontWeight: FontWeight.w600),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  List<_DetailSection> _buildDetailSections() {
    if (_tabIndex == 1) {
      return const [
        _DetailSection(
          icon: Icons.map_outlined,
          title: '行程概览',
          content: '上午：故宫｜下午：景山公园｜傍晚：什刹海',
        ),
        _DetailSection(
          icon: Icons.directions_subway,
          title: '交通',
          content: '地铁1号线＋步行组合，建议提前下载公交地铁支付App。',
        ),
      ];
    }
    if (_tabIndex == 2) {
      return const [
        _DetailSection(
          icon: Icons.map_outlined,
          title: '行程概览',
          content: '上午：长城｜下午：奥林匹克公园｜夜晚：三里屯',
        ),
        _DetailSection(
          icon: Icons.restaurant_outlined,
          title: '餐饮',
          content: '沿途选择本地小馆，体验特色小吃。',
        ),
      ];
    }
    return const [
      _DetailSection(
        icon: Icons.article_outlined,
        title: '行程概述',
        content: '两天一夜的北京经典攻略。去北京一定要提前预约，因为好多景点都需要提前预约！',
      ),
      _DetailSection(
        icon: Icons.directions_subway,
        title: '交通',
        content: '选择自己方便的火车站即可。地铁出行更便捷，基本上景点可达。',
      ),
    ];
  }
}

class _ActionPill extends StatelessWidget {
  const _ActionPill({
    required this.label,
    required this.selected,
    required this.onTap,
    this.leading,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;
  final IconData? leading;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(22),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? const Color(0xFFE8F1FF) : const Color(0xFFF1F2F5),
          borderRadius: BorderRadius.circular(22),
          border: Border.all(
            color: selected ? const Color(0xFF2D5BFF) : Colors.transparent,
          ),
        ),
        child: Row(
          children: [
            if (leading != null) ...[
              Icon(leading, size: 16, color: const Color(0xFF2D5BFF)),
              const SizedBox(width: 6),
            ],
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                color:
                    selected ? const Color(0xFF2D5BFF) : const Color(0xFF5E6168),
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _DetailSection {
  const _DetailSection({
    required this.icon,
    required this.title,
    required this.content,
  });

  final IconData icon;
  final String title;
  final String content;
}

class _DetailInfoCard extends StatelessWidget {
  const _DetailInfoCard({
    required this.icon,
    required this.title,
    required this.content,
  });

  final IconData icon;
  final String title;
  final String content;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFF6F7F9),
        borderRadius: BorderRadius.circular(18),
      ),
      child: Row(
        children: [
          Container(
            width: 36,
            height: 36,
            decoration: const BoxDecoration(
              color: Color(0xFFE8ECF7),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: const Color(0xFF2D5BFF), size: 18),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 6),
                Text(
                  content,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                    height: 1.5,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
