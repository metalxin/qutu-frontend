import 'dart:ui';

import 'package:flutter/material.dart';

class TabBarPlusMenuPage extends StatefulWidget {
  const TabBarPlusMenuPage({super.key});

  @override
  State<TabBarPlusMenuPage> createState() => _TabBarPlusMenuPageState();
}

class _TabBarPlusMenuPageState extends State<TabBarPlusMenuPage>
    with SingleTickerProviderStateMixin {
  static const _barHeight = 72.0;
  static const _plusSize = 112.0;
  static const _menuPadding = 24.0;
  static const _menuAnimationDuration = Duration(milliseconds: 400);
  static const _bounceCurve = Cubic(0.34, 1.56, 0.64, 1);

  late final AnimationController _controller;
  bool _showMenu = false;

  final _menuItems = const [
    _MenuItemData(
      title: '创建行程',
      subtitle: '快速开始新路线',
      icon: Icons.add_road_outlined,
      color: Color(0xFF2D5BFF),
    ),
    _MenuItemData(
      title: '智能导入',
      subtitle: '识别笔记或图片',
      icon: Icons.auto_awesome_outlined,
      color: Color(0xFF19B35A),
    ),
    _MenuItemData(
      title: '记录路线',
      subtitle: '开启运动轨迹',
      icon: Icons.edit_location_alt_outlined,
      color: Color(0xFFFC8A23),
    ),
    _MenuItemData(
      title: '采集识别',
      subtitle: '收藏你的生活',
      icon: Icons.photo_camera_outlined,
      color: Color(0xFF9C61FF),
    ),
  ];

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: _menuAnimationDuration,
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _show() {
    if (_showMenu) return;
    setState(() => _showMenu = true);
    _controller.forward(from: 0);
  }

  Future<void> _hide() async {
    if (!_showMenu) return;
    setState(() => _showMenu = false);
    await _controller.reverse();
  }

  void _toggle() {
    if (_showMenu) {
      _hide();
    } else {
      _show();
    }
  }

  Animation<double> _itemAnimation(double delayMs) {
    final start = delayMs / _menuAnimationDuration.inMilliseconds;
    return CurvedAnimation(
      parent: _controller,
      curve: Interval(start, 1, curve: _bounceCurve),
    );
  }

  @override
  Widget build(BuildContext context) {
    final bottomInset = MediaQuery.of(context).padding.bottom;
    return Scaffold(
      backgroundColor: const Color(0xFFF6F7F9),
      body: Stack(
        children: [
          Positioned.fill(
            child: Column(
              children: [
                const SizedBox(height: 60),
                const Text(
                  '首页内容区域',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  '点击下方加号按钮查看弹出菜单',
                  style: TextStyle(color: Color(0xFF8C9099)),
                ),
                const Spacer(),
                SizedBox(height: _barHeight + bottomInset + 40),
              ],
            ),
          ),
          AnimatedBuilder(
            animation: _controller,
            builder: (context, child) {
              final active = _showMenu || _controller.value > 0;
              if (!active) return const SizedBox.shrink();
              return Stack(
                children: [
                  AnimatedOpacity(
                    duration: _menuAnimationDuration,
                    curve: Curves.ease,
                    opacity: _showMenu ? 1 : 0,
                    child: GestureDetector(
                      onTap: _hide,
                      child: BackdropFilter(
                        filter:
                            ImageFilter.blur(sigmaX: 30, sigmaY: 30),
                        child: Container(
                          color: const Color(0x4D000000),
                        ),
                      ),
                    ),
                  ),
                  IgnorePointer(
                    ignoring: !_showMenu,
                    child: Align(
                      alignment: Alignment.bottomCenter,
                      child: Padding(
                        padding: EdgeInsets.fromLTRB(
                            _menuPadding, 0, _menuPadding, 16 + bottomInset),
                        child: Stack(
                          clipBehavior: Clip.none,
                          children: [
                            _MenuContainer(
                              items: _menuItems,
                              animations: [
                                _itemAnimation(0),
                                _itemAnimation(50),
                                _itemAnimation(100),
                                _itemAnimation(150),
                              ],
                              closeAnimation: _itemAnimation(200),
                              onClose: _hide,
                            ),
                            Positioned(
                              left: 0,
                              top: -60,
                              child: _FloatCommandButton(
                                animation: _itemAnimation(250),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ],
              );
            },
          ),
          Positioned(
            left: 0,
            right: 0,
            bottom: 0,
            child: _BottomBar(
              barHeight: _barHeight + bottomInset,
              bottomInset: bottomInset,
            ),
          ),
          Positioned(
            left: 0,
            right: 0,
            bottom: _barHeight - 60,
            child: Center(
              child: _PlusButton(
                size: _plusSize,
                onTap: _toggle,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _BottomBar extends StatelessWidget {
  const _BottomBar({
    required this.barHeight,
    required this.bottomInset,
  });

  final double barHeight;
  final double bottomInset;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: barHeight,
      padding: EdgeInsets.only(bottom: bottomInset),
      decoration: const BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Color(0x12000000),
            blurRadius: 12,
            offset: Offset(0, -4),
          ),
        ],
      ),
      child: Row(
        children: const [
          Expanded(child: _TabItem(icon: Icons.home_filled, label: '首页')),
          SizedBox(width: 80),
          Expanded(child: _TabItem(icon: Icons.place_outlined, label: '附近')),
        ],
      ),
    );
  }
}

class _TabItem extends StatelessWidget {
  const _TabItem({required this.icon, required this.label});

  final IconData icon;
  final String label;

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(icon, size: 24, color: const Color(0xFF8C9099)),
        const SizedBox(height: 4),
        Text(
          label,
          style: const TextStyle(fontSize: 11, color: Color(0xFF8C9099)),
        ),
      ],
    );
  }
}

class _PlusButton extends StatelessWidget {
  const _PlusButton({required this.size, required this.onTap});

  final double size;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          gradient: const LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [Color(0xFFFF6B6B), Color(0xFFFF8E53)],
          ),
          boxShadow: const [
            BoxShadow(
              color: Color(0x33000000),
              blurRadius: 16,
              offset: Offset(0, 8),
            ),
          ],
        ),
        child: const Icon(Icons.add, color: Colors.white, size: 38),
      ),
    );
  }
}

class _MenuContainer extends StatelessWidget {
  const _MenuContainer({
    required this.items,
    required this.animations,
    required this.closeAnimation,
    required this.onClose,
  });

  final List<_MenuItemData> items;
  final List<Animation<double>> animations;
  final Animation<double> closeAnimation;
  final VoidCallback onClose;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(20, 20, 20, 24),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
        boxShadow: const [
          BoxShadow(
            color: Color(0x14000000),
            blurRadius: 16,
            offset: Offset(0, 8),
          ),
        ],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          for (int i = 0; i < items.length; i++) ...[
            _AnimatedMenuItem(
              animation: animations[i],
              child: _MenuItem(data: items[i]),
            ),
            if (i != items.length - 1) const SizedBox(height: 12),
          ],
          const SizedBox(height: 18),
          _AnimatedMenuItem(
            animation: closeAnimation,
            offset: 60,
            child: _CloseCircleButton(onPressed: onClose),
          ),
        ],
      ),
    );
  }
}

