import 'dart:ui';

import 'package:flutter/material.dart';
import '../../pages/attraction/attraction_sheet.dart';
import '../../pages/collection/time_capture_page.dart';
import '../../pages/demo/tabbar_plus_menu_page.dart';
import '../../pages/guide/guide_list_page.dart';
import '../../pages/planner/smart_plan_page.dart';
import '../../pages/story/travel_story_page.dart';
import '../../utils/navigation.dart';
import '../../widgets/guide_search_bar.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 120),
          physics: const BouncingScrollPhysics(),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const _TopBar(),
              const SizedBox(height: 16),
              const _SearchBar(),
              const SizedBox(height: 16),
              _FeatureRow(
                onCaptureTap: () =>
                    pushPage(context, const TimeCapturePage()),
                onGuideTap: () => pushPage(context, const GuideListPage()),
                onPlanTap: () => pushPage(context, const SmartPlanPage()),
                onStoryTap: () => pushPage(context, const TravelStoryPage()),
              ),
              const SizedBox(height: 18),
              const _HighlightCard(),
              const SizedBox(height: 18),
              _SectionHeader(
                onRegionTap: () => _showRegionSheet(context),
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  Expanded(
                    child: _PlaceCard(
                      title: '北京',
                      subtitle: '古都文化之旅',
                      rating: '4.9',
                      tag: '热门',
                      spots: '128个景点',
                      gradientColors: const [
                        Color(0xFFB45A34),
                        Color(0xFFF1C38C),
                      ],
                      onTap: () => _showAttractionSheet(context, '北京'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: _PlaceCard(
                      title: '南京',
                      subtitle: '六朝古都',
                      rating: '4.8',
                      tag: '推荐',
                      spots: '86个景点',
                      gradientColors: const [
                        Color(0xFF2E6D7A),
                        Color(0xFFA9D9E9),
                      ],
                      onTap: () => _showAttractionSheet(context, '南京'),
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
      floatingActionButton: _AddButton(
        onPressed: () => _showCreateSheet(context),
      ),
      bottomNavigationBar: const _BottomNavBar(),
    );
  }
}

void _showCreateSheet(BuildContext context) {
  showGeneralDialog(
    context: context,
    barrierDismissible: true,
    barrierLabel: 'create',
    barrierColor: Colors.black38,
    transitionDuration: const Duration(milliseconds: 260),
    pageBuilder: (context, animation, secondaryAnimation) {
      final bottomPadding = MediaQuery.of(context).padding.bottom;
      return GestureDetector(
        onTap: () => Navigator.of(context).pop(),
        child: Material(
          color: Colors.transparent,
          child: Stack(
            children: [
              BackdropFilter(
                filter: ImageFilter.blur(sigmaX: 8, sigmaY: 8),
                child: Container(color: Colors.black26),
              ),
              Align(
                alignment: Alignment.bottomCenter,
                child: GestureDetector(
                  onTap: () {},
                  child: _CreateSheetBody(
                    animation: animation,
                    bottomPadding: bottomPadding,
                    onClose: () => Navigator.of(context).pop(),
                  ),
                ),
              ),
            ],
          ),
        ),
      );
    },
    transitionBuilder: (context, animation, secondaryAnimation, child) {
      final curved = CurvedAnimation(
        parent: animation,
        curve: Curves.easeOutCubic,
      );
      return FadeTransition(
        opacity: curved,
        child: SlideTransition(
          position: Tween<Offset>(
            begin: const Offset(0, 0.12),
            end: Offset.zero,
          ).animate(curved),
          child: child,
        ),
      );
    },
  );
}

void _showRegionSheet(BuildContext context) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (context) => _RegionSheet(parentContext: context),
  );
}

void _showAttractionSheet(BuildContext context, String city) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (context) => AttractionSheet(city: city),
  );
}

class _CreateSheetBody extends StatelessWidget {
  const _CreateSheetBody({
    required this.animation,
    required this.bottomPadding,
    required this.onClose,
  });

