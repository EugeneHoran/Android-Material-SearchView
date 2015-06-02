package com.eugene.fithealth.FatSecretSearchAndGet;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.eugene.fithealth.Utilities.Globals;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FatSecretSearchItem {
    public JSONObject searchFood(String searchFood, int page) {
        Log.e("Search", searchFood);
        List<String> params = new ArrayList<>(Arrays.asList(generateOauthParams(page)));
        String[] template = new String[1];
        params.add("method=foods.search");
        params.add("search_expression=" + Uri.encode(searchFood));
        params.add("oauth_signature=" + sign(Globals.APP_METHOD, Globals.APP_URL, params.toArray(template)));
        JSONObject foods = null;
        try {
            URL url = new URL(Globals.APP_URL + "?" + paramify(params.toArray(template)));
            URLConnection api = url.openConnection();
            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(api.getInputStream()));
            while ((line = reader.readLine()) != null) builder.append(line);
            JSONObject food = new JSONObject(builder.toString());   // { first
            foods = food.getJSONObject("foods"); //second
        } catch (Exception exception) {
            Log.e("FatSecret Error", exception.toString());
            exception.printStackTrace();
        }
        return foods;
    }

    private static String[] generateOauthParams(int i) {
        return new String[]{
            "oauth_consumer_key=" + Globals.APP_KEY,
            "oauth_signature_method=HMAC-SHA1",
            "oauth_timestamp=" +
                Long.valueOf(System.currentTimeMillis() * 1000).toString(),
            "oauth_nonce=" + nonce(),
            "oauth_version=1.0",
            "format=json",
            "page_number=" + i,
            "max_results=" + 20};
    }

    private static String sign(String method, String uri, String[] params) {
        String[] p = {method, Uri.encode(uri), Uri.encode(paramify(params))};
        String s = join(p, "&");
        SecretKey sk = new SecretKeySpec(Globals.APP_SECRET.getBytes(), Globals.HMAC_SHA1_ALGORITHM);
        try {
            Mac m = Mac.getInstance(Globals.HMAC_SHA1_ALGORITHM);
            m.init(sk);
            return Uri.encode(new String(Base64.encode(m.doFinal(s.getBytes()), Base64.DEFAULT)).trim());
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.w("FatSecret_TEST FAIL", e.getMessage());
            return null;
        } catch (java.security.InvalidKeyException e) {
            Log.w("FatSecret_TEST FAIL", e.getMessage());
            return null;
        }
    }

    private static String paramify(String[] params) {
        String[] p = Arrays.copyOf(params, params.length);
        Arrays.sort(p);
        return join(p, "&");
    }

    private static String join(String[] array, String separator) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                b.append(separator);
            b.append(array[i]);
        }
        return b.toString();
    }

    private static String nonce() {
        Random r = new Random();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < r.nextInt(8) + 2; i++)
            n.append(r.nextInt(26) + 'a');
        return n.toString();
    }
}

