import 'package:flutter/material.dart';

class TimeCaptureEditPage extends StatefulWidget {
  const TimeCaptureEditPage({super.key});

  @override
  State<TimeCaptureEditPage> createState() => _TimeCaptureEditPageState();
}

class _TimeCaptureEditPageState extends State<TimeCaptureEditPage> {
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _descController = TextEditingController();
  int _selectedGroup = 0;
  final List<String> _groups = [
    '城市记忆',
    '海边',
    '美食',
    '夜景',
    '建筑',
    '同行',
    '路线'
  ];
  final List<String> _tags = [
    '微风',
    '好天气',
    '周末',
    '旅拍',
    '日落'
  ];
  final Set<String> _selectedTags = {};

  @override
  void dispose() {
    _titleController.dispose();
    _descController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF6F7F9),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        centerTitle: true,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text(
          '编辑采集',
          style: TextStyle(
            color: Color(0xFF1D1E22),
            fontWeight: FontWeight.w600,
          ),
        ),
        actions: [
          TextButton(
            onPressed: () {
              FocusScope.of(context).unfocus();
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('已保存到草稿')),
              );
            },
            child: const Text(
              '保存',
              style: TextStyle(
                color: Color(0xFF2D7DFF),
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
        ],
      ),
      body: Stack(
        children: [
          ListView(
            padding: const EdgeInsets.fromLTRB(20, 12, 20, 24),
            physics: const BouncingScrollPhysics(),
            children: [
              _TitleCard(controller: _titleController),
              const SizedBox(height: 16),
              _LocationCard(),
              const SizedBox(height: 16),
              _GroupCard(
                groups: _groups,
                selectedIndex: _selectedGroup,
                onSelected: (index) => setState(() => _selectedGroup = index),
              ),
              const SizedBox(height: 16),
              _DescriptionCard(
                controller: _descController,
                onGenerate: () {
                  setState(() {
                    _descController.text =
                        '午后的光线刚好，空气里带着一点海风的湿润。沿着小路慢慢走，看着城市的边缘被夕阳染成温暖的颜色。';
                  });
                },
              ),
              const SizedBox(height: 16),
              _TagCard(
                tags: _tags,
                selectedTags: _selectedTags,
                onToggle: (tag) {
                  setState(() {
                    if (_selectedTags.contains(tag)) {
                      _selectedTags.remove(tag);
                    } else {
                      _selectedTags.add(tag);
                    }
                  });
                },
              ),
            ],
          ),
          Positioned(
            right: 16,
            top: 180,
            child: Column(
              children: [
                _FloatingActionIcon(
                  icon: Icons.grid_view_outlined,
                  onTap: () {},
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
    );
  }
}

class _TitleCard extends StatelessWidget {
  const _TitleCard({required this.controller});

  final TextEditingController controller;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          TextField(
            controller: controller,
            decoration: const InputDecoration(
              hintText: '输入标题...',
              border: InputBorder.none,
              isDense: true,
            ),
            style: const TextStyle(
              fontSize: 15,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          ClipRRect(
            borderRadius: BorderRadius.circular(16),
            child: Stack(
              children: [
                Container(
                  height: 180,
                  width: double.infinity,
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: [Color(0xFF19C0C8), Color(0xFF2D7DFF)],
                    ),
                  ),
                ),
                Positioned(
                  right: 12,
                  top: 12,
                  child: Container(
                    padding:
                        const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
                    decoration: BoxDecoration(
                      color: Colors.black45,
                      borderRadius: BorderRadius.circular(14),
                    ),
                    child: const Text(
                      '1/6',
                      style: TextStyle(color: Colors.white, fontSize: 11),
                    ),
                  ),
                ),
                const Center(
                  child: Icon(Icons.photo_camera_outlined,
                      color: Colors.white70, size: 36),
                ),
              ],
            ),
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              const Text(
                '02/23',
                style: TextStyle(
                  fontSize: 22,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const Spacer(),
              RotatedBox(
                quarterTurns: 1,
                child: Text(
                  DateTime.now().year.toString(),
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _LocationCard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 14),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '位置',
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Row(
            children: const [
              Icon(Icons.location_on_outlined, size: 16, color: Color(0xFFEA4335)),
              SizedBox(width: 6),
              Text(
                '添加位置',
                style: TextStyle(fontSize: 13, color: Color(0xFF1D1E22)),
              ),
              Spacer(),
              Icon(Icons.chevron_right, color: Color(0xFF8C9099)),
            ],
          ),
        ],
      ),
    );
  }
}

class _GroupCard extends StatelessWidget {
  const _GroupCard({
    required this.groups,
    required this.selectedIndex,
    required this.onSelected,
  });

  final List<String> groups;
  final int selectedIndex;
  final ValueChanged<int> onSelected;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '分组',
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: [
              ...groups.asMap().entries.map(
                    (entry) => ChoiceChip(
                      label: Text(entry.value),
                      selected: entry.key == selectedIndex,
                      selectedColor: const Color(0xFFE8F1FF),
                      backgroundColor: const Color(0xFFF1F2F6),
                      side: BorderSide(
                        color: entry.key == selectedIndex
                            ? const Color(0xFF2D7DFF)
                            : const Color(0xFFE0E3EA),
                      ),
                      labelStyle: TextStyle(
                        fontSize: 12,
                        color: entry.key == selectedIndex
                            ? const Color(0xFF2D7DFF)
                            : const Color(0xFF5E6168),
                        fontWeight: FontWeight.w600,
                      ),
                      onSelected: (_) => onSelected(entry.key),
                    ),
                  ),
              ActionChip(
                label: const Text('+ 新建'),
                onPressed: () {},
                labelStyle:
                    const TextStyle(fontSize: 12, color: Color(0xFF8C9099)),
                side: const BorderSide(color: Color(0xFFCED3DD)),
                backgroundColor: Colors.white,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _DescriptionCard extends StatelessWidget {
  const _DescriptionCard({
    required this.controller,
    required this.onGenerate,
  });

  final TextEditingController controller;
  final VoidCallback onGenerate;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Text(
                '描述',
                style: TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
              const Spacer(),
              InkWell(
                borderRadius: BorderRadius.circular(16),
                onTap: onGenerate,
                child: Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                  decoration: BoxDecoration(
                    color: const Color(0xFF7E6BFF),
                    borderRadius: BorderRadius.circular(16),
                  ),
                  child: Row(
                    children: const [
                      Text(
                        '✨',
                        style: TextStyle(fontSize: 12),
                      ),
                      SizedBox(width: 4),
                      Text(
                        'AI生成',
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.white,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 12),
          TextField(
            controller: controller,
            maxLines: 4,
            decoration: const InputDecoration(
              hintText: '添加描述...',
              border: InputBorder.none,
            ),
          ),
        ],
      ),
    );
  }
}

class _TagCard extends StatelessWidget {
  const _TagCard({
    required this.tags,
    required this.selectedTags,
    required this.onToggle,
  });

  final List<String> tags;
  final Set<String> selectedTags;
  final ValueChanged<String> onToggle;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 14, 16, 16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(18),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '标签',
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 10,
            runSpacing: 10,
            children: [
              ...tags.map(
                (tag) => FilterChip(
                  label: Text(tag),
                  selected: selectedTags.contains(tag),
                  selectedColor: const Color(0xFFE8F1FF),
                  backgroundColor: const Color(0xFFF1F2F6),
                  side: BorderSide(
                    color: selectedTags.contains(tag)
                        ? const Color(0xFF2D7DFF)
                        : const Color(0xFFE0E3EA),
                  ),
                  labelStyle: TextStyle(
                    fontSize: 12,
                    color: selectedTags.contains(tag)
                        ? const Color(0xFF2D7DFF)
                        : const Color(0xFF5E6168),
                  ),
                  onSelected: (_) => onToggle(tag),
                ),
              ),
              ActionChip(
                label: const Text('+ 添加标签'),
                onPressed: () {},
                labelStyle:
                    const TextStyle(fontSize: 12, color: Color(0xFF8C9099)),
                side: const BorderSide(color: Color(0xFFCED3DD)),
                backgroundColor: Colors.white,
              ),
            ],
          ),
        ],
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
