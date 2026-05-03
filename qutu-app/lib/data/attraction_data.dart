import 'package:flutter/material.dart';
import '../models/attraction_item.dart';

final Map<String, List<AttractionItem>> attractionData = {
  '北京': [
    const AttractionItem(
      name: '颐和园',
      tag: '皇家园林',
      rating: 4.8,
      address: '海淀区新建宫门路19号',
      time: '08:00-18:00',
      price: '￥30起',
      gradientColors: [Color(0xFFB45A34), Color(0xFFF1C38C)],
    ),
    const AttractionItem(
      name: '故宫',
      tag: '历史建筑',
      rating: 4.9,
      address: '东城区景山前街4号',
      time: '08:30-17:00',
      price: '￥60起',
      gradientColors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
    ),
    const AttractionItem(
      name: '什刹海',
      tag: '城市风光',
      rating: 4.7,
      address: '西城区地安门西大街49号',
      time: '全天开放',
      price: '免费',
      gradientColors: [Color(0xFF5B4E99), Color(0xFFB6A7F5)],
    ),
  ],
  '南京': [
    const AttractionItem(
      name: '中山陵',
      tag: '历史纪念',
      rating: 4.8,
      address: '玄武区石象路7号',
      time: '08:30-17:00',
      price: '免费',
      gradientColors: [Color(0xFF2E6D7A), Color(0xFFA9D9E9)],
    ),
    const AttractionItem(
      name: '夫子庙',
      tag: '古街风情',
      rating: 4.6,
      address: '秦淮区贡院街',
      time: '全天开放',
      price: '免费',
      gradientColors: [Color(0xFFB45A34), Color(0xFFF1C38C)],
    ),
    const AttractionItem(
      name: '明孝陵',
      tag: '世界遗产',
      rating: 4.7,
      address: '玄武区钟山风景区内',
      time: '08:00-17:30',
      price: '￥70起',
      gradientColors: [Color(0xFF5B4E99), Color(0xFFB6A7F5)],
    ),
  ],
};
