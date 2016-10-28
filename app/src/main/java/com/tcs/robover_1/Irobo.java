package com.tcs.robover_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

//import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;




public class Irobo extends Activity implements TextToSpeech.OnInitListener, LocationListener {



    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private TextToSpeech tts;
    protected PowerManager.WakeLock mWakeLock;
    EditText metTextHint;
    ListView mlvTextMatches;
    Spinner msTextMatches;
    Button mbtSpeak;
    ProgressBar mProgress;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String description, temp, windspeed, pressure, humidity;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irobo);
        mProgress = (ProgressBar) findViewById(R.id.progressBar1);
       // FacebookSdk.sdkInitialize(getApplicationContext());
        checkVoiceRecognition();
     tts = new TextToSpeech(this, this);

      //  MyAsyncTask2 task2 = new MyAsyncTask2();
      //  task2.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=ffca7aa90fefd79fbc262629f17adb9c&units=metric");

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to re
            // quest the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Log.d("tag","speak1");
       speakOut("Welcome to I Bot Automated Console.");
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            Log.d("tag","speak2");

                speakOut("Welcome to I Bot Automated Console.");
            }
        }, 3000);
        Log.d("tag","speak3");

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                Log.d("tag","speak4");
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                // Specify the calling package to identify your application
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                        .getPackage().getName());


                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Irobo Console");


                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


                startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
            }
        }, 12000);

        Log.d("tag","speak5");
    }

    public void checkVoiceRecognition() {
        // Check if voice recognition is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            mbtSpeak.setEnabled(false);
            mbtSpeak.setText("Voice recognizer not present");
            Toast.makeText(this, "Voice recognizer not present",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void speak(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());


        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I-Robo Console");


        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE)

            //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    if (textMatchList.get(0).contains("start yourself")||textMatchList.get(0).contains("start")) {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                speakOut("Starting Face Recognition Version 1 point 1 ");
                                mProgress.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Intent i = new Intent(Irobo.this, FaceDetectionActivity.class);
                                startActivity(i);
                                finish();

                            }
                        }, 4000);

                    } else if (textMatchList.get(0).contains("innovations")) {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                speakOut("You Are Successfully Authenticated now  What command do You have for me ?");
                                mProgress.setVisibility(View.VISIBLE);
                            }
                        }, 1000);


                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                // Specify the calling package to identify your application
                                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                                        .getPackage().getName());


                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Irobo Console");


                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


                                startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                            }
                        }, 7000);


                    } else {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                mProgress.setVisibility(View.INVISIBLE);
                                speakOut("Sorry did not get you  Please repeate ! ?");

                            }
                        }, 1000);


                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                // Specify the calling package to identify your application
                                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                                        .getPackage().getName());


                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Irobo Console");


                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


                                startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                            }
                        }, 4000);
                    }

                }
                //Result code for various error.
            } else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {
                showToastMessage("Audio Error");
            } else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {
                showToastMessage("Client Error");
            } else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {
                showToastMessage("Network Error");
            } else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {
                showToastMessage("No Match");
            } else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {
                showToastMessage("Server Error");
            }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Helper method to show the toast message
     **/
    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.ENGLISH);
            double takeit = 0.9;
            double takeitagain = 0.8;
            tts.setPitch((float) takeit);
            tts.setSpeechRate((float) takeitagain);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            }/* else {

                //speakOut();
            }*/

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut(String text) {


        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        if(tts != null) {

            tts.stop();
            tts.shutdown();
            // Log.d(TAG, "TTS Destroyed");
        }
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        MyAsyncTask task = new MyAsyncTask();
        task.execute("http://192.168.1.104:6577/iBot/AddData?latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude(), "http://192.168.1.104:6577/iBot/GasSenser?gas=0");
Log.d("lat",String.valueOf(location.getLatitude()));
        MyAsyncTask2 task2 = new MyAsyncTask2();
        task2.execute("http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&appid=ffca7aa90fefd79fbc262629f17adb9c&units=metric");

        if (ActivityCompat.checkSelfPermission(Irobo.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Irobo.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.removeUpdates(this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    private class MyAsyncTask extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected JSONObject doInBackground(String... params) {

            JSONArray response = new JSONArray();

            JSONObject obj = new JSONObject();
            for (int i = 0; i < params.length; i++) {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(params[i]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    urlConnection.connect();
                    int responseCode = urlConnection.getResponseCode();

                    if (responseCode == 200) {


                    } else {
                        Log.v("CatalogClient", "Response code:" + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }


            }


            return obj;
        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            super.onPostExecute(obj);

            Toast.makeText(getApplicationContext(), "Processings done", Toast.LENGTH_LONG).show();
            Log.e("llt", "Locatn updated");


        }

        private String readStream(InputStream in) throws UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder sb = new StringBuilder();
            try {

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }

    private class MyAsyncTask2 extends AsyncTask<String, Void, JSONObject> {
        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected JSONObject doInBackground(String... params) {


            URL url;
            HttpURLConnection urlConnection = null;
            JSONArray response = new JSONArray();

            JSONObject obj = new JSONObject();
            try {
                url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");

                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();

                if (responseCode == 200) {

                    InputStream i = urlConnection.getInputStream();
                    String responseString = readStream(urlConnection.getInputStream());

                    Log.v("CatalogClient", responseString);

                    obj = new JSONObject(responseString);
                    //response = new JSONArray(responseString);
                } else {
                    Log.v("CatalogClient", "Response code:" + responseCode);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (urlConnection != null)
                    urlConnection.disconnect();
            }

            return obj;


        }

        @Override
        protected void onPostExecute(JSONObject obj) {
            super.onPostExecute(obj);


            try {
                JSONObject info = obj.getJSONObject("main");
                temp = info.getString("temp");
                pressure = info.getString("pressure");
                humidity = info.getString("humidity");

                JSONObject info3 = obj.getJSONObject("wind");
                windspeed = info3.getString("speed");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String report = "The weather report for your location is . Temperature , " + temp + ",Pressure ," + pressure + ", Humidity," + humidity + " ,Wind speed" + windspeed;


            SharedPreferences o = getSharedPreferences("weather", Context.MODE_PRIVATE);
            SharedPreferences.Editor e = o.edit();
            e.putString("report", report).commit();
            MyAsyncTask task = new MyAsyncTask();
            task.execute("http://192.168.1.104:6577/iBot/AddTemp?temp=" + temp + "&pressure=" + pressure + "&windspeed=" + windspeed + "&humidity=" + humidity, "https://api.thingspeak.com/update?api_key=S3794JL53GZH00ZQ&field1=" + temp + "&field2=" + humidity + "&field3=" + windspeed);

        }

        private String readStream(InputStream in) throws UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            StringBuilder sb = new StringBuilder();
            try {

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }


}