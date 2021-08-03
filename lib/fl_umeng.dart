import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

class FlUMeng {
  factory FlUMeng() => _getInstance();

  FlUMeng._internal();

  static FlUMeng get instance => _getInstance();

  static FlUMeng? _instance;

  static FlUMeng _getInstance() {
    _instance ??= FlUMeng._internal();
    return _instance!;
  }

  final MethodChannel _channel = const MethodChannel('UMeng');

  /// 初始化
  Future<bool> init(
      {required String androidAppKey,
      required String iosAppKey,
      String channel = ''}) async {
    if (!_supportPlatform) return false;
    final bool? state = await _channel.invokeMethod<bool?>(
        'init', <String, String?>{
      'appKey': _isAndroid ? androidAppKey : iosAppKey,
      'channel': channel
    });
    return state ?? false;
  }

  /// 设置用户账号
  /// provider 账号来源。不能以下划线"_"开头，使用大写字母和数字标识，长度小于32 字节
  Future<bool> signIn(String userID, {String? provider}) async {
    if (!_supportPlatform) return false;
    final Map<String, String> map = <String, String>{'userID': userID};
    if (provider != null) map['provider'] = provider;
    final bool? state =
        await _channel.invokeMethod<bool?>('onProfileSignIn', map);
    return state ?? false;
  }

  /// 取消用户账号
  Future<bool> signOff() async {
    if (!_supportPlatform) return false;
    final bool? state = await _channel.invokeMethod<bool?>('onProfileSignOff');
    return state ?? false;
  }

  /// 发送自定义事件（目前属性值支持字符、整数、浮点、长整数，暂不支持NULL、布尔、MAP、数组）
  Future<bool> onEvent(String event, Map<String, dynamic> properties) async {
    if (!_supportPlatform) return false;
    final bool? state = await _channel.invokeMethod<bool?>(
        'onEvent', <String, dynamic>{'event': event, 'properties': properties});
    return state ?? false;
  }

  /// 如果需要使用页面统计，则先打开该设置
  Future<bool> setPageCollectionModeManual() async {
    if (!_supportPlatform) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('setPageCollectionModeManual');
    return state ?? false;
  }

  /// 进入页面统计
  Future<bool> onPageStart(String pageName) async {
    if (!_supportPlatform) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('onPageStart', pageName);
    return state ?? false;
  }

  /// 离开页面统计
  Future<bool> onPageEnd(String pageName) async {
    if (!_supportPlatform) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('onPageEnd', pageName);
    return state ?? false;
  }

  /// 如果不需要上述页面统计，在完成后可关闭该设置；如果没有用该功能可忽略；
  Future<bool> setPageCollectionModeAuto() async {
    if (!_supportPlatform) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('setPageCollectionModeAuto');
    return state ?? false;
  }

  /// 是否开启日志
  /// 仅支持android
  Future<bool> setLogEnabled(bool logEnabled) async {
    if (!_isAndroid) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('setLogEnabled', logEnabled);
    return state ?? false;
  }

  /// 错误上报
  /// 仅支持android
  Future<bool> reportError(String error) async {
    if (!_isAndroid) return false;
    final bool? state =
        await _channel.invokeMethod<bool?>('reportError', error);
    return state ?? false;
  }

  bool get _supportPlatform {
    if (!kIsWeb && (_isAndroid || _isIOS)) return true;
    print('Not support platform for $defaultTargetPlatform');
    return false;
  }

  bool get _isAndroid => defaultTargetPlatform == TargetPlatform.android;

  bool get _isIOS => defaultTargetPlatform == TargetPlatform.iOS;
}
