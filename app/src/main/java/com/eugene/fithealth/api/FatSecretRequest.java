package com.eugene.fithealth.api;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.eugene.fithealth.util.Globals;
import com.eugene.fithealth.model.FoodSearch;
import com.eugene.fithealth.model.Foods;
import com.google.gson.Gson;

import java.io.IOException;
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

public class FatSecretRequest {

    public FatSecretRequest() {
    }

    public Foods getFoods(String query, String page) {
        List<String> params = new ArrayList<>(Arrays.asList(generateOauthParams(page)));
        String[] template = new String[1];
        params.add("method=foods.search");
        params.add("search_expression=" + Uri.encode(query));
        params.add("oauth_signature=" + sign(Globals.APP_METHOD, Globals.APP_URL, params.toArray(template)));
        try {
            URL url = new URL(Globals.APP_URL + "?" + paramify(params.toArray(template)));
            Log.e("Testing", url.toString());
            URLConnection api = url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(api.getInputStream());
            FoodSearch response = new Gson().fromJson(inputStreamReader, FoodSearch.class);
            return response.getFoods();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String[] generateOauthParams(String i) {
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

    private String sign(String method, String uri, String[] params) {
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

    private String paramify(String[] params) {
        String[] p = Arrays.copyOf(params, params.length);
        Arrays.sort(p);
        return join(p, "&");
    }

    private String join(String[] array, String separator) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0)
                b.append(separator);
            b.append(array[i]);
        }
        return b.toString();
    }

    private String nonce() {
        Random r = new Random();
        StringBuilder n = new StringBuilder();
        for (int i = 0; i < r.nextInt(8) + 2; i++)
            n.append(r.nextInt(26) + 'a');
        return n.toString();
    }
}
