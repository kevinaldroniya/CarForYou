package com.car.foryou.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.car.foryou.utils.IPaymuProperties;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class iPaymuHelper {

    private final IPaymuProperties iPaymuProperties;
    private final EncryptionHelper encryptionHelper;

    public iPaymuHelper(IPaymuProperties iPaymuProperties, EncryptionHelper encryptionHelper){
        this.iPaymuProperties = iPaymuProperties;
        this.encryptionHelper = encryptionHelper;
    }

//    public void doPayment(){
//        OkHttpClient client = new OkHttpClient().newBuilder().build();
//        JSONObject jsonBody = new JSONObject();
//
//        String product = "kadosoasd";
//        long price = 1_000_000L;
//        int qty = 1;
//
//        jsonBody.put("account", iPaymuProperties.getVirtualAccount());
//        jsonBody.put("product", product);
//        jsonBody.put("qty", qty);
//        jsonBody.put("price", price);
//        jsonBody.put("returnUrl", "");
//        jsonBody.put("notifyUrl", "");
//        jsonBody.put("cancelUrl", "");
//
//        try {
//            MediaType json = MediaType.parse("application/json; charset=utf-8");
//            RequestBody body = RequestBody.create(jsonBody.toJSONString(), json);
//            Request request = new Request.Builder().url(iPaymuProperties.getPaymentUrl()).post(body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("signature", encryptionHelper.getSignatureIPaymu(jsonBody.toJSONString()))
//                    .addHeader("va", iPaymuProperties.getVirtualAccount())
//                    .addHeader("timestamp", String.valueOf(new Date().getTime()))
//                    .build();
//            try (Response response = client. newCall(request).execute()) {
//                ResponseBody responseBody = response.body();
//                assert responseBody != null;
//                JSON resJson = JSON.parseObject(responseBody.toString());
//                System.out.println(resJson);
//                System.out.println(responseBody.toString());
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
