import 'package:flutter/material.dart';
import '../../data/story_data.dart';
import '../../models/story_item.dart';
import '../../utils/navigation.dart';
import 'story_detail_page.dart';

class TravelStoryPage extends StatelessWidget {
  const TravelStoryPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF6F7F9),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        centerTitle: true,
        automaticallyImplyLeading: true,
        iconTheme: const IconThemeData(color: Color(0xFF1D1E22)),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios_new, size: 18),
          onPressed: () => Navigator.of(context).pop(),
        ),
        title: const Text(
          '旅行故事',
          style: TextStyle(
            color: Color(0xFF1D1E22),
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      body: ListView(
        padding: const EdgeInsets.fromLTRB(20, 10, 20, 20),
        physics: const BouncingScrollPhysics(),
        children: [
          StoryIntroCard(
            date: '02/21',
            weekday: '周六',
            fullDate: '2026-02-21',
            onRecordTap: () => _showRecordDiarySheet(context),
          ),
          const SizedBox(height: 16),
          ...storyItems.map(
            (item) => Padding(
              padding: const EdgeInsets.only(bottom: 16),
              child: StoryCard(
                item: item,
                onTap: () => pushPage(context, StoryDetailPage(item: item)),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

void _showRecordDiarySheet(BuildContext context) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (context) {
      final bottomPadding = MediaQuery.of(context).padding.bottom;
      return Container(
        padding: EdgeInsets.fromLTRB(20, 16, 20, 16 + bottomPadding),
        decoration: const BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                const Expanded(
                  child: Text(
                    '记录旅行故事',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.w600,
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
            _RecordOption(
              icon: Icons.photo_camera_outlined,
              title: '上传旅行照片',
              subtitle: '用照片记录你的旅行故事',
            ),
            const SizedBox(height: 10),
            _RecordOption(
              icon: Icons.edit_note_outlined,
              title: '写一篇旅行日记',
              subtitle: '记录当天的心情和风景',
            ),
            const SizedBox(height: 10),
            _RecordOption(
              icon: Icons.location_on_outlined,
              title: '打卡地点',
              subtitle: '记录你去过的目的地',
            ),
          ],
        ),
      );
    },
  );
}

class StoryIntroCard extends StatelessWidget {
  const StoryIntroCard({
    super.key,
    required this.date,
    required this.weekday,
    required this.fullDate,
    required this.onRecordTap,
  });

  final String date;
  final String weekday;
  final String fullDate;
  final VoidCallback onRecordTap;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(24),
      ),
      child: Row(
        children: [
          Container(
            width: 64,
            height: 64,
            decoration: BoxDecoration(
              color: const Color(0xFFE8F7EF),
              borderRadius: BorderRadius.circular(18),
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(
                  date,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  weekday,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF5E6168),
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  '记录今天的旅行故事',
                  style: TextStyle(
                    fontSize: 15,
                    fontWeight: FontWeight.w600,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  fullDate,
                  style: const TextStyle(
                    fontSize: 12,
                    color: Color(0xFF8C9099),
                  ),
                ),
              ],
            ),
          ),
          ElevatedButton(
            onPressed: onRecordTap,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF1DB954),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(18),
              ),
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            ),
            child: const Text(
              '记录',
              style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600),
            ),
          ),
        ],
      ),
    );
  }
}

class StoryCard extends StatelessWidget {
  const StoryCard({super.key, required this.item, required this.onTap});

  final StoryItem item;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(22),
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(22),
        ),
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(18),
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
                  Text(
                    item.title,
                    style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    item.location,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Color(0xFF8C9099),
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    item.content,
                    maxLines: 2,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Color(0xFF5E6168),
                    ),
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

class _RecordOption extends StatelessWidget {
  const _RecordOption({
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
