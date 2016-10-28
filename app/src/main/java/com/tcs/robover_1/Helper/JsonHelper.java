package com.tcs.robover_1.Helper;

import com.google.gson.Gson;

/**
 * Created by 831961 on 10/7/2015.
 */
public class JsonHelper {

    public static <T> String serialize(Object jsonObject){
        Gson gson = new Gson();
        String j = gson.toJson(jsonObject);
        return j;
    }

    public static <T> Object deserialize(String jsonString, Class<T> objectClass){
        Gson gson = new Gson();
        Object object = gson.fromJson(jsonString,objectClass);
        return object;
    }
}
