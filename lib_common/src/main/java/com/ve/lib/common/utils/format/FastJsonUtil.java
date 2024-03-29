package com.ve.lib.common.utils.format;

/**
 * @Author weiyi
 * @Date 2022/7/13
 * @Description current project lockit-android
 * Android fastjson封装工具
 * https://www.jianshu.com/p/52ba5efb031f
 */

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.JSON.parseObject;

public class FastJsonUtil {
    /**
     * 对象转化为json fastjson 使用方式
     *
     * @return
     */
    public static String objectToJson(Object object) {
        if (object == null) {
            return "";
        }
        try {
            return JSON.toJSONString(object);
        } catch (JSONException e) {
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * json转化为对象  fastjson 使用方式
     *
     * @return
     */
    public static <T> T jsonToObject(String jsonData, Class<T> clazz) {
        T t = null;
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            t = parseObject(jsonData, clazz);
        } catch (Exception e) {
        }
        return t;
    }

    public static JSONObject jsonToObject(String jsonData) {
        JSONObject t = null;
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        try {
            t = parseObject(jsonData);
        } catch (Exception e) {
        }
        return t;
    }

    /**
     * json转化为List<Person>或List<String>数据  fastjson 使用方式
     *
     * @param jsonData
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToArray(String jsonData, Class<T> clazz) {
        List<T> list = null;
        try {
            list = JSON.parseArray(jsonData, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * json转化为List  fastjson 使用方式
     */
    public static List jsonToList(String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        List arrayList = null;
        try {
            arrayList = parseObject(jsonData, new TypeReference<ArrayList>() {
            });
        } catch (Exception e) {
        }
        return arrayList;
    }


    /**
     * json转化为Map  fastjson 使用方式
     */
    public static Map jsonToMap(String jsonData) {
        if (TextUtils.isEmpty(jsonData)) {
            return null;
        }
        Map map = null;
        try {
            map = parseObject(jsonData, new TypeReference<Map>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}