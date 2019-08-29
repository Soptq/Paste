package soptqs.paste.utils;

import com.jayway.jsonpath.JsonPath;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import soptqs.paste.database.PhoneItem;

import static soptqs.paste.constants.RegexConstants.isColorCode;
import static soptqs.paste.constants.RegexConstants.isMobileSimple;

/**
 * Created by S0ptq on 2018/2/6.
 */

public class RecognitionUtils {

    final static String TAG = "RecognitionUtils";

    public static int isWhat(String content){
        if (isColorCode(content)) return 1;
        if (isMobileSimple(content)) return 2;
        return 0;
    }


    public static PhoneItem phoneRecog(String mphone){
        final String phone = mphone;
        Callable<PhoneItem> getInfo = new Callable<PhoneItem>() {
            @Override
            public PhoneItem call() throws Exception {
                PhoneItem phoneItem = null;
                final String key = "";
                String url;
                url = "http://apis.juhe.cn/mobile/get?phone="
                        + phone
                        +"&key="
                        + key;
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                    urlConnection.connect();
                    urlConnection.setConnectTimeout(2000);

                    BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) urlConnection.getContent()));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    String source = total.toString();
                    String province = JsonPath.parse(source).read("$['result']['province']");
                    String city = JsonPath.parse(source).read("$['result']['city']");
                    String serviceProvider = JsonPath.parse(source).read("$['result']['company']");
                    String errorCode = JsonPath.parse(source).read("$['resultcode']");
//                    Log.e(TAG, "onCreate: "+province );
//                    Log.e(TAG, "onCreate: "+city );
//                    Log.e(TAG, "onCreate: "+serviceProvider );
//                    Log.e(TAG, "onCreate: "+errorCode );
                    phoneItem = new PhoneItem(errorCode, province, city, serviceProvider);
                    urlConnection.disconnect();
                }catch (IOException e) {
                    e.printStackTrace();
                }
                return phoneItem;
            }
        };

        FutureTask<PhoneItem> task = new FutureTask<>(getInfo);
        new Thread(task).start();
        try
        {
            return task.get();
        } catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String getShortURL(String url) {
        final String longUrl = url;
        Callable<String> getInfo = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String baseurl;
                String shortURL = null;
                baseurl = "http://suo.im/api.php?format=json&url="
                        + longUrl;
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) new URL(baseurl).openConnection();
                    urlConnection.connect();
                    urlConnection.setConnectTimeout(2000);

                    BufferedReader r = new BufferedReader(new InputStreamReader((InputStream) urlConnection.getContent()));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line).append('\n');
                    }

                    String source = total.toString();
                    shortURL = JsonPath.parse(source).read("$.url");
                    urlConnection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return shortURL;
            }
        };

        FutureTask<String> task = new FutureTask<>(getInfo);
        new Thread(task).start();
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
