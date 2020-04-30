package com.example.googlepaydemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingClientStateListener {

    BillingClient billingClient;
    private Button btn_bug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_bug.findViewById(R.id.btn_bug);

        //1、初始化  PurchasesUpdatedListener 的引用，以接收通过您的应用以及 Google Play 商店发起的购买交易的更新。
        billingClient = BillingClient.newBuilder(getApplication()).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                    // TODO 支付完成
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
        }).build();

        //2、链接Google Play Service   实现BillingClientStateListener
        billingClient.startConnection(this);

        //购买按钮
        btn_bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
     * */
    private String premiumUpgradePrice,gasPrice;

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
                }else {
                    //发生错误，您可以使用 getDebugMessage() 查看相关的错误消息。
                    billingResult.getDebugMessage();
                }
            }
        });
    }




}