  final Animation<double> animation;
  final double bottomPadding;
  final VoidCallback onClose;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.fromLTRB(20, 0, 20, 24 + bottomPadding),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          _StaggerItem(
            animation: animation,
            index: 0,
            child: const Align(
              alignment: Alignment.centerLeft,
              child: _CommandChip(),
            ),
          ),
          const SizedBox(height: 12),
          _StaggerItem(
            animation: animation,
            index: 1,
            child: const _ActionCard(
              title: '创建新的行程',
              trailingIcon: Icons.add,
              isDark: true,
            ),
          ),
          const SizedBox(height: 12),
          _StaggerItem(
            animation: animation,
            index: 2,
            child: const _ActionCard(
              title: '智能导入地点/行程',
              subtitle: '粘贴笔记链接、行程文本，或上传图片进行识别',
              trailingIcon: Icons.download,
              iconColor: Color(0xFF19B35A),
            ),
          ),
          const SizedBox(height: 12),
          _StaggerItem(
            animation: animation,
            index: 3,
            child: const _ActionCard(
              title: '记录行程路线',
              subtitle: '选择运动方式开始记录路线',
              trailingIcon: Icons.edit_location_alt_outlined,
              iconColor: Color(0xFFFC8A23),
            ),
          ),
          const SizedBox(height: 12),
          _StaggerItem(
            animation: animation,
            index: 4,
            child: const _ActionCard(
              title: '「采集」识别',
              subtitle: '识别同时收藏你的生活',
              trailingIcon: Icons.photo_camera_outlined,
              iconColor: Color(0xFF9C61FF),
            ),
          ),
          const SizedBox(height: 18),
          _StaggerItem(
            animation: animation,
            index: 5,
            child: _CloseButton(onPressed: onClose),
          ),
        ],
      ),
    );
  }
}

class _StaggerItem extends StatelessWidget {
  const _StaggerItem({
    required this.animation,
    required this.index,
    required this.child,
  });

  final Animation<double> animation;
  final int index;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    final start = 0.1 + (index * 0.1);
    final curved = CurvedAnimation(
      parent: animation,
      curve: Interval(start, 1.0, curve: Curves.easeOutCubic),
    );
    final bounce = TweenSequence<double>([
      TweenSequenceItem(
        tween: Tween<double>(begin: 0.88, end: 1.05)
            .chain(CurveTween(curve: Curves.easeOut)),
        weight: 70,
      ),
      TweenSequenceItem(
        tween: Tween<double>(begin: 1.05, end: 1.0)
            .chain(CurveTween(curve: Curves.easeOutBack)),
        weight: 30,
      ),
    ]).animate(curved);
    return FadeTransition(
      opacity: curved,
      child: ScaleTransition(
        scale: bounce,
        child: SlideTransition(
          position: Tween<Offset>(
            begin: const Offset(0, 0.12),
            end: Offset.zero,
          ).animate(curved),
          child: child,
        ),
      ),
    );
  }
}

class _RegionSheet extends StatefulWidget {
  const _RegionSheet({required this.parentContext});

  final BuildContext parentContext;

  @override
  State<_RegionSheet> createState() => _RegionSheetState();
}

