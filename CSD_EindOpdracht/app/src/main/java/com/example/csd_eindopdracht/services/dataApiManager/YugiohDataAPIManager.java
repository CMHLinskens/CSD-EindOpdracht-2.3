package com.example.csd_eindopdracht.services.dataApiManager;

import com.example.csd_eindopdracht.util.Factory;

import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;

public class YugiohDataAPIManager extends DataApiManager {

    public YugiohDataAPIManager(Factory factory) {
        super(factory);
    }

    @Override
    public void getCardsWithLevel(int level, Callback callback) {
        final String url = "https://db.ygoprodeck.com/api/v7/cardinfo.php" +
                "?level=" + level;

        final Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    @Override
    public void getCardWithName(String name, Callback callback) {
        final String url = "https://db.ygoprodeck.com/api/v7/cardinfo.php" +
                "?name=" + name;

        final Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    @Override
    public void getCardsWithID(List<String> IDs, Callback callback) {
        final StringBuilder url = new StringBuilder("https://db.ygoprodeck.com/api/v7/cardinfo.php" +
                "?id=");
        for(String id : IDs){
            url.append(Integer.parseInt(id)).append(",");
        }
        url.deleteCharAt(url.length()-1);

        final Request request = new Request.Builder().url(url.toString()).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
