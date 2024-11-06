package com.car.foryou.helper;

import com.car.foryou.utils.IPaymuProperties;
import com.google.gson.JsonObject;
import net.minidev.json.JSONObject;
import okhttp3.*;
import org.springframework.stereotype.Component;

@Component
public class iPaymuHelper {

    private final IPaymuProperties iPaymuProperties;

    public iPaymuHelper(IPaymuProperties iPaymuProperties){
        this.iPaymuProperties = iPaymuProperties;
    }

    public void doPayment(){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        JSONObject jsonBody = new JSONObject();

        String product = "kadosoasd";
        long price = 1_000_000L;
        int qty = 1;

        jsonBody.put("account", iPaymuProperties.getVirtualAccount());
        jsonBody.put("product", product);
        jsonBody.put("qty", qty);
        jsonBody.put("price", price);
        jsonBody.put("returnUrl", "");
        jsonBody.put("notifyUrl", "");
        jsonBody.put("cancelUrl", "");

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody.toJSONString());
        Request request = new Request.Builder().url(iPaymuProperties.getPaymentUrl()).post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("signature")
    }
}
