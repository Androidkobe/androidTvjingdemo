package com.partybuild.boxindex_cmcc;


import android.util.Base64;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class DeviceUNHelper {

    private final static String SDK_VERSION = "1.0.0";

    private final static boolean STAING_ABLE = true;

    private final static String STRING
            = "https://api-sys.test.shuzidangxiao.cn/index.php?con=api&ac=boxindex";
    private final static String ONLINE
            = "https://api-sys.shuzidangxiao.cn/index.php?con=api&ac=boxindex";

    private final static String KEY_PAR_SERVICE = "service";
    private final static String KEY_PAR_VERSION = "version";
    private final static String KEY_PAR_DATA = "endata";

    private final static String KEY_MAP_SN = "device_sn";
    private final static String KEY_MAP_MAC = "device_mac";
    private final static String KEY_MAP_WIFIMAC = "device_wifimac";
    private final static String KEY_MAP_TYPE = "device_type";
    private final static String KEY_MAP_GROUP = "device_group";
    private final static String KEY_MAP_MODEL = "device_model";

    private static int corePoolSize = Runtime.getRuntime().availableProcessors();

    private static int maximumPoolSize = corePoolSize * 2 + 1;

    private static long keepAliveTime = 5;

    private static TimeUnit unit = TimeUnit.MINUTES;

    private static BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();

    private static ThreadFactory threadFactory = Executors.defaultThreadFactory();

    private static RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime, unit,
            workQueue,
            threadFactory,
            handler);


    private static String getUrl() {
        return STAING_ABLE ? STRING : ONLINE;
    }

    private static Map<String, String> getParMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_PAR_SERVICE, "regDevice");
        map.put(KEY_PAR_VERSION, SDK_VERSION);
        map.put(KEY_PAR_DATA, getEnDataBase64());
        Log.i("DeviceUNHelper","map par: "+map.toString());
        return map;
    }

    private static String getEnDataBase64() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_MAP_SN, AndroidUtils.getSN());
            jsonObject.put(KEY_MAP_MODEL, AndroidUtils.getModel());
            jsonObject.put(KEY_MAP_MAC, MacUtils.getEthernetMac());
            jsonObject.put(KEY_MAP_WIFIMAC, MacUtils.getWifiMac());
            jsonObject.put(KEY_MAP_TYPE, "2");
            jsonObject.put(KEY_MAP_GROUP, "cmcc");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("DeviceUNHelper","data par: "+jsonObject.toString());
        return Base64.encodeToString(jsonObject.toString().getBytes(), Base64.DEFAULT);
    }

    public static void getDeviceUN(DeviceUnCallBack deviceUnCallBack) {
        executor.execute(() -> {
            String result =  HttpUtils.doHttpReqeust("POST", getUrl(), getParMap());
            Log.i("DeviceUNHelper","result : "+result);
            try {
                if(result != null && result.length() > 0){
                    JSONObject resultJson = new JSONObject(result);
                    if (resultJson.has("data")){
                        String data = resultJson.optString("data");
                        if(data != null && data.length() > 0) {
                            JSONObject dataJson = new JSONObject(data);
                            if (dataJson.has("device_un")) {
                                String un = dataJson.optString("device_un");
                                deviceUnCallBack.onSuccess(un);
                                return ;
                            }
                        }
                    }
                }
            }catch (Exception e){

            }
            deviceUnCallBack.onFailed();
        });
    }
}
