import 'package:flutter/material.dart';
import '../../utils/navigation.dart';
import 'time_capture_edit_page.dart';

class TimeCapturePage extends StatefulWidget {
  const TimeCapturePage({super.key});

  @override
  State<TimeCapturePage> createState() => _TimeCapturePageState();
}

class _TimeCapturePageState extends State<TimeCapturePage> {
  bool _hasPermission = false;
  bool _isFlashOn = false;
  bool _isGridOn = false;
  int _modeIndex = 0;

  void _handleCapture() {
    if (!_hasPermission) {
      setState(() => _hasPermission = true);
      return;
    }
    pushPage(context, const TimeCaptureEditPage());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black,
      body: SafeArea(
        child: Stack(
          fit: StackFit.expand,
          children: [
            Column(
              children: [
                Padding(
                  padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
                  child: Row(
                    children: [
                      _CircleIconButton(
                        icon: Icons.close,
                        onTap: () => Navigator.of(context).pop(),
                      ),
                      const Spacer(),
                      _CircleIconButton(
                        icon: _isFlashOn ? Icons.flash_on : Icons.flash_off,
                        onTap: () => setState(() => _isFlashOn = !_isFlashOn),
                      ),
                      const SizedBox(width: 10),
                      _CircleIconButton(
                        icon: Icons.settings_outlined,
                        onTap: () {},
                      ),
                    ],
                  ),
                ),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.fromLTRB(20, 24, 20, 24),
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(24),
                      child: Container(
                        decoration: BoxDecoration(
                          gradient: LinearGradient(
                            begin: Alignment.topCenter,
                            end: Alignment.bottomCenter,
                            colors: [
                              Colors.black,
                              const Color(0xFF1C2330),
                              const Color(0xFF0B0F14),
                            ],
                          ),
                        ),
                        child: Stack(
                          fit: StackFit.expand,
                          children: [
                            if (_hasPermission)
                              Align(
                                alignment: Alignment.bottomCenter,
                                child: Container(
                                  height: 220,
                                  decoration: const BoxDecoration(
                                    gradient: LinearGradient(
                                      begin: Alignment.topCenter,
                                      end: Alignment.bottomCenter,
                                      colors: [
                                        Colors.transparent,
                                        Color(0xD9000000),
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                            if (_isGridOn)
                              const _GridOverlay(),
                            if (!_hasPermission)
                              Center(
                                child: Column(
                                  mainAxisSize: MainAxisSize.min,
                                  children: [
                                    const Icon(Icons.photo_camera_outlined,
                                        size: 64, color: Colors.white54),
                                    const SizedBox(height: 16),
                                    const Text(
                                      '相机权限未授权',
                                      style: TextStyle(
                                        color: Colors.white70,
                                        fontSize: 16,
                                        fontWeight: FontWeight.w600,
                                      ),
                                    ),
                                    const SizedBox(height: 18),
                                    SizedBox(
                                      width: 220,
                                      height: 44,
                                      child: ElevatedButton(
                                        onPressed: _handleCapture,
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor:
                                              const Color(0xFF2D7DFF),
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(22),
                                          ),
                                        ),
                                        child: const Text(
                                          '使用系统相机拍照',
                                          style: TextStyle(
                                              fontWeight: FontWeight.w600),
                                        ),
                                      ),
                                    ),
                                    const SizedBox(height: 12),
                                    SizedBox(
                                      width: 220,
                                      height: 44,
                                      child: ElevatedButton(
                                        onPressed: () => pushPage(
                                          context,
                                          const TimeCaptureEditPage(),
                                        ),
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor:
                                              const Color(0xFF3A3A3A),
                                          shape: RoundedRectangleBorder(
                                            borderRadius:
                                                BorderRadius.circular(22),
                                          ),
                                        ),
                                        child: const Text(
                                          '从相册选择',
                                          style: TextStyle(
                                              fontWeight: FontWeight.w600),
                                        ),
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
                _CaptureModeBar(
                  currentIndex: _modeIndex,
                  onChanged: (index) => setState(() => _modeIndex = index),
                ),
                Padding(
                  padding: const EdgeInsets.fromLTRB(24, 12, 24, 24),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      _CaptureOption(
                        icon: Icons.photo_outlined,
                        label: '',
                        onTap: () => pushPage(
                          context,
                          const TimeCaptureEditPage(),
                        ),
                      ),
                      _ShutterButton(onTap: _handleCapture),
                      _CaptureOption(
                        icon: Icons.cameraswitch_outlined,
                        label: '',
                        onTap: () {},
                        outlined: true,
                      ),
                    ],
                  ),
                ),
              ],
            ),
            Positioned(
              right: 16,
              bottom: 200,
              child: Column(
                children: [
                  _FloatingActionIcon(
                    icon: _isGridOn
                        ? Icons.grid_on_outlined
                        : Icons.grid_view_outlined,
                    onTap: () => setState(() => _isGridOn = !_isGridOn),
                  ),
                  const SizedBox(height: 12),
                  _FloatingActionIcon(
                    icon: Icons.autorenew_rounded,
                    onTap: () {},
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

class _CircleIconButton extends StatelessWidget {
  const _CircleIconButton({required this.icon, required this.onTap});

  final IconData icon;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 36,
      height: 36,
      decoration: const BoxDecoration(
        color: Color(0x33000000),
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onTap,
        child: Icon(icon, color: Colors.white, size: 18),
      ),
    );
  }
}

class _FloatingActionIcon extends StatelessWidget {
  const _FloatingActionIcon({required this.icon, required this.onTap});

  final IconData icon;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 44,
      height: 44,
      decoration: const BoxDecoration(
        color: Color(0xFFFAD2DE),
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onTap,
        child: Icon(icon, color: const Color(0xFFF56A8C), size: 22),
      ),
    );
  }
}

class _CaptureModeBar extends StatelessWidget {
  const _CaptureModeBar({
    required this.currentIndex,
    required this.onChanged,
  });

  final int currentIndex;
  final ValueChanged<int> onChanged;

  @override
  Widget build(BuildContext context) {
    final items = ['照片', '视频', '人像', '夜景'];
    return SizedBox(
      height: 34,
      child: ListView.separated(
        padding: const EdgeInsets.symmetric(horizontal: 32),
        scrollDirection: Axis.horizontal,
        itemBuilder: (context, index) {
          final selected = index == currentIndex;
          return GestureDetector(
            onTap: () => onChanged(index),
            child: Text(
              items[index],
              style: TextStyle(
                fontSize: 14,
                color: selected ? Colors.white : Colors.white54,
                fontWeight: selected ? FontWeight.w600 : FontWeight.w400,
              ),
            ),
          );
        },
        separatorBuilder: (_, _) => const SizedBox(width: 18),
        itemCount: items.length,
      ),
    );
  }
}

class _GridOverlay extends StatelessWidget {
  const _GridOverlay();

  @override
  Widget build(BuildContext context) {
    return LayoutBuilder(
      builder: (context, constraints) {
        return CustomPaint(
          painter: _GridPainter(),
          size: Size(constraints.maxWidth, constraints.maxHeight),
        );
      },
    );
  }
}

class _GridPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.white12
      ..strokeWidth = 1;
    final thirdWidth = size.width / 3;
    final thirdHeight = size.height / 3;
    for (var i = 1; i < 3; i++) {
      canvas.drawLine(
        Offset(thirdWidth * i, 0),
        Offset(thirdWidth * i, size.height),
        paint,
      );
      canvas.drawLine(
        Offset(0, thirdHeight * i),
        Offset(size.width, thirdHeight * i),
        paint,
      );
    }
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}

class _CaptureOption extends StatelessWidget {
  const _CaptureOption({
    required this.icon,
    required this.label,
    required this.onTap,
    this.outlined = false,
  });

  final IconData icon;
  final String label;
  final VoidCallback onTap;
  final bool outlined;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Ink(
          width: 54,
          height: 54,
          decoration: BoxDecoration(
            color: outlined ? Colors.transparent : const Color(0xFF1F1F1F),
            borderRadius: BorderRadius.circular(16),
            border: outlined
                ? Border.all(color: const Color(0xFF5E5E5E), width: 2)
                : null,
          ),
          child: InkWell(
            borderRadius: BorderRadius.circular(16),
            onTap: onTap,
            child: Icon(icon, color: Colors.white70, size: 26),
          ),
        ),
        if (label.isNotEmpty) ...[
          const SizedBox(height: 6),
          Text(
            label,
            style: const TextStyle(color: Colors.white70, fontSize: 12),
          ),
        ],
      ],
    );
  }
}

class _ShutterButton extends StatelessWidget {
  const _ShutterButton({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Ink(
      width: 72,
      height: 72,
      decoration: const BoxDecoration(
        color: Colors.white,
        shape: BoxShape.circle,
      ),
      child: InkWell(
        customBorder: const CircleBorder(),
        onTap: onTap,
        child: Center(
          child: Container(
            width: 60,
            height: 60,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              border: Border.all(color: const Color(0xFFDDDDDD), width: 4),
            ),
          ),
        ),
      ),
    );
  }
}
