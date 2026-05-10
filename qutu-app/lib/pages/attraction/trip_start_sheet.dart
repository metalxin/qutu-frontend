import 'package:flutter/material.dart';
import '../../models/trip_plan.dart';
import '../../services/planning_api_service.dart';

class TripStartSheet extends StatefulWidget {
  const TripStartSheet({
    super.key,
    required this.spotName,
    required this.cityName,
  });

  final String spotName;
  final String cityName;

  @override
  State<TripStartSheet> createState() => _TripStartSheetState();
}

class _TripStartSheetState extends State<TripStartSheet>
    with SingleTickerProviderStateMixin {
  int _days = 3;
  int _preferenceIndex = 0;
  int _transportIndex = 0;
  bool _isGenerating = false;
  late final AnimationController _animController;
  late final Animation<double> _sheetAnimation;

  static const _preferences = [
    _PreferenceItem('auto', '自动推荐', 'AI智能规划最优路线'),
    _PreferenceItem('spots', '只规划景点', '不含餐饮住宿推荐'),
    _PreferenceItem('relax', '休闲模式', '每天2-3个景点，节奏慢'),
    _PreferenceItem('intensive', '特种兵模式', '紧凑安排，玩遍更多'),
  ];

  static const _transports = [
    _TransportItem('driving', '自驾', Icons.directions_car),
    _TransportItem('public', '公共交通', Icons.directions_transit),
    _TransportItem('walking', '步行', Icons.directions_walk),
    _TransportItem('cycling', '骑行', Icons.directions_bike),
  ];

  @override
  void initState() {
    super.initState();
    _animController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 400),
    );
    _sheetAnimation = CurvedAnimation(
      parent: _animController,
      curve: Curves.easeOutCubic,
    );
    _animController.forward();
  }

  @override
  void dispose() {
    _animController.dispose();
    super.dispose();
  }

  Future<void> _generate() async {
    if (_isGenerating) return;
    setState(() => _isGenerating = true);

    final pref = _preferences[_preferenceIndex];
    final transport = _transports[_transportIndex];

    final request = PlanningRequest(
      startLocation: widget.cityName,
      endLocation: widget.cityName,
      days: _days,
      preference: pref.id,
      transport: transport.id,
    );

    try {
      final api = PlanningApiService();
      final route = await api.generateRoute(request);

      if (!mounted) return;
      Navigator.of(context).pop(route);
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('规划生成失败: $e'),
          backgroundColor: Colors.red.shade400,
        ),
      );
    } finally {
      if (mounted) {
        setState(() => _isGenerating = false);
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final bottomPadding = MediaQuery.of(context).padding.bottom;
    return AnimatedBuilder(
      animation: _sheetAnimation,
      builder: (context, child) {
        return SlideTransition(
          position: Tween<Offset>(
            begin: const Offset(0, 0.15),
            end: Offset.zero,
          ).animate(_sheetAnimation),
          child: FadeTransition(
            opacity: _sheetAnimation,
            child: child,
          ),
        );
      },
      child: Container(
        padding: EdgeInsets.fromLTRB(20, 12, 20, 16 + bottomPadding),
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Center(
              child: Container(
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: const Color(0xFFE0E3EA),
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Container(
                  width: 40,
                  height: 40,
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [Color(0xFFFF6A3D), Color(0xFFFF9A6C)],
                    ),
                    borderRadius: BorderRadius.circular(12),
                  ),
                  child: const Icon(Icons.play_arrow_rounded,
                      color: Colors.white, size: 24),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        '开始旅程',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(height: 2),
                      Text(
                        '目的地: ${widget.spotName} · ${widget.cityName}',
                        style: const TextStyle(
                          fontSize: 12,
                          color: Color(0xFF8C9099),
                        ),
                      ),
                    ],
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
            const SizedBox(height: 20),
            _buildDaysSelector(),
            const SizedBox(height: 18),
            _buildPreferenceSelector(),
            const SizedBox(height: 18),
            _buildTransportSelector(),
            const SizedBox(height: 24),
            _buildGenerateButton(),
          ],
        ),
      ),
    );
  }

  Widget _buildDaysSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(Icons.calendar_today_outlined,
                size: 16, color: Color(0xFF2D5BFF)),
            const SizedBox(width: 6),
            const Text(
              '行程天数',
              style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
            ),
            const Spacer(),
            Text(
              '$_days天',
              style: const TextStyle(
                fontSize: 14,
                color: Color(0xFFFF6A3D),
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        Row(
          children: List.generate(7, (index) {
            final day = index + 1;
            final selected = day == _days;
            return Expanded(
              child: GestureDetector(
                onTap: () => setState(() => _days = day),
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 200),
                  margin: EdgeInsets.only(left: index == 0 ? 0 : 6),
                  height: 38,
                  decoration: BoxDecoration(
                    color: selected
                        ? const Color(0xFFFF6A3D)
                        : const Color(0xFFF6F7F9),
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Center(
                    child: Text(
                      '$day',
                      style: TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w600,
                        color: selected ? Colors.white : const Color(0xFF5E6168),
                      ),
                    ),
                  ),
                ),
              ),
            );
          }),
        ),
      ],
    );
  }

  Widget _buildPreferenceSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(Icons.tune, size: 16, color: Color(0xFF2D5BFF)),
            const SizedBox(width: 6),
            const Text(
              '规划偏好',
              style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
            ),
          ],
        ),
        const SizedBox(height: 10),
        Wrap(
          spacing: 8,
          runSpacing: 8,
          children: _preferences.asMap().entries.map((entry) {
            final index = entry.key;
            final pref = entry.value;
            final selected = index == _preferenceIndex;
            return GestureDetector(
              onTap: () => setState(() => _preferenceIndex = index),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 200),
                padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
                decoration: BoxDecoration(
                  color: selected
                      ? const Color(0xFFFFEEE8)
                      : const Color(0xFFF6F7F9),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(
                    color: selected
                        ? const Color(0xFFFF6A3D)
                        : const Color(0xFFE0E3EA),
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      pref.name,
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: selected
                            ? const Color(0xFFFF6A3D)
                            : const Color(0xFF1D1E22),
                      ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      pref.desc,
                      style: TextStyle(
                        fontSize: 10,
                        color: selected
                            ? const Color(0xFFFF6A3D).withAlpha(180)
                            : const Color(0xFF8C9099),
                      ),
                    ),
                  ],
                ),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildTransportSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          children: [
            const Icon(Icons.directions, size: 16, color: Color(0xFF2D5BFF)),
            const SizedBox(width: 6),
            const Text(
              '交通方式',
              style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
            ),
          ],
        ),
        const SizedBox(height: 10),
        Row(
          children: _transports.asMap().entries.map((entry) {
            final index = entry.key;
            final transport = entry.value;
            final selected = index == _transportIndex;
            return Expanded(
              child: GestureDetector(
                onTap: () => setState(() => _transportIndex = index),
                child: AnimatedContainer(
                  duration: const Duration(milliseconds: 200),
                  margin: EdgeInsets.only(left: index == 0 ? 0 : 8),
                  padding: const EdgeInsets.symmetric(vertical: 10),
                  decoration: BoxDecoration(
                    color: selected
                        ? const Color(0xFFE8F1FF)
                        : const Color(0xFFF6F7F9),
                    borderRadius: BorderRadius.circular(12),
                    border: Border.all(
                      color: selected
                          ? const Color(0xFF2D5BFF)
                          : const Color(0xFFE0E3EA),
                    ),
                  ),
                  child: Column(
                    children: [
                      Icon(
                        transport.icon,
                        size: 20,
                        color: selected
                            ? const Color(0xFF2D5BFF)
                            : const Color(0xFF8C9099),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        transport.name,
                        style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                          color: selected
                              ? const Color(0xFF2D5BFF)
                              : const Color(0xFF5E6168),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildGenerateButton() {
    return SizedBox(
      height: 52,
      width: double.infinity,
      child: ElevatedButton(
        onPressed: _isGenerating ? null : _generate,
        style: ElevatedButton.styleFrom(
          backgroundColor: const Color(0xFFFF6A3D),
          disabledBackgroundColor: const Color(0xFFFF6A3D).withAlpha(120),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(26),
          ),
        ),
        child: _isGenerating
            ? Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      color: Colors.white.withAlpha(200),
                    ),
                  ),
                  const SizedBox(width: 10),
                  const Text(
                    'AI正在规划中...',
                    style: TextStyle(
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ],
              )
            : Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  const Icon(Icons.auto_awesome, size: 20, color: Colors.white),
                  const SizedBox(width: 8),
                  Text(
                    'AI智能规划 $_days天行程',
                    style: const TextStyle(
                      fontWeight: FontWeight.w600,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
      ),
    );
  }
}

class _PreferenceItem {
  final String id;
  final String name;
  final String desc;
  const _PreferenceItem(this.id, this.name, this.desc);
}

class _TransportItem {
  final String id;
  final String name;
  final IconData icon;
  const _TransportItem(this.id, this.name, this.icon);
}

Future<AIRoute?> showTripStartSheet(
  BuildContext context, {
  required String spotName,
  required String cityName,
}) {
  return showModalBottomSheet<AIRoute>(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (context) => TripStartSheet(
      spotName: spotName,
      cityName: cityName,
    ),
  );
}
