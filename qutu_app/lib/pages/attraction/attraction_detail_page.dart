import 'package:flutter/material.dart';
import '../../models/attraction_item.dart';

class AttractionDetailPage extends StatelessWidget {
  const AttractionDetailPage({super.key, required this.item});

  final AttractionItem item;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF6F7F9),
      body: Stack(
        children: [
          ListView(
            padding: EdgeInsets.zero,
            physics: const BouncingScrollPhysics(),
            children: [
              Container(
                height: 260,
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                    colors: item.gradientColors,
                  ),
                ),
              ),
              Container(
                padding: const EdgeInsets.fromLTRB(20, 16, 20, 20),
                decoration: const BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Text(
                          item.name,
                          style: const TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                        const SizedBox(width: 6),
                        Text(
                          '(${item.tag})',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFF8C9099),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 8),
                    Row(
                      children: [
                        const Icon(Icons.star,
                            size: 16, color: Color(0xFFFFB74D)),
                        const SizedBox(width: 4),
                        Text(
                          '${item.rating.toStringAsFixed(1)}/5',
                          style: const TextStyle(
                            fontSize: 12,
                            color: Color(0xFF5E6168),
                          ),
                        ),
                        const SizedBox(width: 8),
                        const Text(
                          '（来自携程）',
                          style: TextStyle(
                            fontSize: 12,
                            color: Color(0xFF8C9099),
                          ),
                        ),
                        const SizedBox(width: 12),
                        const Icon(Icons.near_me_outlined,
                            size: 16, color: Color(0xFF2D5BFF)),
                        const SizedBox(width: 4),
                        const Text(
                          '12.5km',
                          style: TextStyle(
                            fontSize: 12,
                            color: Color(0xFF1D1E22),
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 12),
                    const Text(
                      '颐和园，中国清朝时期皇家园林，前身为清漪园，坐落在北京西郊，距城区十五公里，占地约二百九十公顷，与圆明园毗邻。它是以昆明湖、万寿山为基址，以杭州西湖为蓝本，汲取江南园林的设计手法而建成的一座大型山水园林。',
                      style: TextStyle(
                        fontSize: 13,
                        color: Color(0xFF5E6168),
                        height: 1.5,
                      ),
                    ),
                    const SizedBox(height: 4),
                    const Text(
                      '阅读全文...',
                      style: TextStyle(
                        fontSize: 13,
                        color: Color(0xFFFF6A5F),
                      ),
                    ),
                    const SizedBox(height: 16),
                    Row(
                      children: [
                        Expanded(
                          child: _DetailInfoBox(
                            icon: Icons.location_on_outlined,
                            title: '北京市海淀区',
                            subtitle: item.address,
                          ),
                        ),
                        const SizedBox(width: 12),
                        const Expanded(
                          child: _DetailInfoBox(
                            icon: Icons.confirmation_number_outlined,
                            title: '旺季¥30 / 淡季¥20',
                            subtitle: '联票¥60',
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 18),
                    const _DetailSectionHeader(title: '图库', actionLabel: '查看全部'),
                    const SizedBox(height: 10),
                    SizedBox(
                      height: 90,
                      child: ListView(
                        physics: const BouncingScrollPhysics(),
                        scrollDirection: Axis.horizontal,
                        children: [
                          _MiniImageCard(colors: item.gradientColors),
                          const SizedBox(width: 10),
                          const _MiniImageCard(colors: [
                            Color(0xFFB45A34),
                            Color(0xFFF1C38C)
                          ]),
                          const SizedBox(width: 10),
                          const _MiniImageCard(colors: [
                            Color(0xFF2E6D7A),
                            Color(0xFFA9D9E9)
                          ]),
                        ],
                      ),
                    ),
                    const SizedBox(height: 18),
                    const _DetailSectionHeader(title: '相关攻略', actionLabel: '查看更多'),
                    const SizedBox(height: 10),
                    const _GuidePreviewCard(
                      title: '北京四天三夜深度游',
                      subtitle: '带你深度探索北京的每一个角落，感受古都魅力',
                      days: '4天3晚',
                      likes: '1256',
                      views: '9800',
                      colors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
                    ),
                    const SizedBox(height: 12),
                    const _GuidePreviewCard(
                      title: '北京周末亲子游',
                      subtitle: '带娃玩转北京，科技馆、动物园、游乐场',
                      days: '2天1晚',
                      likes: '890',
                      views: '6500',
                      colors: [Color(0xFFB45A34), Color(0xFFF1C38C)],
                    ),
                    const SizedBox(height: 12),
                    const _GuidePreviewCard(
                      title: '北京美食探店攻略',
                      subtitle: '从老字号到网红店，一篇攻略吃遍北京',
                      days: '3天2晚',
                      likes: '2340',
                      views: '18000',
                      colors: [Color(0xFF5B4E99), Color(0xFFB6A7F5)],
                    ),
                    const SizedBox(height: 18),
                    const Text(
                      '旅行贴士',
                      style: TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(height: 10),
                    const _TipsCard(
                      icon: Icons.access_time,
                      title: '开放时间',
                      subtitle: '旺季6:30–18:00，淡季7:00–17:00',
                    ),
                    const SizedBox(height: 10),
                    const _TipsCard(
                      icon: Icons.directions_subway_outlined,
                      title: '交通方式',
                      subtitle: '地铁4号线北宫门站D口出',
                    ),
                    const SizedBox(height: 10),
                    const _TipsCard(
                      icon: Icons.map_outlined,
                      title: '建议游玩',
                      subtitle: '3–4小时',
                    ),
                    const SizedBox(height: 10),
                    const _TipsCard(
                      icon: Icons.calendar_today_outlined,
                      title: '最佳季节',
                      subtitle: '春秋两季，天气宜人',
                    ),
                    const SizedBox(height: 90),
                  ],
                ),
              ),
            ],
          ),
          SafeArea(
            child: Padding(
              padding: const EdgeInsets.fromLTRB(12, 8, 12, 0),
              child: Row(
                children: [
                  _CircleOverlayButton(
                    icon: Icons.arrow_back_ios_new,
                    onTap: () => Navigator.of(context).pop(),
                  ),
                  const Spacer(),
                  _CircleOverlayButton(
                    icon: Icons.favorite_border,
                    onTap: () {},
                  ),
                  const SizedBox(width: 10),
                  _CircleOverlayButton(
                    icon: Icons.share_outlined,
                    onTap: () {},
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
      bottomNavigationBar: SafeArea(
        top: false,
        child: Container(
          padding: const EdgeInsets.fromLTRB(20, 12, 20, 16),
          color: Colors.white,
          child: SizedBox(
            height: 52,
            child: ElevatedButton(
              onPressed: () {},
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFFF6A3D),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(26),
                ),
              ),
              child: const Text(
                '开始旅程',
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class _DetailInfoBox extends StatelessWidget {
  const _DetailInfoBox({
    required this.icon,
    required this.title,
    required this.subtitle,
  });

  final IconData icon;
  final String title;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF6F7F9),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              Icon(icon, size: 16, color: const Color(0xFF2D5BFF)),
              const SizedBox(width: 6),
              Expanded(
                child: Text(
                  title,
                  style: const TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 6),
          Text(
            subtitle,
            style: const TextStyle(
              fontSize: 11,
              color: Color(0xFF8C9099),
            ),
          ),
        ],
      ),
    );
  }
}

class _DetailSectionHeader extends StatelessWidget {
  const _DetailSectionHeader({
    required this.title,
    required this.actionLabel,
  });

  final String title;
  final String actionLabel;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w600,
          ),
        ),
        const Spacer(),
        Text(
          actionLabel,
          style: const TextStyle(
            fontSize: 12,
            color: Color(0xFFFF6A5F),
          ),
        ),
      ],
    );
  }
}

class _MiniImageCard extends StatelessWidget {
  const _MiniImageCard({required this.colors});

  final List<Color> colors;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 120,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(14),
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
          colors: colors,
        ),
      ),
      child: const Center(
        child: Icon(Icons.photo_camera_outlined, color: Colors.white70),
      ),
    );
  }
}

