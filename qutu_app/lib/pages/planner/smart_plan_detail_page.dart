import 'package:flutter/material.dart';

class SmartPlanDetailPage extends StatefulWidget {
  const SmartPlanDetailPage({super.key});

  @override
  State<SmartPlanDetailPage> createState() => _SmartPlanDetailPageState();
}

class _SmartPlanDetailPageState extends State<SmartPlanDetailPage>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  late final Animation<double> _fadeAnimation;
  late final Animation<Offset> _slideAnimation;
  int _dayIndex = 0;

  final List<_DayPlan> _plans = [
    _DayPlan(
      title: 'Day 1 第一天',
      date: '2月24日',
      totalHours: '8小时',
      distance: '21km',
      items: [
        _TimelineItem('09:00', '12:00', '安徽省博物馆', '博物馆', '3小时', 4.7),
        _TimelineItem('13:30', '16:30', '三河古镇', '古镇', '3小时', 4.6),
      ],
    ),
    _DayPlan(
      title: 'Day 2 第二天',
      date: '2月25日',
      totalHours: '7小时',
      distance: '18km',
      items: [
        _TimelineItem('09:30', '11:30', '逍遥津', '公园', '2小时', 4.5),
        _TimelineItem('13:00', '16:00', '包公园', '文化', '3小时', 4.6),
      ],
    ),
    _DayPlan(
      title: 'Day 3 第三天',
      date: '2月26日',
      totalHours: '6小时',
      distance: '15km',
      items: [
        _TimelineItem('10:00', '12:00', '淮河路步行街', '街区', '2小时', 4.4),
        _TimelineItem('13:30', '16:00', '合柴1972', '艺术', '2.5小时', 4.5),
      ],
    ),
  ];

  @override
  void initState() {
    super.initState();
    _controller =
        AnimationController(vsync: this, duration: const Duration(milliseconds: 650));
    _fadeAnimation = CurvedAnimation(parent: _controller, curve: Curves.easeOut);
    _slideAnimation = Tween<Offset>(
      begin: const Offset(0, 0.08),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOutCubic));
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final plan = _plans[_dayIndex];
    return Scaffold(
      backgroundColor: const Color(0xFFF5F6F8),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text(
          '规划详情',
          style: TextStyle(
            color: Color(0xFF1D1E22),
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      body: FadeTransition(
        opacity: _fadeAnimation,
        child: SlideTransition(
          position: _slideAnimation,
          child: ListView(
            padding: const EdgeInsets.fromLTRB(20, 16, 20, 20),
            physics: const BouncingScrollPhysics(),
            children: [
              _HeroHeader(),
              const SizedBox(height: 16),
              _StatCard(),
              const SizedBox(height: 16),
              _DayTabs(
                plans: _plans,
                selectedIndex: _dayIndex,
                onChanged: (index) => setState(() => _dayIndex = index),
              ),
              const SizedBox(height: 12),
              _DaySummary(plan: plan),
              const SizedBox(height: 12),
              AnimatedSwitcher(
                duration: const Duration(milliseconds: 300),
                switchInCurve: Curves.easeOut,
                switchOutCurve: Curves.easeIn,
                child: _TimelineList(
                  key: ValueKey(_dayIndex),
                  items: plan.items,
                ),
              ),
              const SizedBox(height: 90),
            ],
          ),
        ),
      ),
      bottomNavigationBar: SafeArea(
        top: false,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(20, 0, 20, 16),
          child: Row(
            children: [
              Expanded(
                child: OutlinedButton(
                  onPressed: () {},
                  style: OutlinedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(26),
                    ),
                    side: const BorderSide(color: Color(0xFFE0E3EA)),
                  ),
                  child: const Text('重新生成'),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: ElevatedButton(
                  onPressed: () {},
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF57A2E6),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(26),
                    ),
                  ),
                  child: const Text('保存并开始'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _HeroHeader extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(20),
      child: Container(
        height: 160,
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF353535), Color(0xFF1B1B1B)],
          ),
        ),
        child: Stack(
          children: [
            Align(
              alignment: Alignment.bottomCenter,
              child: Container(
                height: 60,
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    colors: [Colors.transparent, Colors.black54],
                  ),
                ),
              ),
            ),
            Positioned(
              left: 16,
              bottom: 24,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: const [
                  Text(
                    '合肥3日精华游',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  SizedBox(height: 4),
                  Text(
                    '合肥 → 合肥    3天行程',
                    style: TextStyle(color: Colors.white70, fontSize: 12),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatCard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
        boxShadow: const [
          BoxShadow(
            color: Color(0x12000000),
            blurRadius: 10,
            offset: Offset(0, 6),
          ),
        ],
      ),
      child: Row(
        children: const [
          _StatItem(label: '景点数', value: '6'),
          _DividerLine(),
          _StatItem(label: '总里程', value: '53km'),
          _DividerLine(),
          _StatItem(label: '预计花费', value: '¥1,200'),
          _DividerLine(),
          _StatItem(label: '最佳季节', value: '春秋季节'),
        ],
      ),
    );
  }
}

class _DividerLine extends StatelessWidget {
  const _DividerLine();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 1,
      height: 32,
      color: const Color(0xFFF0F1F4),
    );
  }
}

