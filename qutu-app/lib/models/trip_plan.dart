class AIRoute {
  final int? id;
  final String? name;
  final String? startCity;
  final String? endCity;
  final int? days;
  final String? distance;
  final String? totalTime;
  final List<RouteSpot>? spots;
  final List<DaySchedule>? schedule;
  final RouteSummary? summary;

  const AIRoute({
    this.id,
    this.name,
    this.startCity,
    this.endCity,
    this.days,
    this.distance,
    this.totalTime,
    this.spots,
    this.schedule,
    this.summary,
  });

  factory AIRoute.fromJson(Map<String, dynamic> json) {
    return AIRoute(
      id: json['id'] as int?,
      name: json['name'] as String?,
      startCity: json['startCity'] as String?,
      endCity: json['endCity'] as String?,
      days: json['days'] as int?,
      distance: json['distance'] as String?,
      totalTime: json['totalTime'] as String?,
      spots: (json['spots'] as List<dynamic>?)
          ?.map((e) => RouteSpot.fromJson(e as Map<String, dynamic>))
          .toList(),
      schedule: (json['schedule'] as List<dynamic>?)
          ?.map((e) => DaySchedule.fromJson(e as Map<String, dynamic>))
          .toList(),
      summary: json['summary'] != null
          ? RouteSummary.fromJson(json['summary'] as Map<String, dynamic>)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'startCity': startCity,
      'endCity': endCity,
      'days': days,
      'distance': distance,
      'totalTime': totalTime,
      'spots': spots?.map((e) => e.toJson()).toList(),
      'schedule': schedule?.map((e) => e.toJson()).toList(),
      'summary': summary?.toJson(),
    };
  }
}

class RouteSpot {
  final int? id;
  final String? name;
  final int? day;
  final int? order;
  final String? duration;
  final String? startTime;
  final String? endTime;
  final SpotInfo? spotInfo;

  const RouteSpot({
    this.id,
    this.name,
    this.day,
    this.order,
    this.duration,
    this.startTime,
    this.endTime,
    this.spotInfo,
  });

  factory RouteSpot.fromJson(Map<String, dynamic> json) {
    return RouteSpot(
      id: json['id'] as int?,
      name: json['name'] as String?,
      day: json['day'] as int?,
      order: json['order'] as int?,
      duration: json['duration'] as String?,
      startTime: json['startTime'] as String?,
      endTime: json['endTime'] as String?,
      spotInfo: json['spotInfo'] != null
          ? SpotInfo.fromJson(json['spotInfo'] as Map<String, dynamic>)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'day': day,
      'order': order,
      'duration': duration,
      'startTime': startTime,
      'endTime': endTime,
      'spotInfo': spotInfo?.toJson(),
    };
  }
}

class SpotInfo {
  final int? id;
  final String? name;
  final double? rating;
  final String? duration;
  final String? price;
  final String? category;
  final String? description;
  final String? tips;
  final String? openTime;
  final String? address;
  final double? latitude;
  final double? longitude;

  const SpotInfo({
    this.id,
    this.name,
    this.rating,
    this.duration,
    this.price,
    this.category,
    this.description,
    this.tips,
    this.openTime,
    this.address,
    this.latitude,
    this.longitude,
  });

