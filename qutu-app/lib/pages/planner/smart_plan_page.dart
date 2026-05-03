import 'package:flutter/material.dart';
import '../../utils/navigation.dart';
import 'smart_plan_detail_page.dart';

class SmartPlanPage extends StatefulWidget {
  const SmartPlanPage({super.key});

  @override
  State<SmartPlanPage> createState() => _SmartPlanPageState();
}

class _SmartPlanPageState extends State<SmartPlanPage> {
  int _days = 3;
  int _preferenceIndex = 0;
  final List<String> _waypoints = [];
  bool _isGenerating = false;
  Future<void> _startGenerate() async {
    if (_isGenerating) return;
    setState(() => _isGenerating = true);
    if (!mounted) return;
    await showDialog<void>(
      context: context,
      barrierDismissible: false,
      builder: (context) => const _GeneratingDialog(),
    );
    setState(() => _isGenerating = false);
    if (!mounted) return;
    pushPage(context, const SmartPlanDetailPage());
  }

  @override
  Widget build(BuildContext context) {
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
          '智能规划',
          style: TextStyle(
            color: Color(0xFF1D1E22),
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      body: Column(
        children: [
          _HeaderBanner(
            onDotsTap: () {},
          ),
          Expanded(
            child: ListView(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 20),
              physics: const BouncingScrollPhysics(),
              children: [
                _RouteCard(
                  waypoints: _waypoints,
                  onAdd: () {
                    setState(() {
                      _waypoints.add('途经地${_waypoints.length + 1}');
                    });
                  },
                  onRemove: (index) {
                    setState(() {
                      _waypoints.removeAt(index);
                    });
                  },
                  onClear: () {
                    setState(() {
                      _waypoints.clear();
                    });
                  },
                ),
                const SizedBox(height: 16),
                _SettingCard(
                  days: _days,
                  onDecrease: () {
                    setState(() {
                      if (_days > 1) _days -= 1;
                    });
                  },
                  onIncrease: () {
                    setState(() {
                      _days += 1;
                    });
                  },
                  preferenceIndex: _preferenceIndex,
                  onPreferenceChanged: (index) {
                    setState(() => _preferenceIndex = index);
                  },
                ),
              ],
            ),
          ),
          SafeArea(
            top: false,
            child: Padding(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 16),
              child: SizedBox(
                height: 52,
                child: ElevatedButton(
                  onPressed: _startGenerate,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF1BC6A3),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(26),
                    ),
                  ),
                  child: const Text(
                    '智能生成路线',
                    style: TextStyle(fontWeight: FontWeight.w600),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _HeaderBanner extends StatelessWidget {
  const _HeaderBanner({required this.onDotsTap});

  final VoidCallback onDotsTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.fromLTRB(20, 18, 20, 20),
      decoration: const BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.centerLeft,
          end: Alignment.centerRight,
          colors: [Color(0xFF00C6B7), Color(0xFF3A8DFF)],
        ),
      ),
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: const [
                Text(
                  '智能行程规划',
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.w600,
                    color: Colors.white,
                  ),
                ),
                SizedBox(height: 6),
                Text(
                  'AI为您定制专属自驾路线',
                  style: TextStyle(color: Colors.white70),
                ),
              ],
            ),
          ),
          IconButton(
            onPressed: onDotsTap,
            icon: const Icon(Icons.tune, color: Colors.white),
          ),
          const SizedBox(width: 6),
          const _ProgressDots(),
        ],
      ),
    );
  }
}

class _ProgressDots extends StatelessWidget {
  const _ProgressDots();

  @override
  Widget build(BuildContext context) {
    return Row(
      children: List.generate(
        4,
        (index) => Container(
          width: 10,
          height: 10,
          margin: const EdgeInsets.only(left: 6),
          decoration: BoxDecoration(
            color: index == 0 ? Colors.white : Colors.white38,
            shape: BoxShape.circle,
          ),
        ),
      ),
    );
  }
}

class _RouteCard extends StatelessWidget {
  const _RouteCard({
    required this.waypoints,
    required this.onAdd,
    required this.onRemove,
    required this.onClear,
  });

