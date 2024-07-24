package com.stu.minote.http;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.lang.reflect.Type;

public class JsonHelper {
    public static Object JsonToObject(String str, Type T) {
        Gson gson = new Gson();
        Object t = gson.fromJson(str, T);

        return t;

    }

    public static JSONObject ToJsonObject(String str) {
        try {
            JSONTokener jsonParser = new JSONTokener(str);
            return (JSONObject) jsonParser.nextValue();
        } catch (Exception error) {
            return null;
        }

    }

    public static <T> String ObjectToString(T o) {
        try {
            Gson gson = new Gson();
            String t = gson.toJson(o, o.getClass());
            return t;
        } catch (Exception error) {
           // LogHelper.Log("对象序列化时出错：" + error.getMessage(), false);
            return null;
        }
    }
}