  factory SpotInfo.fromJson(Map<String, dynamic> json) {
    return SpotInfo(
      id: json['id'] as int?,
      name: json['name'] as String?,
      rating: (json['rating'] as num?)?.toDouble(),
      duration: json['duration'] as String?,
      price: json['price'] as String?,
      category: json['category'] as String?,
      description: json['description'] as String?,
      tips: json['tips'] as String?,
      openTime: json['openTime'] as String?,
      address: json['address'] as String?,
      latitude: (json['latitude'] as num?)?.toDouble(),
      longitude: (json['longitude'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'rating': rating,
      'duration': duration,
      'price': price,
      'category': category,
      'description': description,
      'tips': tips,
      'openTime': openTime,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
}

class DaySchedule {
  final int? day;
  final String? date;
  final List<RouteSpot>? spots;
  final String? totalDuration;
  final String? totalDistance;
  final MealsInfo? meals;
  final HotelInfo? hotel;

  const DaySchedule({
    this.day,
    this.date,
    this.spots,
    this.totalDuration,
    this.totalDistance,
    this.meals,
    this.hotel,
  });

  factory DaySchedule.fromJson(Map<String, dynamic> json) {
    return DaySchedule(
      day: json['day'] as int?,
      date: json['date'] as String?,
      spots: (json['spots'] as List<dynamic>?)
          ?.map((e) => RouteSpot.fromJson(e as Map<String, dynamic>))
          .toList(),
      totalDuration: json['totalDuration'] as String?,
      totalDistance: json['totalDistance'] as String?,
      meals: json['meals'] != null
          ? MealsInfo.fromJson(json['meals'] as Map<String, dynamic>)
          : null,
      hotel: json['hotel'] != null
          ? HotelInfo.fromJson(json['hotel'] as Map<String, dynamic>)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'day': day,
      'date': date,
      'spots': spots?.map((e) => e.toJson()).toList(),
      'totalDuration': totalDuration,
      'totalDistance': totalDistance,
      'meals': meals?.toJson(),
      'hotel': hotel?.toJson(),
    };
  }
}

class MealsInfo {
  final MealInfo? lunch;
  final MealInfo? dinner;

  const MealsInfo({this.lunch, this.dinner});

  factory MealsInfo.fromJson(Map<String, dynamic> json) {
    return MealsInfo(
      lunch: json['lunch'] != null
          ? MealInfo.fromJson(json['lunch'] as Map<String, dynamic>)
          : null,
      dinner: json['dinner'] != null
          ? MealInfo.fromJson(json['dinner'] as Map<String, dynamic>)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'lunch': lunch?.toJson(),
      'dinner': dinner?.toJson(),
    };
  }
}

class MealInfo {
  final String? name;
  final String? address;

  const MealInfo({this.name, this.address});

  factory MealInfo.fromJson(Map<String, dynamic> json) {
    return MealInfo(
      name: json['name'] as String?,
      address: json['address'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'address': address,
    };
  }
}

class HotelInfo {
  final String? name;
  final String? price;

  const HotelInfo({this.name, this.price});

  factory HotelInfo.fromJson(Map<String, dynamic> json) {
    return HotelInfo(
      name: json['name'] as String?,
      price: json['price'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'price': price,
    };
  }
}

class RouteSummary {
  final int? totalSpots;
  final String? totalDistance;
  final String? estimatedCost;
  final String? bestSeason;

  const RouteSummary({
    this.totalSpots,
    this.totalDistance,
    this.estimatedCost,
    this.bestSeason,
  });

  factory RouteSummary.fromJson(Map<String, dynamic> json) {
    return RouteSummary(
      totalSpots: json['totalSpots'] as int?,
      totalDistance: json['totalDistance'] as String?,
      estimatedCost: json['estimatedCost'] as String?,
      bestSeason: json['bestSeason'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalSpots': totalSpots,
      'totalDistance': totalDistance,
      'estimatedCost': estimatedCost,
      'bestSeason': bestSeason,
    };
  }
}

class PlanningRequest {
  final String startLocation;
  final String endLocation;
  final List<String>? waypoints;
  final int days;
  final String preference;
  final String? transport;

  const PlanningRequest({
    required this.startLocation,
    required this.endLocation,
    this.waypoints,
    required this.days,
    required this.preference,
    this.transport,
  });

  Map<String, dynamic> toJson() {
    return {
      'startLocation': startLocation,
      'endLocation': endLocation,
      'waypoints': waypoints,
      'days': days,
      'preference': preference,
      'transport': transport,
    };
  }
}

class PreferenceOption {
  final String id;
  final String name;
  final String desc;

  const PreferenceOption({
    required this.id,
    required this.name,
    required this.desc,
  });

  factory PreferenceOption.fromJson(Map<String, dynamic> json) {
    return PreferenceOption(
      id: json['id'] as String,
      name: json['name'] as String,
      desc: json['desc'] as String,
    );
  }
}

class TransportMode {
  final String id;
  final String name;
  final String icon;

  const TransportMode({
    required this.id,
    required this.name,
    required this.icon,
  });

  factory TransportMode.fromJson(Map<String, dynamic> json) {
    return TransportMode(
      id: json['id'] as String,
      name: json['name'] as String,
      icon: json['icon'] as String,
    );
  }
}
