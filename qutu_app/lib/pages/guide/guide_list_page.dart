import 'package:flutter/material.dart';
import '../../data/guide_data.dart';
import '../../models/guide_item.dart';
import '../../utils/navigation.dart';
import '../../widgets/guide_search_bar.dart';
import 'guide_detail_page.dart';

class GuideListPage extends StatefulWidget {
  const GuideListPage({super.key});

  @override
  State<GuideListPage> createState() => _GuideListPageState();
}

class _GuideListPageState extends State<GuideListPage> {
  int _selectedCategory = 0;

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
          '精选攻略',
          style: TextStyle(
            color: Color(0xFF1D1E22),
            fontWeight: FontWeight.w600,
          ),
        ),
      ),
      body: Column(
        children: [
          const SizedBox(height: 12),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 20),
            child: GuideSearchBar(
              hintText: '搜索目的地/关键词',
            ),
          ),
          const SizedBox(height: 12),
          SizedBox(
            height: 40,
            child: ListView.separated(
              padding: const EdgeInsets.symmetric(horizontal: 16),
              scrollDirection: Axis.horizontal,
              physics: const BouncingScrollPhysics(),
              itemBuilder: (context, index) {
                final label = guideCategories[index];
                final selected = index == _selectedCategory;
                return ChoiceChip(
                  label: Text(label),
                  selected: selected,
                  selectedColor: const Color(0xFFE8F7EF),
                  backgroundColor: const Color(0xFFF0F2F6),
                  side: BorderSide(
                    color: selected
                        ? const Color(0xFF34C471)
                        : const Color(0xFFE0E3EA),
                  ),
                  labelStyle: TextStyle(
                    color: selected
                        ? const Color(0xFF34C471)
                        : const Color(0xFF5E6168),
                    fontWeight: FontWeight.w600,
                  ),
                  onSelected: (_) {
                    setState(() {
                      _selectedCategory = index;
                    });
                  },
                );
              },
              separatorBuilder: (_, _) => const SizedBox(width: 10),
              itemCount: guideCategories.length,
            ),
          ),
          const SizedBox(height: 12),
          Expanded(
            child: ListView.separated(
              padding: const EdgeInsets.fromLTRB(20, 0, 20, 20),
              physics: const BouncingScrollPhysics(),
              itemBuilder: (context, index) {
                final item = guideItems[index];
                return GuideCard(
                  item: item,
                  onTap: () => pushPage(
                    context,
                    GuideDetailPage(item: item),
                  ),
                );
              },
              separatorBuilder: (_, _) => const SizedBox(height: 16),
              itemCount: guideItems.length,
            ),
          ),
        ],
      ),
    );
  }
}

class GuideCard extends StatelessWidget {
  const GuideCard({super.key, required this.item, required this.onTap});

  final GuideItem item;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      borderRadius: BorderRadius.circular(20),
      onTap: onTap,
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: const [
            BoxShadow(
              color: Color(0x14000000),
              blurRadius: 10,
              offset: Offset(0, 6),
            ),
          ],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Hero(
              tag: item.id,
              child: ClipRRect(
                borderRadius:
                    const BorderRadius.vertical(top: Radius.circular(20)),
                child: Container(
                  height: 180,
                  width: double.infinity,
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                      colors: item.gradientColors,
                    ),
                  ),
                  child: const Center(
                    child: Icon(
                      Icons.photo_camera_outlined,
                      color: Colors.white70,
                      size: 36,
                    ),
                  ),
                ),
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 14, 16, 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    item.title,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    item.subtitle,
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
      ),
    );
  }
}
