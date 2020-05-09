package com.example.googlepaydemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoCustomCommandCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoRoomCallback;
import com.zego.zegoliveroom.callback.im.IZegoIMCallback;
import com.zego.zegoliveroom.callback.im.IZegoRoomMessageCallback;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;
import com.zego.zegoliveroom.entity.ZegoUser;
import com.zego.zegoliveroom.entity.ZegoUserState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingClientStateListener, PurchasesUpdatedListener, AcknowledgePurchaseResponseListener {

    BillingClient billingClient;
    private Button btn_bug, btn_great_room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_bug = btn_bug.findViewById(R.id.btn_bug);
        btn_great_room = btn_great_room.findViewById(R.id.btn_great_room);

        //1、初始化  PurchasesUpdatedListener 的引用，以接收通过您的应用以及 Google Play 商店发起的购买交易的更新。
        billingClient = BillingClient.newBuilder(getApplication()).setListener(this).build();

        //2、链接Google Play Service   实现BillingClientStateListener
        billingClient.startConnection(this);

        //购买按钮
        btn_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //skuDetails  通过调用querySkuDetailsAsync（）检索“ skuDetails”的值。
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                BillingResult responseCode = billingClient.launchBillingFlow(getParent(), flowParams);


            }
        });


        //登录 房间
        btn_great_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZegoLiveRoom.setUser("888", "suxiting");
            }
        });

        ZegoLiveRoom zegoLiveRoom = new ZegoLiveRoom();
        zegoLiveRoom.setZegoRoomCallback(new IZegoRoomCallback() {

            @Override
            public void onKickOut(int i, String s, String s1) {

            }

            @Override
            public void onDisconnect(int i, String s) {

            }

            @Override
            public void onReconnect(int i, String s) {

            }

            @Override
            public void onTempBroken(int i, String s) {

            }

            @Override
            public void onStreamUpdated(int i, ZegoStreamInfo[] zegoStreamInfos, String s) {

            }

            @Override
            public void onStreamExtraInfoUpdated(ZegoStreamInfo[] zegoStreamInfos, String s) {

            }

            @Override
            public void onRecvCustomCommand(String s, String s1, String s2, String s3) {

            }
        });
        //登录房间
        zegoLiveRoom.loginRoom("roomID", 1, new IZegoLoginCompletionCallback() {

            @Override
            public void onLoginCompletion(int stateCode, ZegoStreamInfo[] zegoStreamInfos) {
                // zegoStreamInfos，内部封装了 userID、userName、streamID 和 extraInfo。
                // 登录房间成功后，开发者可通过 zegoStreamInfos 获取到当前房间推流信息，便于后续的拉流操作。
                // 当 listStream 为 null 时说明当前房间没有人推流
                if (stateCode == 0) {
                    Log.i("登录房间成功 roomId : %s", "roomID");
                } else {
                    // 登录房间失败请查看 登录房间错误码，如果错误码是网络问题相关的，App 提示用户稍后再试，或者 App 内部重试登录。
                    Log.i("登录房间失败, stateCode : %d", stateCode + "");
                }
            }
        });

        /**
         * 创建会话前，开发者需要调用此 API 设置房间配置。
         *
         * 设置房间配置信息。
         * 注意：必须在 {@link #loginRoom(String, int, IZegoLoginCompletionCallback)} or
         * {@link #loginRoom(String, String, int, IZegoLoginCompletionCallback)} 之前调用。
         *
         * @param audienceCreateRoom 观众是否可以创建房间：true: 可以，false: 不可以。默认值为 true
         * @param userStateUpdate    用户状态（进入/退出房间））是否广播：
         *                           true:  房间内用户状态改变时，其他用户会收到 {@link IZegoIMCallback#onUserUpdate} 回调；
         *                           false: 房间内用户状态改变时，其他用户不会收到 {@link IZegoIMCallback#onUserUpdate} 回调
         */
        zegoLiveRoom.setRoomConfig(true,true);


        zegoLiveRoom.send




    }

    @Override
    public void onBillingSetupFinished(BillingResult billingResult) {
        // 连接成功
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            // The billing client is ready. You can query purchases here.

            // 5. 查询商品详情
            getProduct();
            // 6. 支付商品

        } else {
            // TODO 连接失败
        }
    }

    @Override
    public void onBillingServiceDisconnected() {
        // 连接断开 逻辑处理
        /**
         * 替换 onBillingServiceDisconnected() 回调方法并实现您自己的重试政策，
         * 以便在客户端丢失连接时处理 Google Play 连接丢失的问题
         *
         * 如果 Google Play 商店服务正在后台更新，那么 BillingClient 可能会失去连接。
         * 在发送进一步的请求之前，BillingClient 必须先调用 startConnection() 方法以重启连接。
         *
         * 注意：
         * 强烈建议您实现自己的连接重试政策并替换 onBillingServiceDisconnected() 方法。
         * 请确保在执行任何方法时都与 BillingClient 保持连接。
         */
    }


    //5、获取商品详情
    /**
     * 商品信息需要将带有内购权限的apk上传到GooglePlayConsole后，
     * 添加内购商品，设置商品ID，待商品生效后，
     * 移动端通过商品ID来查询商品的详细信息。
     */
    private String premiumUpgradePrice, gasPrice;
    private SkuDetails skuDetails;

    private void getProduct() {
        List<String> skuList = new ArrayList<>();
        skuList.add("premium_upgrade");
        skuList.add("gas");
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
        billingClient.querySkuDetailsAsync(params.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                // Process the result.
                // billingResult.getResponseCode() 为响应码
                // skuDetailsList 为查询的商品信息列表
                //检索应用内商品的价格
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                    //Google Play 结算库会将查询结果存储在 SkuDetails 对象的 List 中
                    for (SkuDetails skuDetails : skuDetailsList) {
                        String sku = skuDetails.getSku();
                        String price = skuDetails.getPrice();
                        if ("premium_upgrade".equals(sku)) {
                            premiumUpgradePrice = price;
                        } else if ("gas".equals(sku)) {
                            gasPrice = price;
                        }
                    }
                } else {
                    //发生错误，您可以使用 getDebugMessage() 查看相关的错误消息。
                    billingResult.getDebugMessage();
                }
            }
        });
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
            // TODO 支付完成
            for (Purchase purchase : list) {
                handlePurchase(purchase);

                //消耗商品
                consumableGoods(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            // TODO 用户取消了支付
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            // Handle an error caused by a user cancelling the purchase flow.
            // TODO 商品已经购买过（重复购买了此商品，如果需要支持重复购买，需要将商品购买成功后消费掉）
        } else {
            // Handle any other error codes.
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            // Acknowledge the purchase if it hasn't already been acknowledged.
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
            }
        }
    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

    }

    //商品的消耗 请求
    private void consumableGoods(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder()
//                .setPurchaseToken(/* token */)
//                .setDeveloperPayload(/* payload */)
                .setPurchaseToken(purchase.getPurchaseToken())
                .setDeveloperPayload(purchase.getDeveloperPayload())
                .build();
        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String outToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    // For example, increase the number of coins inside the user's basket.
                }
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    /**
     * 送礼物   发送消息
     * <p>
     * 发送房间内广播消息。
     *
     * <p>发送成功后，房间内其他成员会通过 {@link IZegoIMCallback#onRecvRoomMessage(String, ZegoRoomMessage[])} 接收此消息</p>
     *
     * @param messageType     消息类型，   详见 {@link com.zego.zegoliveroom.constants.ZegoIM.MessageType}
     * @param messageCategory 消息分类，   详见 {@link com.zego.zegoliveroom.constants.ZegoIM.MessageCategory}
     * @param content         消息内容，长度 <= 512 bytes 的可打印字符串
     * @param callback        实现 {@link IZegoRoomMessageCallback} 接口的对象实例，用于接收消息发送结果及 server 下发的 messageID
     * @return true:调用成功 等待 {@link IZegoRoomMessageCallback#onSendRoomMessage(int, String, long)} 返回, false:调用失败
     * @attentin 此 API 调用频率默认是 600次/min，若需要更大的发送频率可联系 Zego 技术支持进行配置
     */
    public boolean sendRoomMessage(int messageType, int messageCategory, String content, IZegoRoomMessageCallback callback) {
        return ZGJoinLiveHelper.sharedInstance().getZegoLiveRoom().sendRoomMessage(messageType, 4, content, new IZegoRoomMessageCallback() {
            @Override
            public void onSendRoomMessage(int i, String s, long l) {

            }
        });
    }


    // 设置 SDK 相关的回调监听
    public void initSDKCallback() {

        /**
         * 收到房间的广播消息。
         *
         * @param roomID        房间 ID
         * @param listMsg       消息列表, 每条消息都将包含消息内容，消息分类，消息类型，发送者等信息
         * @see com.zego.zegoliveroom.ZegoLiveRoom#sendRoomMessage(int, int, String, IZegoRoomMessageCallback)
         */
        ZGJoinLiveHelper.sharedInstance().getZegoLiveRoom().setZegoIMCallback(new IZegoIMCallback() {
            @Override
            public void onUserUpdate(ZegoUserState[] zegoUserStates, int i) {

            }

            @Override
            public void onRecvRoomMessage(String s, ZegoRoomMessage[] zegoRoomMessages) {
                //发送成功后 房间内其他成员会通过 {@link IZegoIMCallback#onRecvRoomMessage(String, ZegoRoomMessage[])} 接收此消息</p>


            }

            @Override
            public void onUpdateOnlineCount(String s, int i) {

            }

            @Override
            public void onRecvBigRoomMessage(String s, ZegoBigRoomMessage[] zegoBigRoomMessages) {

            }
        });
    }


    /**
     * 在房间中创建一个会话。
     * 要想观众可以创建房间，必须在登录前调用 {@link #setRoomConfig(boolean, boolean)} 进行设置。
     *
     * @param conversationName     会话名称，长度 <= 255 bytes 的可打印字符串
     * @param listMember           参与会话的成员列表，所有成员必须在同一个房间内
     * @param callback             实现 {@link IZegoCreateConversationCallback} 接口的对象实例，
     *                             用于接收创建会话结果及 server 下发的会话 ID
     * @return                     true: 调用成功，等待 {@link IZegoCreateConversationCallback#onCreateConversation(int, String, String)} 返回；
     *                             false: 调用失败
     *
     * @see #setRoomConfig(boolean, boolean)
     */
    public boolean createConversation(String conversationName, ZegoUser[] listMember,IZegoCustomCommandCallback callback){
        return ZGJoinLiveHelper.sharedInstance().getZegoLiveRoom().sendCustomCommand(listMember,conversationName, new IZegoCustomCommandCallback(){

            @Override
            public void onSendCustomCommand(int i, String s) {

            }
        });
    }


    /**
     * 在会话中发送一条消息。
     * 消息发送成功后，参与会话的成员列表会通过 {@link IZegoIMCallback#onRecvConversationMessage(String, String, ZegoConversationMessage)} 收到此消息
     * 注意：发送会话消息前，必须调用 {@link #createConversation(String, ZegoUser[], IZegoCreateConversationCallback)} 成功创建会话
     *
     * @param messageType       消息类型， 详见 {@link com.zego.zegoliveroom.constants.ZegoIM.MessageType}
     * @param conversationID    会话 ID，由 server 下发。在 {@link #createConversation(String, ZegoUser[], IZegoCreateConversationCallback)}
     *                          的回调 {@link IZegoCreateConversationCallback#onCreateConversation(int, String, String)} 中获得
     * @param content           消息内容，长度 <= 1024 bytes 的可打印字符串
     * @param callback          实现 {@link IZegoConversationMessageCallback} 接口的对象实例，用于接收发送消息结果及 server 下发的 messageID
     * @return                  true:调用成功，等待 {@link IZegoConversationMessageCallback#onSendConversationMessage(int, String, String, long)} 回调；false:调用失败
     *
     * @see IZegoIMCallback#onRecvConversationMessage(String, String, ZegoConversationMessage)
     */
    public boolean sendConversationMessage(int messageType, String conversationID, String content, IZegoRoomMessageCallback callback){
        return ZGJoinLiveHelper.sharedInstance().getZegoLiveRoom().sendRoomMessage(messageType, 4, content, new IZegoRoomMessageCallback() {
            @Override
            public void onSendRoomMessage(int i, String s, long l) {

            }
        });
    }





}
