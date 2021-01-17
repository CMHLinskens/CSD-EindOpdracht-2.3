package com.example.csd_eindopdracht.services.dataApiManager;

import com.example.csd_eindopdracht.util.Factory;

import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;

public abstract class DataApiManager {
    protected String apiKey = "";
    protected OkHttpClient client;
    protected Factory factory;

    public DataApiManager(Factory factory) {
        client = new OkHttpClient();
        this.factory = factory;
    }

    public void getCardsWithLevel(int level, Callback callback){}

    public void getCardWithName(String name, Callback callback){}

    public void getCardsWithID(List<String> IDs, Callback callback) {}
}