class _GuidePreviewCard extends StatelessWidget {
  const _GuidePreviewCard({
    required this.title,
    required this.subtitle,
    required this.days,
    required this.likes,
    required this.views,
    required this.colors,
  });

  final String title;
  final String subtitle;
  final String days;
  final String likes;
  final String views;
  final List<Color> colors;

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: const Color(0xFFF6F7F9),
        borderRadius: BorderRadius.circular(18),
      ),
      padding: const EdgeInsets.all(14),
      child: Row(
        children: [
          Container(
            width: 68,
            height: 68,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(14),
              gradient: LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: colors,
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
                Text(
                  title,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  subtitle,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
                const SizedBox(height: 6),
                Row(
                  children: [
                    _StatChip(icon: Icons.access_time, label: days),
                    const SizedBox(width: 8),
                    _StatChip(icon: Icons.favorite_border, label: likes),
                    const SizedBox(width: 8),
                    _StatChip(icon: Icons.visibility_outlined, label: views),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

class _StatChip extends StatelessWidget {
  const _StatChip({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        children: [
          Icon(icon, size: 12, color: const Color(0xFF8C9099)),
          const SizedBox(width: 4),
          Text(
            label,
            style: const TextStyle(fontSize: 11, color: Color(0xFF5E6168)),
          ),
        ],
      ),
    );
  }
}

class _TipsCard extends StatelessWidget {
  const _TipsCard({
    required this.icon,
    required this.title,
    required this.subtitle,
  });

  final IconData icon;
  final String title;
  final String subtitle;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: const Color(0xFFF6F7F9),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          Container(
            width: 34,
            height: 34,
            decoration: const BoxDecoration(
              color: Color(0xFFE8ECF7),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: const Color(0xFF2D5BFF), size: 18),
          ),
          const SizedBox(width: 12),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(
                  fontSize: 13,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                subtitle,
                style: const TextStyle(
                  fontSize: 12,
                  color: Color(0xFF8C9099),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _CircleOverlayButton extends StatelessWidget {
  const _CircleOverlayButton({required this.icon, required this.onTap});

  final IconData icon;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 36,
      height: 36,
      decoration: const BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onTap,
        child: Icon(icon, size: 18, color: const Color(0xFF1D1E22)),
      ),
    );
  }
}