class _AnimatedMenuItem extends StatelessWidget {
  const _AnimatedMenuItem({
    required this.animation,
    required this.child,
    this.offset = 80,
  });

  final Animation<double> animation;
  final Widget child;
  final double offset;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: animation,
      builder: (context, _) {
        final value = animation.value;
        return Opacity(
          opacity: value,
          child: Transform.translate(
            offset: Offset(0, (1 - value) * offset),
            child: Transform.scale(
              scale: 0.9 + 0.1 * value,
              child: child,
            ),
          ),
        );
      },
    );
  }
}

class _MenuItem extends StatelessWidget {
  const _MenuItem({required this.data});

  final _MenuItemData data;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFFF6F7F9),
        borderRadius: BorderRadius.circular(18),
      ),
      child: Row(
        children: [
          Container(
            width: 44,
            height: 44,
            decoration: BoxDecoration(
              color: data.color.withAlpha(36),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Icon(data.icon, color: data.color, size: 22),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  data.title,
                  style: const TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  data.subtitle,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.chevron_right, color: Color(0xFFB0B5BD)),
        ],
      ),
    );
  }
}

class _CloseCircleButton extends StatefulWidget {
  const _CloseCircleButton({required this.onPressed});

  final VoidCallback onPressed;

  @override
  State<_CloseCircleButton> createState() => _CloseCircleButtonState();
}

class _CloseCircleButtonState extends State<_CloseCircleButton>
    with SingleTickerProviderStateMixin {
  late final AnimationController _pressController;

  @override
  void initState() {
    super.initState();
    _pressController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 120),
      lowerBound: 0.95,
      upperBound: 1,
      value: 1,
    );
  }

  @override
  void dispose() {
    _pressController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaleTransition(
      scale: _pressController,
      child: InkWell(
        borderRadius: BorderRadius.circular(48),
        onTap: widget.onPressed,
        onTapDown: (_) => _pressController.reverse(),
        onTapCancel: () => _pressController.forward(),
        onTapUp: (_) => _pressController.forward(),
        child: Container(
          width: 96,
          height: 96,
          decoration: const BoxDecoration(
            shape: BoxShape.circle,
            color: Color(0xFFF0F1F4),
          ),
          child: const Icon(Icons.close_rounded,
              size: 30, color: Color(0xFF5E6168)),
        ),
      ),
    );
  }
}

class _FloatCommandButton extends StatelessWidget {
  const _FloatCommandButton({required this.animation});

  final Animation<double> animation;

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: animation,
      builder: (context, child) {
        final value = animation.value;
        return Opacity(
          opacity: value,
          child: Transform.translate(
            offset: Offset(0, (1 - value) * 60),
            child: child,
          ),
        );
      },
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(22),
          boxShadow: const [
            BoxShadow(
              color: Color(0x18000000),
              blurRadius: 12,
              offset: Offset(0, 6),
            ),
          ],
        ),
        child: Row(
          children: const [
            Icon(Icons.lock_outline, size: 18, color: Color(0xFF2D5BFF)),
            SizedBox(width: 8),
            Text(
              '使用口令',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w600,
                color: Color(0xFF1D1E22),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _MenuItemData {
  const _MenuItemData({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.color,
  });

  final String title;
  final String subtitle;
  final IconData icon;
  final Color color;
}
