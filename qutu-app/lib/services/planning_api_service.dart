import '../models/trip_plan.dart';
import '../services/api_client.dart';

class PlanningApiService {
  final ApiClient _client = ApiClient.instance;

  Future<AIRoute> generateRoute(PlanningRequest request) async {
    final response = await _client.post(
      '/planning/planning/generate',
      data: request.toJson(),
    );
    return AIRoute.fromJson(response['data'] as Map<String, dynamic>);
  }

  Future<List<PreferenceOption>> getPreferenceOptions() async {
    final response = await _client.get('/planning/planning/preferences');
    final list = response['data'] as List<dynamic>;
    return list
        .map((e) => PreferenceOption.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<TransportMode>> getTransportModes() async {
    final response = await _client.get('/planning/planning/transports');
    final list = response['data'] as List<dynamic>;
    return list
        .map((e) => TransportMode.fromJson(e as Map<String, dynamic>))
        .toList();
  }

  Future<List<String>> getHotCities() async {
    final response = await _client.get('/planning/planning/cities');
    final list = response['data'] as List<dynamic>;
    return list.map((e) => e as String).toList();
  }

  Future<int> savePlan(AIRoute route) async {
    final response = await _client.post(
      '/planning/planning/save',
      data: route.toJson(),
    );
    return response['data'] as int;
  }
}
