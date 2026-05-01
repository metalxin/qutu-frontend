import 'package:flutter/material.dart';
import '../models/story_item.dart';

const List<StoryItem> storyItems = [
  StoryItem(
    id: 'story-1',
    title: '北京故宫一日漫游',
    location: '北京·故宫',
    date: '2026-02-21',
    weekday: '周六',
    content:
        '清晨入宫，沿着中轴线慢慢走，午后在御花园里晒太阳，感受这座古老宫城的庄重与宁静。',
    gradientColors: [Color(0xFFB45A34), Color(0xFFF1C38C)],
  ),
  StoryItem(
    id: 'story-2',
    title: '南京秦淮夜色',
    location: '南京·秦淮河',
    date: '2026-02-19',
    weekday: '周四',
    content:
        '夜晚的秦淮河热闹而柔和，灯影在水面摇晃，沿河小吃很有烟火气。',
    gradientColors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
  ),
  StoryItem(
    id: 'story-3',
    title: '西湖日落',
    location: '杭州·西湖',
    date: '2026-02-16',
    weekday: '周一',
    content:
        '夕阳下的西湖像一幅画，微风吹起水面的粼光，整个人都被治愈。',
    gradientColors: [Color(0xFF5B4E99), Color(0xFFB6A7F5)],
  ),
];
