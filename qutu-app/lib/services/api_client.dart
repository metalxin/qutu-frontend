import 'package:dio/dio.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ApiClient {
  static ApiClient? _instance;
  late final Dio _dio;

  ApiClient._() {
    _dio = Dio(BaseOptions(
      baseUrl: 'http://localhost:9999',
      connectTimeout: const Duration(seconds: 30),
      receiveTimeout: const Duration(seconds: 60),
      headers: {
        'Content-Type': 'application/json',
      },
    ));

    _dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final prefs = await SharedPreferences.getInstance();
        final token = prefs.getString('access_token');
        if (token != null && token.isNotEmpty) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        handler.next(options);
      },
      onResponse: (response, handler) {
        handler.next(response);
      },
      onError: (error, handler) {
        handler.next(error);
      },
    ));
  }

  static ApiClient get instance {
    _instance ??= ApiClient._();
    return _instance!;
  }

  Dio get dio => _dio;

  Future<Map<String, dynamic>> get(String path,
      {Map<String, dynamic>? queryParameters}) async {
    final response = await _dio.get(path, queryParameters: queryParameters);
    return _handleResponse(response);
  }

  Future<Map<String, dynamic>> post(String path, {dynamic data}) async {
    final response = await _dio.post(path, data: data);
    return _handleResponse(response);
  }

  Future<Map<String, dynamic>> put(String path, {dynamic data}) async {
    final response = await _dio.put(path, data: data);
    return _handleResponse(response);
  }

  Future<Map<String, dynamic>> delete(String path,
      {Map<String, dynamic>? queryParameters}) async {
    final response =
        await _dio.delete(path, queryParameters: queryParameters);
    return _handleResponse(response);
  }

  Map<String, dynamic> _handleResponse(Response response) {
    final data = response.data;
    if (data is Map<String, dynamic>) {
      if (data['code'] == 0) {
        return data;
      } else {
        throw ApiException(
          code: data['code'] ?? -1,
          message: data['msg'] ?? '请求失败',
        );
      }
    }
    throw ApiException(code: -1, message: '响应格式异常');
  }
}

class ApiException implements Exception {
  final int code;
  final String message;

  ApiException({required this.code, required this.message});

  @override
  String toString() => 'ApiException: $code - $message';
}
