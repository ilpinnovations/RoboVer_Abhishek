package com.tcs.robover_1;

import android.os.AsyncTask;

import com.loopj.android.http.HttpGet;

import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * Created by Riyaz on 6/9/2016.
 */


public class NewsBingAsyncHelper_Entertainment extends AsyncTask<String, Void, String>{


    private ServiceResponse mServiceResponse;
    public NewsBingAsyncHelper_Entertainment(ServiceResponse serviceResponse){
        mServiceResponse = serviceResponse;
    }


    @Override
    protected String doInBackground(String... params) {


        HttpClient httpclient = HttpClients.createDefault();

        try
        {
            String jsonString = null;
            URIBuilder builder = new URIBuilder("https://bingapis.azure-api.net/api/v5/news/");
            builder.setParameter("Category", "Entertainment");
            builder.setParameter("count", "3");
           //builder.setParameter("offset", "0");
            builder.setParameter("mkt", "en-in");
            builder.setParameter("safesearch", "Moderate");

            URI uri = builder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Ocp-Apim-Subscription-Key", "a8f5e563f9004ae8b9bcb934206a82d2");
            // Request body
            StringEntity reqEntity = new StringEntity("{body}");
            request.setEntity(reqEntity);

            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                jsonString=EntityUtils.toString(entity);
                return jsonString;
               // System.out.println(EntityUtils.toString(entity));
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        return null;
    }



    @Override
    protected void onPostExecute(String s) {
        mServiceResponse.onServiceResponse(s);
        //Log.d("tag",s);
    }

    interface ServiceResponse {
        void onServiceResponse(String serviceResponse);
    }


    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager(){
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {}
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }}}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }
}