class _StatItem extends StatelessWidget {
  const _StatItem({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        children: [
          Text(
            value,
            style: const TextStyle(
              color: Color(0xFF4A90E2),
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            label,
            style: const TextStyle(fontSize: 12, color: Color(0xFF8C9099)),
          ),
        ],
      ),
    );
  }
}

class _DayTabs extends StatelessWidget {
  const _DayTabs({
    required this.plans,
    required this.selectedIndex,
    required this.onChanged,
  });

  final List<_DayPlan> plans;
  final int selectedIndex;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: plans
          .asMap()
          .entries
          .map(
            (entry) => Expanded(
              child: GestureDetector(
                onTap: () => onChanged(entry.key),
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 250),
                  margin: EdgeInsets.only(
                    right: entry.key == plans.length - 1 ? 0 : 10,
                  ),
                  padding: const EdgeInsets.symmetric(vertical: 10),
                  decoration: BoxDecoration(
                    color: entry.key == selectedIndex
                        ? const Color(0xFF5AA3E5)
                        : Colors.white,
                    borderRadius: BorderRadius.circular(14),
                    boxShadow: const [
                      BoxShadow(
                        color: Color(0x0F000000),
                        blurRadius: 6,
                        offset: Offset(0, 4),
                      ),
                    ],
                  ),
                  child: Column(
                    children: [
                      Text(
                        entry.value.title,
                        style: TextStyle(
                          fontWeight: FontWeight.w600,
                          color: entry.key == selectedIndex
                              ? Colors.white
                              : const Color(0xFF1D1E22),
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        entry.value.date,
                        style: TextStyle(
                          fontSize: 12,
                          color: entry.key == selectedIndex
                              ? Colors.white70
                              : const Color(0xFF8C9099),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          )
          .toList(),
    );
  }
}

class _DaySummary extends StatelessWidget {
  const _DaySummary({required this.plan});

  final _DayPlan plan;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        children: [
          Text(
            '预计游玩 ${plan.totalHours}',
            style: const TextStyle(fontWeight: FontWeight.w600),
          ),
          const Spacer(),
          Text(
            '行程 ${plan.distance}',
            style: const TextStyle(color: Color(0xFF8C9099)),
          ),
        ],
      ),
    );
  }
}

class _TimelineList extends StatelessWidget {
  const _TimelineList({super.key, required this.items});

  final List<_TimelineItem> items;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: items
          .map(
            (item) => Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: _TimelineCard(item: item),
            ),
          )
          .toList(),
    );
  }
}

class _TimelineCard extends StatelessWidget {
  const _TimelineCard({required this.item});

  final _TimelineItem item;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        SizedBox(
          width: 70,
          child: Column(
            children: [
              Text(item.start, style: const TextStyle(fontWeight: FontWeight.w600)),
              const SizedBox(height: 4),
              Text(item.end, style: const TextStyle(color: Color(0xFF8C9099))),
              const SizedBox(height: 10),
              Container(
                width: 12,
                height: 12,
                decoration: BoxDecoration(
                  color: Colors.white,
                  border: Border.all(color: const Color(0xFF5AA3E5), width: 2),
                  shape: BoxShape.circle,
                ),
              ),
              Container(
                width: 2,
                height: 60,
                color: const Color(0xFFE1E4EA),
              ),
            ],
          ),
        ),
        Expanded(
          child: Container(
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(16),
              boxShadow: const [
                BoxShadow(
                  color: Color(0x0D000000),
                  blurRadius: 6,
                  offset: Offset(0, 4),
                ),
              ],
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  item.title,
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  children: [
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                      decoration: BoxDecoration(
                        color: const Color(0xFFE8F1FF),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Text(
                        item.tag,
                        style: const TextStyle(
                          fontSize: 11,
                          color: Color(0xFF4A90E2),
                        ),
                      ),
                    ),
                    const SizedBox(width: 8),
                    Text(item.duration,
                        style: const TextStyle(color: Color(0xFF8C9099))),
                    const Spacer(),
                    Text(
                      item.rating.toStringAsFixed(1),
                      style: const TextStyle(
                        color: Color(0xFFFF8C2B),
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _DayPlan {
  const _DayPlan({
    required this.title,
    required this.date,
    required this.totalHours,
    required this.distance,
    required this.items,
  });

  final String title;
  final String date;
  final String totalHours;
  final String distance;
  final List<_TimelineItem> items;
}

class _TimelineItem {
  const _TimelineItem(
    this.start,
    this.end,
    this.title,
    this.tag,
    this.duration,
    this.rating,
  );

  final String start;
  final String end;
  final String title;
  final String tag;
  final String duration;
  final double rating;
}