  final List<String> waypoints;
  final VoidCallback onAdd;
  final ValueChanged<int> onRemove;
  final VoidCallback onClear;

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
      child: Column(
        children: [
          _RouteTile(
            label: '起',
            labelColor: const Color(0xFF2D7DFF),
            title: '合肥市',
            onRemove: onClear,
          ),
          const Divider(height: 20, color: Color(0xFFEAECEF)),
          InkWell(
            borderRadius: BorderRadius.circular(14),
            onTap: onAdd,
            child: Row(
              children: [
                Container(
                  width: 28,
                  height: 28,
                  decoration: BoxDecoration(
                    color: const Color(0xFFE8F7F4),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(
                      color: const Color(0xFF1BC6A3),
                      style: BorderStyle.solid,
                    ),
                  ),
                  child: const Icon(Icons.add,
                      size: 18, color: Color(0xFF1BC6A3)),
                ),
                const SizedBox(width: 10),
                const Expanded(
                  child: Text(
                    '添加途经地',
                    style: TextStyle(
                      fontSize: 14,
                      color: Color(0xFF1BC6A3),
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                const Text(
                  '（建议按游玩顺序添加）',
                  style: TextStyle(fontSize: 12, color: Color(0xFF9AA0AA)),
                ),
                const SizedBox(width: 6),
                const Icon(Icons.chevron_right, color: Color(0xFF9AA0AA)),
              ],
            ),
          ),
          if (waypoints.isNotEmpty) ...[
            const SizedBox(height: 12),
            Column(
              children: waypoints
                  .asMap()
                  .entries
                  .map(
                    (entry) => Padding(
                      padding: const EdgeInsets.only(top: 12),
                      child: _RouteTile(
                        label: '途',
                        labelColor: const Color(0xFF00B894),
                        title: entry.value,
                        onRemove: () => onRemove(entry.key),
                      ),
                    ),
                  )
                  .toList(),
            ),
          ],
          const Divider(height: 20, color: Color(0xFFEAECEF)),
          _RouteTile(
            label: '终',
            labelColor: const Color(0xFFFF6A5F),
            title: '合肥市',
            onRemove: onClear,
          ),
        ],
      ),
    );
  }
}

class _RouteTile extends StatelessWidget {
  const _RouteTile({
    required this.label,
    required this.labelColor,
    required this.title,
    required this.onRemove,
  });

  final String label;
  final Color labelColor;
  final String title;
  final VoidCallback onRemove;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          width: 28,
          height: 28,
          decoration: BoxDecoration(
            color: labelColor.withAlpha(38),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Center(
            child: Text(
              label,
              style: TextStyle(
                color: labelColor,
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: Text(
            title,
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w600,
            ),
          ),
        ),
        InkWell(
          borderRadius: BorderRadius.circular(12),
          onTap: onRemove,
          child: const Padding(
            padding: EdgeInsets.all(6),
            child: Icon(Icons.close, size: 18, color: Color(0xFFB0B5BD)),
          ),
        ),
      ],
    );
  }
}

class _SettingCard extends StatelessWidget {
  const _SettingCard({
    required this.days,
    required this.onDecrease,
    required this.onIncrease,
    required this.preferenceIndex,
    required this.onPreferenceChanged,
  });

  final int days;
  final VoidCallback onDecrease;
  final VoidCallback onIncrease;
  final int preferenceIndex;
  final ValueChanged<int> onPreferenceChanged;

  @override
  Widget build(BuildContext context) {
    const preferences = ['自动推荐', '只规划景点'];
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
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: const [
              Icon(Icons.settings, color: Color(0xFF1BC6A3), size: 18),
              SizedBox(width: 6),
              Text(
                '行程设置',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              ),
            ],
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              const Text(
                '行程天数',
                style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
              ),
              const Spacer(),
              _CounterButton(icon: Icons.remove, onTap: onDecrease),
              const SizedBox(width: 12),
              Text(
                '$days',
                style:
                    const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
              ),
              const SizedBox(width: 6),
              const Text(
                '天',
                style: TextStyle(fontSize: 13, color: Color(0xFF8C9099)),
              ),
              const SizedBox(width: 12),
              _CounterButton(icon: Icons.add, onTap: onIncrease),
            ],
          ),
          const SizedBox(height: 18),
          const Text(
            '规划偏好',
            style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 10),
          Wrap(
            spacing: 10,
            children: preferences
                .asMap()
                .entries
                .map(
                  (entry) => ChoiceChip(
                    label: Text(entry.value),
                    selected: entry.key == preferenceIndex,
                    selectedColor: const Color(0xFFE8F7F4),
                    backgroundColor: const Color(0xFFF1F2F6),
                    side: BorderSide(
                      color: entry.key == preferenceIndex
                          ? const Color(0xFF1BC6A3)
                          : const Color(0xFFE0E3EA),
                    ),
                    labelStyle: TextStyle(
                      fontSize: 12,
                      color: entry.key == preferenceIndex
                          ? const Color(0xFF1BC6A3)
                          : const Color(0xFF5E6168),
                      fontWeight: FontWeight.w600,
                    ),
                    onSelected: (_) => onPreferenceChanged(entry.key),
                  ),
                )
                .toList(),
          ),
        ],
      ),
    );
  }
}

