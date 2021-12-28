package fl.umeng

import android.content.Context
import android.os.Bundle
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.statistics.common.DeviceConfig
import com.umeng.umcrash.UMCrash
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


class UMengPlugin : FlutterPlugin, MethodChannel.MethodCallHandler {

    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(plugin: FlutterPluginBinding) {
        channel = MethodChannel(plugin.binaryMessenger, "UMeng")
        context = plugin.applicationContext
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "init" -> {
                val key = call.argument<String>("appKey")
                val channel = call.argument<String>("channel")
                val preInit = call.argument<Boolean>("preInit")
                if (preInit == true) {
                    UMConfigure.preInit(context, key, channel)
                } else {
                    UMConfigure.init(context, key, channel, UMConfigure.DEVICE_TYPE_PHONE, null)
                }
                result.success(true)
            }
            "getUMId" -> {
                result.success(
                    mapOf(
                        "umId" to UMConfigure.getUMIDString(context),
                        "umzId" to UMConfigure.getUmengZID(context),
                    )
                )
            }
            "getDeviceInfo" -> {
                result.success(
                    mapOf(
                        "deviceId" to DeviceConfig.getDeviceId(context),
                        "mac" to DeviceConfig.getMac(context),
                        "androidId" to DeviceConfig.getAndroidId(context),
                        "oaId" to DeviceConfig.getOaid(context),
                        "appHashKey" to DeviceConfig.getAppHashKey(context),
                        "appMD5Signature" to DeviceConfig.getAppMD5Signature(context),
                        "appName" to DeviceConfig.getAppName(context),
                        "appSHA1Key" to DeviceConfig.getAppSHA1Key(context),
                        "iPAddress" to DeviceConfig.getIPAddress(context),
                        "idfa" to DeviceConfig.getIdfa(context),
                        "imei" to DeviceConfig.getImei(context),
                        "imeiNew" to DeviceConfig.getImeiNew(context),
                        "imis" to DeviceConfig.getImsi(context),
                        "mccmnc" to DeviceConfig.getMCCMNC(context),
                        "meId" to DeviceConfig.getMeid(context),
                        "secondSimIMEi" to DeviceConfig.getSecondSimIMEi(context),
                        "simICCID" to DeviceConfig.getSimICCID(context),
                        "serial" to DeviceConfig.getSerial(),
                    )
                )
            }
            "setEncryptEnabled" -> {
                UMConfigure.setEncryptEnabled(call.arguments as Boolean)
                result.success(true)
            }
            "getTestDeviceInfo" -> result.success(DeviceConfig.getDeviceIdForGeneral(context))
            "setLogEnabled" -> {
                UMConfigure.setLogEnabled(call.arguments as Boolean)
                result.success(true)
            }
            "onEvent" -> {
                val event = call.argument<String>("event")
                val map = call.argument<Map<String, *>>("properties")
                MobclickAgent.onEventObject(context, event, map)
                result.success(true)
            }
            "onProfileSignIn" -> {
                val provider = call.argument<String?>("provider")
                val userID = call.argument<String>("userID")
                if (provider != null) {
                    MobclickAgent.onProfileSignIn(userID, provider)
                } else {
                    MobclickAgent.onProfileSignIn(userID)
                }
                result.success(true)
            }
            "onProfileSignOff" -> {
                MobclickAgent.onProfileSignOff()
                result.success(true)
            }
            "setPageCollectionModeAuto" -> {
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_AUTO)
                result.success(true)
            }
            "setPageCollectionModeManual" -> {
                MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.LEGACY_MANUAL)
                result.success(true)
            }
            "onPageStart" -> {
                MobclickAgent.onPageStart(call.arguments as String)
                result.success(true)
            }
            "onPageEnd" -> {
                MobclickAgent.onPageEnd(call.arguments as String)
                result.success(true)
            }
            "reportError" -> {
                MobclickAgent.reportError(context, call.arguments as String)
                result.success(true)
            }
            "onKillProcess" -> {
                MobclickAgent.onKillProcess(context)
                result.success(true)
            }
            "setAppVersion" -> {
                UMCrash.setAppVersion(
                    call.argument("version"),
                    call.argument("subVersion"),
                    call.argument("buildId")
                )
                result.success(true)
            }
            "setCrashConfig" -> {
                val bundle = Bundle()
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_CRASH_JAVA,
                    call.argument<Boolean>("enableJava") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_CRASH_NATIVE,
                    call.argument<Boolean>("enableNative") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_CRASH_UNEXP,
                    call.argument<Boolean>("enableUnExp") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_ANR,
                    call.argument<Boolean>("enableAnr") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_PA,
                    call.argument<Boolean>("enablePa") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_LAUNCH,
                    call.argument<Boolean>("enableLaunch") == true
                )
                bundle.putBoolean(
                    UMCrash.KEY_ENABLE_MEM,
                    call.argument<Boolean>("enableMEM") == true
                )
                UMCrash.initConfig(bundle)
                result.success(true)
            }
            "getUMAPMFlag" -> result.success(UMCrash.getUMAPMFlag())
            "setUMCrashDebug" -> {
                UMCrash.setDebug(call.arguments as Boolean)
                result.success(true)
            }
            "customLog" -> {
                UMCrash.generateCustomLog(call.argument<String>("key"), "type")
                result.success(true)
            }
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

}