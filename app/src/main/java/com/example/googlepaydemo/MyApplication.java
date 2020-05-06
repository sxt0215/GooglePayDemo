package com.example.googlepaydemo;

import android.app.Application;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoInitSDKCompletionCallback;

public class MyApplication extends Application {


    public static final long appId = 2845609334L;
    // 需要强转成 byte 类型，原因: 在java中，整数默认是 int 类型
    public static final byte[] appSign = {
            (byte) 0x1a, (byte) 0xb5, (byte) 0xc2, (byte) 0xe5, (byte) 0x76,
            (byte) 0xe7, (byte) 0x4c, (byte) 0x51, (byte) 0xef, (byte) 0x02,
            (byte) 0x6d, (byte) 0x61, (byte) 0x21, (byte) 0x0e, (byte) 0x39,
            (byte) 0x7a, (byte) 0x51, (byte) 0x85, (byte) 0x58, (byte) 0xda,
            (byte) 0x7f, (byte) 0x2e, (byte) 0xc4, (byte) 0x94, (byte) 0xd7,
            (byte) 0xa4, (byte) 0xcd, (byte) 0x74, (byte) 0x81, (byte) 0x9a,
            (byte) 0xac, (byte) 0xb0};

    private ZegoLiveRoom g_ZegoApi;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化 SDK 之前
         *
         * 设置 SDK 上下文
         * */
        ZegoLiveRoom.setSDKContext(new ZegoLiveRoom.SDKContextEx() {

            @Override
            public long getLogFileSize() {
                return 0;  // 单个日志文件的大小，必须在 [5M, 100M] 之间；当返回 0 时，表示关闭写日志功能，不推荐关闭日志。
            }

            @Override
            public String getSubLogFolder() {
                return null;
            }

            @Override
            public String getSoFullPath() {

                return null; // return null 表示使用默认方式加载 libzegoliveroom.so
                // 此处可以返回 so 的绝对路径，用来指定从这个位置加载 libzegoliveroom.so，确保应用具备存取此路径的权限
            }

            @Override
            public String getLogPath() {
                return null; //  return null 表示日志文件会存储到默认位置，如果返回非空，则将日志文件存储到该路径下，注意应用必须具备存取该目录的权限
            }

            @Override
            public Application getAppContext() {
                return getAppContext(); // android上下文. 不能为null
            }
        });

        // 当 App 集成完成后，再向 ZEGO 申请正式环境。
        ZegoLiveRoom.setTestEnv(true); // 为 true 则说明开启测试环境，为 false 则说明使用正式环境


        /**
         * 初始化 SDK
         * */
        g_ZegoApi = new ZegoLiveRoom();
        /**
         * 1、初始化sdk, appID与appSign 开发者如果还没有申请, 可通过 <a>https://console.zego.im/acount/login</a> 申请 AppID
         * 2、AppID 和 AppSign 由 ZEGO 分配给各 App。其中，为了安全考虑，建议将 AppSign 存储在 App 的业务后台，需要使用时从后台获取
         * 3、如果不需要再继续使用 SDK 可调用 g_ZegoApi.unInitSDK() 释放SDK   注意：释放 SDK 后需要再使用 SDK 时，必须重新初始化 SDK。
         * */
        g_ZegoApi.initSDK(appId, appSign, new IZegoInitSDKCompletionCallback() {
            @Override
            public void onInitSDK(int errorCode) {
                // errorCode 非0 代表初始化sdk失败
                // 具体错误码说明请查看<a> https://doc.zego.im/CN/308.html </a>
                /**
                 * kConfigServerCouldntConnectError	21200007	无法连接配置服务器  请检查网络是否正常
                 * kConfigServerTimeoutError	    21200028	连接配置服务器超时  请检查网络是否正常
                 * kConfigDecryptError	            20000001	配置文件解密失败    请检查 AppID 和 AppSign 是否正确  无法解决请联系 ZEGO 技术支持解决
                 * kConfigDecryptError	            20000002	测试环境已过期，请重新申请
                 * */

            }
        });


    }
}