class _CounterButton extends StatelessWidget {
  const _CounterButton({required this.icon, required this.onTap});

  final IconData icon;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 32,
      height: 32,
      decoration: const BoxDecoration(
        color: Color(0xFFE8F7F4),
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onTap,
        child: Icon(icon, size: 18, color: const Color(0xFF1BC6A3)),
      ),
    );
  }
}

class _GeneratingDialog extends StatefulWidget {
  const _GeneratingDialog();

  @override
  State<_GeneratingDialog> createState() => _GeneratingDialogState();
}

class _GeneratingDialogState extends State<_GeneratingDialog>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  late final Animation<double> _scale;
  late final Animation<double> _fade;

  @override
  void initState() {
    super.initState();
    _controller =
        AnimationController(vsync: this, duration: const Duration(seconds: 2));
    _scale = Tween<double>(begin: 0.92, end: 1)
        .animate(CurvedAnimation(parent: _controller, curve: Curves.easeOut));
    _fade = CurvedAnimation(parent: _controller, curve: Curves.easeOut);
    _controller.forward();
    Future.delayed(const Duration(seconds: 2), () {
      if (mounted) {
        Navigator.of(context).pop();
      }
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Center(
      child: FadeTransition(
        opacity: _fade,
        child: ScaleTransition(
          scale: _scale,
          child: Container(
            width: 280,
            padding: const EdgeInsets.fromLTRB(20, 24, 20, 20),
            decoration: BoxDecoration(
              color: Colors.white,
              borderRadius: BorderRadius.circular(20),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                _PulseRing(controller: _controller),
                const SizedBox(height: 16),
                const Text(
                  'AI正在为您规划路线',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                    color: Color(0xFF1D1E22),
                  ),
                ),
                const SizedBox(height: 6),
                const Text(
                  '正在生成最优路线...',
                  style: TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
                const SizedBox(height: 14),
                ClipRRect(
                  borderRadius: BorderRadius.circular(6),
                  child: LinearProgressIndicator(
                    value: _controller.value,
                    minHeight: 6,
                    backgroundColor: const Color(0xFFE6EAF0),
                    valueColor:
                        const AlwaysStoppedAnimation<Color>(Color(0xFF1BC6A3)),
                  ),
                ),
                const SizedBox(height: 10),
                AnimatedBuilder(
                  animation: _controller,
                  builder: (context, child) {
                    final percent = (_controller.value * 100).clamp(0, 100);
                    return Text(
                      '${percent.toStringAsFixed(0)}%',
                      style: const TextStyle(
                        fontSize: 12,
                        color: Color(0xFF1BC6A3),
                        fontWeight: FontWeight.w600,
                      ),
                    );
                  },
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PulseRing extends StatelessWidget {
  const _PulseRing({required this.controller});

  final AnimationController controller;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: controller,
      builder: (context, child) {
        final scale = 1 + controller.value * 0.6;
        final opacity = (1 - controller.value).clamp(0.2, 1.0);
        final ringAlpha = (opacity * 0.2 * 255).round();
        return Stack(
          alignment: Alignment.center,
          children: [
            Container(
              width: 78 * scale,
              height: 78 * scale,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: const Color(0xFF1BC6A3).withAlpha(ringAlpha),
              ),
            ),
            Container(
              width: 48,
              height: 48,
              decoration: const BoxDecoration(
                shape: BoxShape.circle,
                color: Color(0xFF1BC6A3),
              ),
            ),
          ],
        );
      },
    );
  }
}