class _RegionSheetState extends State<_RegionSheet> {
  String _selectedRegion = '中国大陆';

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.of(context).padding.bottom;
    return Container(
      padding: EdgeInsets.fromLTRB(20, 12, 20, 16 + bottomPadding),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: SingleChildScrollView(
        physics: const BouncingScrollPhysics(),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Expanded(
                  child: Text(
                    '选择目的地',
                    style: TextStyle(
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
            const SizedBox(height: 12),
            const GuideSearchBar(hintText: '搜索全球目的地'),
            const SizedBox(height: 12),
            RichText(
              text: const TextSpan(
                style: TextStyle(fontSize: 12, color: Color(0xFF8C9099)),
                children: [
                  TextSpan(text: '全球  |  亚洲  >  '),
                  TextSpan(
                    text: '中国大陆',
                    style: TextStyle(
                      color: Color(0xFF1DB954),
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            _RegionChip(
              label: '中国大陆',
              selected: _selectedRegion == '中国大陆',
              onTap: () => setState(() => _selectedRegion = '中国大陆'),
              activeColor: const Color(0xFF1DB954),
            ),
            const SizedBox(height: 16),
            _RegionSection(
              title: '北方地区',
              items: northRegions,
              selected: _selectedRegion,
              onSelect: (value) => setState(() => _selectedRegion = value),
            ),
            const SizedBox(height: 16),
            _RegionSection(
              title: '南方地区',
              items: southRegions,
              selected: _selectedRegion,
              onSelect: (value) => setState(() => _selectedRegion = value),
            ),
            const SizedBox(height: 16),
            _RegionSection(
              title: '西部地区',
              items: westRegions,
              selected: _selectedRegion,
              onSelect: (value) => setState(() => _selectedRegion = value),
            ),
            const SizedBox(height: 18),
            Row(
              children: [
                OutlinedButton.icon(
                  onPressed: () => Navigator.of(context).pop(),
                  style: OutlinedButton.styleFrom(
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(22),
                    ),
                    side: const BorderSide(color: Color(0xFFE0E3EA)),
                    foregroundColor: const Color(0xFF5E6168),
                    padding:
                        const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  ),
                  icon: const Icon(Icons.arrow_back_ios_new, size: 14),
                  label: const Text('返回'),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: SizedBox(
                    height: 44,
                    child: ElevatedButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                        _showAttractionSheet(
                          widget.parentContext,
                          _selectedRegion,
                        );
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: const Color(0xFF1DB954),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(22),
                        ),
                      ),
                      child: const Text(
                        '查看景点',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    ),
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

class _RegionSection extends StatelessWidget {
  const _RegionSection({
    required this.title,
    required this.items,
    required this.selected,
    required this.onSelect,
  });

  final String title;
  final List<String> items;
  final String selected;
  final ValueChanged<String> onSelect;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontSize: 14,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 10),
        Wrap(
          spacing: 10,
          runSpacing: 10,
          children: items
              .map(
                (item) => _RegionChip(
                  label: item,
                  selected: selected == item,
                  onTap: () => onSelect(item),
                  activeColor: const Color(0xFF1DB954),
                ),
              )
              .toList(),
        ),
      ],
    );
  }
}

class _RegionChip extends StatelessWidget {
  const _RegionChip({
    required this.label,
    required this.selected,
    required this.onTap,
    required this.activeColor,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;
  final Color activeColor;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(16),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? const Color(0xFFE8F7EF) : const Color(0xFFF1F2F5),
          borderRadius: BorderRadius.circular(16),
          border: Border.all(
            color: selected ? activeColor : const Color(0xFFE0E3EA),
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: selected ? activeColor : const Color(0xFF5E6168),
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
    );
  }
}

const List<String> northRegions = [
  '北京',
  '天津',
  '河北',
  '山西',
  '内蒙古',
];

const List<String> southRegions = [
  '上海',
  '江苏',
  '浙江',
  '福建',
  '广东',
  '海南',
];

const List<String> westRegions = [
  '四川',
  '重庆',
  '云南',
  '西藏',
  '新疆',
];

class _TopBar extends StatelessWidget {
  const _TopBar();

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        SizedBox(
          key: const ValueKey('home_logo'),
          height: 32,
          width: 140,
          child: Image.asset(
            'assets/logo/app_logo.png',
            fit: BoxFit.contain,
            errorBuilder: (context, error, stack) => const Icon(
              Icons.map_outlined,
              color: Color(0xFF2D5BFF),
            ),
          ),
        ),
        const Spacer(),
        _CircleIconButton(
          icon: Icons.notifications_none,
          onPressed: () {},
        ),
        const SizedBox(width: 10),
        _CircleIconButton(
          icon: Icons.person_outline,
          onPressed: () {},
        ),
      ],
    );
  }
}

class _SearchBar extends StatelessWidget {
  const _SearchBar();

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 46,
      padding: const EdgeInsets.symmetric(horizontal: 14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(
            color: Color(0x14000000),
            blurRadius: 10,
            offset: Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        children: const [
          Icon(Icons.search, color: Color(0xFF9AA0AA)),
          SizedBox(width: 8),
          Text(
            '搜索城市、景点、攻略',
            style: TextStyle(color: Color(0xFF9AA0AA)),
          ),
        ],
      ),
    );
  }
}

class _FeatureRow extends StatelessWidget {
  const _FeatureRow({
    required this.onCaptureTap,
    required this.onGuideTap,
    required this.onPlanTap,
    required this.onStoryTap,
  });

  final VoidCallback onCaptureTap;
  final VoidCallback onGuideTap;
  final VoidCallback onPlanTap;
  final VoidCallback onStoryTap;

  @override
  Widget build(BuildContext context) {
    final items = [
      _FeatureItem('时光采集', Icons.photo_camera_outlined, Color(0xFFD8EDFF)),
      _FeatureItem('精选攻略', Icons.description_outlined, Color(0xFFE7F6E7)),
      _FeatureItem('智能规划', Icons.calendar_month_outlined, Color(0xFFFFF0D9)),
      _FeatureItem('旅行故事', Icons.bookmark_outline, Color(0xFFF1E7FF)),
      _FeatureItem('旅行清单', Icons.fact_check_outlined, Color(0xFFFFE7F0)),
    ];
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: items
          .map(
            (item) => Expanded(
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 4),
                child: InkWell(
                  borderRadius: BorderRadius.circular(14),
                  onTap: item.title == '时光采集'
                      ? onCaptureTap
                      : item.title == '精选攻略'
                          ? onGuideTap
                          : item.title == '智能规划'
                              ? onPlanTap
                              : item.title == '旅行故事'
                                  ? onStoryTap
                                  : item.title == '旅行清单'
                                      ? () => pushPage(
                                            context,
                                            const TabBarPlusMenuPage(),
                                          )
                                      : null,
                  child: Column(
                    children: [
                      Container(
                        height: 56,
                        width: 56,
                        decoration: BoxDecoration(
                          color: item.color,
                          borderRadius: BorderRadius.circular(14),
                        ),
                        child: Icon(item.icon, color: const Color(0xFF2E5BFF)),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        item.title,
                        style: const TextStyle(
                          fontSize: 11,
                          color: Color(0xFF5E6168),
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

class _FeatureItem {
  const _FeatureItem(this.title, this.icon, this.color);

  final String title;
  final IconData icon;
  final Color color;
}

class _HighlightCard extends StatelessWidget {
  const _HighlightCard();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: const [
          BoxShadow(
            color: Color(0x12000000),
            blurRadius: 10,
            offset: Offset(0, 6),
          ),
        ],
      ),
      child: Row(
        children: [
          Container(
            width: 70,
            height: 70,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16),
              gradient: const LinearGradient(
                begin: Alignment.topLeft,
                end: Alignment.bottomRight,
                colors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
              ),
            ),
            child: const Icon(Icons.photo_camera_outlined,
                color: Colors.white70),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: const [
                Text(
                  '北京周末精选路线',
                  style: TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                SizedBox(height: 6),
                Text(
                  '收藏了 128 个景点 · 42 条路线',
                  style: TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.chevron_right, color: Color(0xFF8C9099)),
        ],
      ),
    );
  }
}

class _SectionHeader extends StatelessWidget {
  const _SectionHeader({required this.onRegionTap});

  final VoidCallback onRegionTap;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        const Text(
          '目的地',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.w600,
            color: Color(0xFF1D1E22),
          ),
        ),
        const Spacer(),
        InkWell(
          borderRadius: BorderRadius.circular(16),
          onTap: onRegionTap,
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
            decoration: BoxDecoration(
              color: const Color(0xFFE8F7EF),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Row(
              children: const [
                Icon(Icons.place_outlined,
                    size: 14, color: Color(0xFF1DB954)),
                SizedBox(width: 4),
                Text(
                  '地区',
                  style: TextStyle(
                    fontSize: 12,
                    color: Color(0xFF1DB954),
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _PlaceCard extends StatelessWidget {
  const _PlaceCard({
    required this.title,
    required this.subtitle,
    required this.rating,
    required this.tag,
    required this.spots,
    required this.gradientColors,
    required this.onTap,
  });

  final String title;
  final String subtitle;
  final String rating;
  final String tag;
  final String spots;
  final List<Color> gradientColors;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(20),
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: const [
            BoxShadow(
              color: Color(0x12000000),
              blurRadius: 10,
              offset: Offset(0, 6),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              height: 84,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                gradient: LinearGradient(
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                  colors: gradientColors,
                ),
              ),
              child: const Align(
                alignment: Alignment.center,
                child: Icon(Icons.photo_camera_outlined,
                    color: Colors.white70),
              ),
            ),
            const SizedBox(height: 10),
            Row(
              children: [
                Text(
                  title,
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
                    tag,
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
              subtitle,
              style: const TextStyle(
                fontSize: 12,
                color: Color(0xFF8C9099),
              ),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                const Icon(Icons.star,
                    size: 14, color: Color(0xFFFFB74D)),
                const SizedBox(width: 4),
                Text(
                  rating,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF5E6168),
                  ),
                ),
                const Spacer(),
                Text(
                  spots,
                  style: const TextStyle(
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

class _AddButton extends StatelessWidget {
  const _AddButton({required this.onPressed});

  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: onPressed,
      backgroundColor: const Color(0xFFFF6A3D),
      child: const Icon(Icons.add, size: 28),
    );
  }
}

class _BottomNavBar extends StatelessWidget {
  const _BottomNavBar();

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      elevation: 0,
      shape: const CircularNotchedRectangle(),
      notchMargin: 8,
      child: SizedBox(
        height: 70,
        child: Row(
          children: const [
            Expanded(
              child: _NavItem(
                icon: Icons.home_filled,
                label: '主页',
                selected: true,
              ),
            ),
            SizedBox(width: 60),
            Expanded(
              child: _NavItem(
                icon: Icons.place_outlined,
                label: '附近',
                selected: false,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _NavItem extends StatelessWidget {
  const _NavItem({
    required this.icon,
    required this.label,
    required this.selected,
  });

  final IconData icon;
  final String label;
  final bool selected;

  @override
  Widget build(BuildContext context) {
    final color = selected ? const Color(0xFF2D5BFF) : const Color(0xFF8C9099);
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(icon, color: color),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(fontSize: 11, color: color),
        ),
      ],
    );
  }
}

class _CircleIconButton extends StatelessWidget {
  const _CircleIconButton({required this.icon, required this.onPressed});

  final IconData icon;
  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 34,
      height: 34,
      decoration: const BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onPressed,
        child: Icon(icon, size: 18, color: const Color(0xFF5E6168)),
      ),
    );
  }
}

class _CommandChip extends StatelessWidget {
  const _CommandChip();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: const Color(0xFFE0E3EA)),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: const [
          Icon(Icons.tips_and_updates_outlined,
              size: 14, color: Color(0xFFFFB74D)),
          SizedBox(width: 4),
          Text(
            '行程助手',
            style: TextStyle(
              fontSize: 12,
              color: Color(0xFF5E6168),
              fontWeight: FontWeight.w600,
            ),
          ),
        ],
      ),
    );
  }
}

class _ActionCard extends StatelessWidget {
  const _ActionCard({
    required this.title,
    this.subtitle,
    required this.trailingIcon,
    this.iconColor,
    this.isDark = false,
  });

  final String title;
  final String? subtitle;
  final IconData trailingIcon;
  final Color? iconColor;
  final bool isDark;

  @override
  Widget build(BuildContext context) {
    final background = isDark ? const Color(0xFF2D5BFF) : Colors.white;
    final titleColor = isDark ? Colors.white : const Color(0xFF1D1E22);
    final subColor = isDark ? Colors.white70 : const Color(0xFF8C9099);
    final iconColor = isDark ? Colors.white : this.iconColor;
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: background,
        borderRadius: BorderRadius.circular(20),
        boxShadow: const [
          BoxShadow(
            color: Color(0x14000000),
            blurRadius: 10,
            offset: Offset(0, 6),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  title,
                  style: TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                    color: titleColor,
                  ),
                ),
                if (subtitle != null) ...[
                  const SizedBox(height: 6),
                  Text(
                    subtitle!,
                    style: TextStyle(fontSize: 12, color: subColor),
                  ),
                ],
              ],
            ),
          ),
          Icon(trailingIcon, color: iconColor, size: 22),
        ],
      ),
    );
  }
}

class _CloseButton extends StatelessWidget {
  const _CloseButton({required this.onPressed});

  final VoidCallback onPressed;

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 48,
      width: double.infinity,
      child: ElevatedButton(
        onPressed: onPressed,
        style: ElevatedButton.styleFrom(
          backgroundColor: Colors.white,
          foregroundColor: const Color(0xFF2D5BFF),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(24),
          ),
        ),
        child: const Text(
          '关闭',
          style: TextStyle(fontWeight: FontWeight.w600),
        ),
      ),
    );
  }
}
