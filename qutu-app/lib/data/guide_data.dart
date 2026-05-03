import 'package:flutter/material.dart';
import '../models/guide_item.dart';

const List<String> guideCategories = [
  'Citywalk',
  '海边',
  '美食指南',
  '去除班味',
];

const List<GuideItem> guideItems = [
  GuideItem(
    id: 'guide-1',
    title: '北京四天三夜自由行',
    subtitle: '超级丝滑的北京旅游攻略！四天三夜带你探索千年古都，领略北京的经典魅力与文化底蕴。',
    summary: '四天三夜的北京经典攻略',
    gradientColors: [Color(0xFFB45A34), Color(0xFFF1C38C)],
  ),
  GuideItem(
    id: 'guide-2',
    title: '北京两天一夜特种兵经典游',
    subtitle: '两天玩遍北京最具历史气息的景点',
    summary: '两天一夜高效打卡路线',
    gradientColors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
  ),
  GuideItem(
    id: 'guide-3',
    title: '南京深度三日游',
    subtitle: '穿梭六朝古都，慢品人文与风景',
    summary: '三日沉浸式文化路线',
    gradientColors: [Color(0xFF5B4E99), Color(0xFFB6A7F5)],
  ),
];
