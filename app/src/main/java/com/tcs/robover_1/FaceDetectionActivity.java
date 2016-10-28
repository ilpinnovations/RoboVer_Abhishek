package com.tcs.robover_1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.tcs.robover_1.Beans.NewsBean;
import com.tcs.robover_1.Helper.JsonHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;


@SuppressLint("InflateParams")
public class FaceDetectionActivity extends Activity implements SurfaceHolder.Callback, TextToSpeech.OnInitListener, SensorEventListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    static boolean waiting = false;
    static boolean dontdistrb = false;
    static int musicflag = 0;
    static boolean picturetaken = false;
    static boolean checkcammusic = false;
    static boolean bt = false;
    static boolean facedetected = false;
    static int count = 0;
    final int RESULT_SAVEIMAGE = 0;
    public MediaPlayer mp;
    protected PowerManager.WakeLock mWakeLock;
    boolean preview = false;
    int group = 0;
    String light;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    Button btstarting;
    volatile boolean stopWorker;
    byte[] readBuffer;
    int readBufferPosition;
    ConnectionDetector cd;
    SharedPreferences sd;
    SensorManager mySensorManager;
    boolean gen = true;
    ShutterCallback mShutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };
    PictureCallback mRawPictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

        }
    };
    private Camera camera;
    PictureCallback mJPGPictureCallback = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            // TODO Auto-generated method stub

            try {

                int imageNum = 0;

                Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File imagesFolder = new File(Environment.getExternalStorageDirectory(), "FaceDetection");
                if (!imagesFolder.exists()) {
                    imagesFolder.mkdirs();
                }

                String fileName = "image_" + String.valueOf(imageNum) + ".jpg";
                File output = new File(imagesFolder, fileName);

                while (output.exists()) {
                    imageNum++;
                    fileName = "image_" + String.valueOf(imageNum) + ".jpg";
                    output = new File(imagesFolder, fileName);
                }

                Uri uriSavedImage = Uri.fromFile(output);

                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                OutputStream imageFileOS;

                imageFileOS = getContentResolver().openOutputStream(uriSavedImage);
                imageFileOS.write(arg0);
                imageFileOS.flush();
                imageFileOS.close();


                if (picturetaken == false) {
                    Log.d("faces", "picture_taken");

                    String filePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + File.separator + "FaceDetection" + File.separator + fileName;
                    Bitmap b = BitmapFactory.decodeFile(filePath);
                    ByteArrayOutputStream output1 = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.JPEG, 100, output1);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(output1.toByteArray());
                    if (cd.isConnectingToInternet()) {
                        Log.d("tag1", "internet");
                        new DetectionTask().execute(inputStream);
                    } else {
                        Log.d("tag1", "No");

                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


                        // Specify the calling package to identify your application
                        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                                .getPackage().getName());
                        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "20000");
                        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "15000");
                        // Display an hint to the user about what he should say.
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I-BOT console");

                        // Given an hint to the recognizer about what the user is going to say
                        //There are two form of language model available
                        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
                        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


                        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                    }

                } else {
                    Intent u = new Intent(FaceDetectionActivity.this, Camerapic.class);
                    u.putExtra("path", fileName);
                    startActivity(u);
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            camera.startPreview();
            camera.startFaceDetection();
        }
    };
    private SurfaceView surfaceView;
    private TextToSpeech tts;
    private SurfaceHolder surfaceHolder;
    private LayoutInflater layoutInflater = null;
    private Button btnTakePicture;
    AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            // TODO Auto-generated method stub
            btnTakePicture.setEnabled(true);
        }
    };
    private int MY_DATA_CHECK_CODE = 0;
    private TextView txtFaceCount;
    @SuppressLint("NewApi")
    FaceDetectionListener faceDetectionListener
            = new FaceDetectionListener() {

        @Override
        public void onFaceDetection(Face[] faces, final Camera camera) {

            Log.d("face", faces.length + "");
            if (faces.length == 0) {
                // Toast.makeText(getApplicationContext(), "No face detected..!", Toast.LENGTH_SHORT).show();
                if (facedetected == false) {
                    txtFaceCount.setText("No Face Detected !!!");
                }
                if (dontdistrb == false) {
                    count = 0;

                }
            } else {
                Log.d("faces", faces.length + "");


                if (dontdistrb == false) {
                    if (musicflag == 1) {
                        // hard comment      mp.stop();
                        musicflag = 0;
                    }
                    count = faces.length;
                    dontdistrb = true;
                    if (checkcammusic == false && Camerapic.waitornot == false) {


                        Camera.CameraInfo info = new Camera.CameraInfo();
                        Camera.getCameraInfo(1, info);
                        if (info.canDisableShutterSound) {
                            camera.enableShutterSound(true);
                        }
                        Log.d("facessd", "1");

                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Log.d("facessd", "2");
                                facedetected = true;
                                camera.takePicture(mShutterCallback,
                                        mRawPictureCallback, mJPGPictureCallback);


                            }
                        }, 2000);
                        Log.d("facessd", "3");

                    } else {
                        Log.d("facessd", "4");

                        Camerapic.waitornot = false;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                speakOut("Is there anything else i can do for you or is it time to say goodbye ?");
                                Voicerecon(6000, VOICE_RECOGNITION_REQUEST_CODE);


                            }
                        }, 2000);
                    }
                    txtFaceCount.setText("Face Detected !!!");
                    //return;
                }

            }


        }
    };
    private TextView txtImagePath;
    private SoundPool spool;
    private int soundID;
    private EditText metTextHint;
    private ListView mlvTextMatches;
    private Spinner msTextMatches;
    private Button mbtSpeak;
    private WebView webView;

    /**
     * Called when the activity is first created.
     */
//    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);
        //bluetooth finding and pairing with device
        cd = new ConnectionDetector(getApplicationContext());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        webView = (WebView) findViewById(R.id.mywebview);
        webView.setWebViewClient(new MyWebViewClient());
        if (cd.isConnectingToInternet()) {
            Log.d("tag1", "internet");


            NewsBingAsyncHelper newsBingAsyncHelper = new NewsBingAsyncHelper(new NewsBingAsyncHelper.ServiceResponse() {
                @Override
                public void onServiceResponse(String serviceResponse) {
                    String headlines = "";
                    Log.d("tag", serviceResponse);
                    SharedPreferences sharedpreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    NewsBean newsList = (NewsBean) JsonHelper.deserialize(serviceResponse, NewsBean.class);
                    for (int i = 0; i < newsList.getValue().size(); i++) {

                        headlines = headlines + newsList.getValue().get(i).getName() + " ";
                        Log.d("head", headlines);
                    }
                    editor.putString("headlines", headlines);
                    editor.apply();
                    //   ContactList contactList = (ContactList) JsonHelper.deserialize(serviceResponse,ContactList.class);
            /*    Log.d("Contact List", contactList.toString());
                for(int i=0; i<contactList.getContactList().size(); i++) {
                    String name = contactList.getContactList().get(i).getFirstName();
                    names += name+"\n";
                }
            //  txt.setText(names);
            }*/
                }
            });
            newsBingAsyncHelper.execute();


            NewsBingAsyncHelper_Entertainment newsBingAsyncHelper_entertainment = new NewsBingAsyncHelper_Entertainment(new NewsBingAsyncHelper_Entertainment.ServiceResponse() {
                @Override
                public void onServiceResponse(String serviceResponse) {
                    String headlines_e = "";
                    Log.d("tag", serviceResponse);
                    SharedPreferences sharedpreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    NewsBean newsList = (NewsBean) JsonHelper.deserialize(serviceResponse, NewsBean.class);
                    for (int i = 0; i < newsList.getValue().size(); i++) {

                        headlines_e = headlines_e + newsList.getValue().get(i).getName() + " ";
                        Log.d("head", headlines_e);
                    }
                    editor.putString("headlines_e", headlines_e);
                    editor.apply();
                    //   ContactList contactList = (ContactList) JsonHelper.deserialize(serviceResponse,ContactList.class);
            /*    Log.d("Contact List", contactList.toString());
                for(int i=0; i<contactList.getContactList().size(); i++) {
                    String name = contactList.getContactList().get(i).getFirstName();
                    names += name+"\n";
                }
            //  txt.setText(names);
            }*/
                }
            });
            newsBingAsyncHelper_entertainment.execute();


        } else {
            Log.d("tag1", "No Internet");
        }
        //  String url = "http://theinspirer.in/iBuzzer/robot.html";
        String url = "file:///android_asset/robot.html";
        String url_static = "file:///android_asset/robot_static.html";
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.camPreview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        txtFaceCount = (TextView) findViewById(R.id.tvFaceCount);
        btstarting = (Button) findViewById(R.id.btstart);
        txtImagePath = (TextView) findViewById(R.id.tvImagePath);
        /*try
        {
            findBT();
            openBT();
        }
        catch (Exception ex) {
        ex.printStackTrace();
        }
           btstarting.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        try {
            openBT();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
});*/

        mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Sensor LightSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mySensorManager.registerListener(
                this,
                LightSensor,
                SensorManager.SENSOR_DELAY_NORMAL);


        layoutInflater = LayoutInflater.from(getBaseContext());
        View viewControl = layoutInflater.inflate(R.layout.picture_control, null);
        LayoutParams layoutParamsControl
                = new LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);

        /*tts = new TextToSpeech(this, this);*/
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
   /*if(bt==true)
   {
       String msg = "8";
       msg += "\n";
       try {
           mmOutputStream.write(msg.getBytes());
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }

       new Handler().postDelayed(new Runnable() {


           @Override
           public void run() {

               String msg = "9";
               msg += "\n";
               try {
                   mmOutputStream.write(msg.getBytes());
               } catch (IOException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
               }


           }
       }, 5000);
   }*/
        btnTakePicture = (Button) findViewById(R.id.takepicture);

        btnTakePicture.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

            }
        });

        LinearLayout layoutBackground = (LinearLayout) findViewById(R.id.linearLayout);
        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btnTakePicture.setEnabled(false);
                camera.autoFocus(mAutoFocusCallback);
            }
        });
        mbtSpeak = (Button) findViewById(R.id.btSpeak);
        checkVoiceRecognition();
    }

    void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {

        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) {
                    mmDevice = device;
                    txtFaceCount.setText("bluetooth found");
                    break;
                }
            }
        }

    }

    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();

        beginListenForData();
        txtFaceCount.setText("bluetooth opened");
        bt = true;
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {

                                        }
                                    });
                                } else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
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
        if (musicflag == 1) {
            mp.stop();
            musicflag = 0;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);


        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                .getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "20000");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "15000");
        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "I-BOT console");

        // Given an hint to the recognizer about what the user is going to say
        //There are two form of language model available
        //1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
        //2.LANGUAGE_MODEL_FREE_FORM  : If not sure about the words or phrases and its domain.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub
        if (preview) {
            camera.stopFaceDetection();
            camera.stopPreview();
            preview = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                camera.startFaceDetection();
                preview = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera = Camera.open(1);

        camera.setFaceDetectionListener(faceDetectionListener);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopFaceDetection();
        camera.stopPreview();
        camera.release();
        camera = null;
        preview = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_face_detection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void speakOutadd(String text) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) { //If Voice recognition is successful then it returns RESULT_OK
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.

                    Log.d("error", textMatchList.get(0));
                    if (textMatchList.get(0).contains("call") || textMatchList.get(0).contains("calling")) {
                        speakOut("So Whom do you want to call?");
                        Voicerecon(4000, 911);

                    } else if (textMatchList.get(0).contains("sms") || textMatchList.get(0).contains("readsms")) {
                        speakOut("I am going to read your last sms");
                        Voicerecon(4000, 2323);


                    } else if (textMatchList.get(0).contains("shutdown")) {
                        speakOut("I m exiting  bye  ");
                        finish();

                    } else if (textMatchList.get(0).contains("bye")) {
                        speakOut("Good bye dear.have a nice day ahead !");
                        checkcammusic = false;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                dontdistrb = false;

                            }
                        }, 4000);
                        // populate the Matches
                        /*mlvTextMatches
                                .setAdapter(new ArrayAdapter<String>(this,
                                        android.R.layout.simple_list_item_1,
                                        textMatchList));*/
                    } else if (textMatchList.get(0).contains("name")) {
                        speakOut("My name is I BOT  . I am a programmable social bot .");
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                dontdistrb = false;
                            }
                        }, 4000);
                        // populate the Matches
                        /*mlvTextMatches
                                .setAdapter(new ArrayAdapter<String>(this,
                                        android.R.layout.simple_list_item_1,
                                        textMatchList));*/
                    } else if (textMatchList.get(0).contains("reminder") || textMatchList.get(0).contains("reminder")) {
                        speakOut("So What do you want me to remind you?");
                        Voicerecon(4000, 280);
                        // populate the Matches
                        /*mlvTextMatches
                                .setAdapter(new ArrayAdapter<String>(this,
                                        android.R.layout.simple_list_item_1,
                                        textMatchList));*/
                    } else if (textMatchList.get(0).contains("picture") || textMatchList.get(0).contains("selfie") || textMatchList.get(0).contains("selfy")) {
                        picturetaken = true;
                        camera.takePicture(mShutterCallback,
                                mRawPictureCallback, mJPGPictureCallback);
                        speakOut("Your photo has been taken ");
                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                dontdistrb = false;
                            }
                        }, 4000);

                    } else if (textMatchList.get(0).contains("brightness") || textMatchList.get(0).contains("lights") || textMatchList.get(0).contains("light sensors")) {
                        speakOut(light);
                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                dontdistrb = false;
                            }
                        }, 5000);

                    } else if (textMatchList.get(0).contains("restart") || textMatchList.get(0).contains("again")) {
                        Intent i = new Intent(this, FaceDetectionActivity.class);
                        startActivity(i);
                        dontdistrb = false;
                        finish();

                    } else if (textMatchList.get(0).contains("remind") || textMatchList.get(0).contains("remind")) {
                        SharedPreferences er = getSharedPreferences("name", Context.MODE_PRIVATE);
                        String remindme = er.getString("remind", "No reminder for you sorry");
                        speakOut(remindme);
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                dontdistrb = false;
                                checkcammusic = true;
                            }
                        }, 4000);

                    } else if (textMatchList.get(0).contains("play") || textMatchList.get(0).contains("song")) {
                        mp = new MediaPlayer();
                        try {
                            AssetFileDescriptor afd = getAssets().openFd("TitanicRomantic.mp3");
                            // mp.setDataSource("/mnt/sdcard/Music/dada.mp3");
                            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            mp.prepare();
                            mp.start();
                            musicflag = 1;
                            Log.v("sound", "count");
                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }

                                ;
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                dontdistrb = false;

                            }
                        }, 500);

                    } else if (textMatchList.get(0).contains("yes please") || textMatchList.get(0).contains("yes")) {
                        if (group == 1) {

                            picturetaken = true;
                            camera.takePicture(mShutterCallback,
                                    mRawPictureCallback, mJPGPictureCallback);
                            speakOut("Your photo has been taken ");
                            checkcammusic = true;
                            new Handler().postDelayed(new Runnable() {


                                @Override
                                public void run() {
                                    dontdistrb = false;
                                }
                            }, 4000);
                            group = 0;
                        } else {
                            if (gen == true) {
                                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                String headlines = sharedPreferences.getString("headlines", "No News for you sir");
                                speakOut(headlines);
                                //   speakOut("A group of Kenyan Muslims travelling on a bus ambushed by Islamist gunmen protected Christian passengers by refusing to be split into groups, according to eyewitnesses.");

                            } else {
                                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                String headlines_e = sharedPreferences.getString("headlines_e", "No News for you mam");
                                speakOut(headlines_e);
                                //  speakOut("Heyway Geans are new in the market with special lady touch . Also,  SRK's Dilwaalee is the biggest hit of the year till date.");
                            }
                        }
                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                dontdistrb = false;

                            }
                        }, 16000);

                    } else if (textMatchList.get(0).contains("story") || textMatchList.get(0).contains("stories")) {
                        speakOut("In a small village, a little boy lived with his father and mother. He was the only son for them. The parents of the little boy were very depressed due to his bad temper. The boy used to get angry very soon and taunt others with his words. His bad temper made him fall for angry words. He scolded kids, neighbours and even his friends due to anger. He invited all worries for his parents through the verbal usage. While he forgot what he spoke in anger, his friends and neighbours avoided him.");

                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                dontdistrb = false;

                            }
                        }, 16000);

                    } else if (textMatchList.get(0).contains("weather") || textMatchList.get(0).contains("report")) {
                        SharedPreferences sd = getSharedPreferences("weather", Context.MODE_PRIVATE);
                        String tospek = sd.getString("report", "bad network connection,unable to fetch details");
                        speakOut(tospek);

                        checkcammusic = true;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                dontdistrb = false;

                            }
                        }, 10000);

                    } else {

                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                speakOut("Sorry did not get you.Please repeate ! ?");

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
                                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);
                                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "20000");
                                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "15000");
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

        } else if (requestCode == 10) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.


                    SharedPreferences sd = getSharedPreferences("name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sd.edit();
                    e.putString("name", textMatchList.get(0).toString());
                    e.commit();
                    speakOut("Hello " + textMatchList.get(0).toString() + " So is there any way I can help you?");
                    Voicerecon(6000, VOICE_RECOGNITION_REQUEST_CODE);
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
            }
        } else if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                SharedPreferences sd = getSharedPreferences("name", Context.MODE_PRIVATE);
                final String name = sd.getString("name", "bla");
                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    if (textMatchList.get(0).contains("yes")) {
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {
                                speakOut("Welcome back " + name + ". Hope you are doing well , So is there any way I can help you?");
                            }
                        }, 1000);

                        Voicerecon(7000, VOICE_RECOGNITION_REQUEST_CODE);


                    } else {
                        dontdistrb = true;
                        speakOut("Then please introduce yourself Dear ");


                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                                // Specify the calling package to identify your application
                                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                                        .getPackage().getName());
                                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "20000");
                                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "15000");

                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Irobo Console");
                                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);

                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);


                                startActivityForResult(intent, 190);
                            }
                        }, 3000);


                        //Result code for various error.
                    }
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
            }
        } else if (requestCode == 190) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    SharedPreferences sd = getSharedPreferences("name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sd.edit();
                    e.putString("name", textMatchList.get(0).toString());
                    e.commit();
                    speakOut("Hello " + textMatchList.get(0).toString() + " So is there any way I can help you?");
                    Voicerecon(4000, VOICE_RECOGNITION_REQUEST_CODE);
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
            }
        } else if (requestCode == 280) {
            if (resultCode == RESULT_OK) {

                ArrayList<String> textMatchList = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                if (!textMatchList.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {

                            speakOutadd("Your Reminder has been saved ! ");

                        }
                    }, 1000);

                    SharedPreferences sd = getSharedPreferences("name", Context.MODE_PRIVATE);
                    SharedPreferences.Editor e = sd.edit();
                    e.putString("remind", textMatchList.get(0).toString());
                    e.commit();

                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {
                            speakOutadd(" So What else can i do for you?");
                            Voicerecon(2000, VOICE_RECOGNITION_REQUEST_CODE);


                        }
                    }, 2000);

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
            }
        } else if (requestCode == 911) {
            if (resultCode == RESULT_OK) {

                final ArrayList<String> textMatchList1 = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList1.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {
                            speakOut("Calling " + textMatchList1.get(0).toString() + " . ");
                        }
                    }, 1000);

                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {


                            String number = getPhoneNumber(textMatchList1.get(0).toString(), FaceDetectionActivity.this);

                            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                            my_callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            my_callIntent.setData(Uri.parse("tel:" + number));
                            if (ActivityCompat.checkSelfPermission(FaceDetectionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(my_callIntent);

                            checkcammusic = true;
                            new Handler().postDelayed(new Runnable() {


                                @Override
                                public void run() {
                                    dontdistrb = false;
                                }
                            }, 2000);
                        }
                    }, 4000);

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
            }
        } else if (requestCode == 2323) {
            if (resultCode == RESULT_OK) {

                final ArrayList<String> textMatchList1 = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (!textMatchList1.isEmpty()) {
                    // If first Match contains the 'search' word
                    // Then start web search.
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {
                            speakOut("Calling " + textMatchList1.get(0).toString() + " . ");
                        }
                    }, 1000);

                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {


                            String number = getPhoneNumber(textMatchList1.get(0).toString(), FaceDetectionActivity.this);

                            Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                            my_callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            my_callIntent.setData(Uri.parse("tel:" + number));
                            if (ActivityCompat.checkSelfPermission(FaceDetectionActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            startActivity(my_callIntent);

                            checkcammusic = true;
                            new Handler().postDelayed(new Runnable() {


                                @Override
                                public void run() {
                                    dontdistrb = false;
                                }
                            }, 2000);
                        }
                    }, 4000);

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
            }
        } else if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                tts = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void Voicerecon(int Interval, final int requestcode) {
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                // Specify the calling package to identify your application
                intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
                        .getPackage().getName());
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 2);
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, "20000");
                intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, "15000");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Irobo Console");
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                startActivityForResult(intent, requestcode);
            }
        }, Interval);
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        bt = false;
        checkcammusic = false;
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
        }
        super.onDestroy();

    }

    public String getPhoneNumber(String name, Context context) {
        String ret = null;
        Log.d("name", name);
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + name + "%'";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor c = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection, selection, null, null);
        if (c.moveToFirst()) {
            ret = c.getString(0);
        }
        c.close();
        if (ret == null)
            ret = "Unsaved";
        return ret;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            if (event.values[0] <= 20) {
                light = "It is Bright light";
            } else {
                light = "It is low light";
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class DetectionTask extends AsyncTask<InputStream, String, com.microsoft.projectoxford.face.contract.Face[]> {
        private boolean mSucceed = true;

        @Override
        protected com.microsoft.projectoxford.face.contract.Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.

            FaceServiceRestClient faceServiceClient =
                    new FaceServiceRestClient("a72723a4305e433c8c63a6ca9fb67e5a");
            try {

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to analyzes facial landmarks */
                        true,       /* Whether to analyzes age */
                        new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.HeadPose
                        });    /* Whether to analyzes gender */
                /* Whether to analyzes head pose */
            } catch (Exception e) {
                mSucceed = false;
                String f = (e.getMessage());
                Log.d("faceapimsg", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            txtFaceCount.setText("Processing . . .");
            if (waiting == false) {
                speakOut("Let me take the honour to introduce myself to you , I was developed in T C S Innovations Labs , I am a social robot with the capability to do anything ");
                waiting = true;
                if (cd.isConnectingToInternet()) {
                    Log.d("tag", "internet");
                } else {
                    Log.d("tag", "No");
                }
            } else {

                speakOut("Processing your face structure , please be patient ");
                // mProgressDialog.show();
            }
            //addLog("Request: Detecting in image " + mImageUri);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // mProgressDialog.setMessage(progress[0]);
            //setInfo(progress[0]);
            txtFaceCount.setText("Calculating . . . ");
        }

        @Override
        protected void onPostExecute(com.microsoft.projectoxford.face.contract.Face[] result) {
            if (mSucceed) {
                try {
                    List<com.microsoft.projectoxford.face.contract.Face> faces = new ArrayList<>();
                    //faces.get(position).faceAttributes.gender
                    if (result == null) {
                        Log.d("tag", "No face detected");
                        speakOutadd("No face detected");

                    }
                    Log.d("tagresult", String.valueOf(result));
                    faces = Arrays.asList(result);
                    DecimalFormat formatter = new DecimalFormat("#0.0");
                    String Details = "Age: " + formatter.format(faces.get(0).faceAttributes.age) + "\n"
                            + "Gender: " + faces.get(0).faceAttributes.gender;
                    int count = 0;
                    int malecount = 0;
                    int femalecount = 0;

                    String genderwa = faces.get(0).faceAttributes.gender.toString();
                    for (com.microsoft.projectoxford.face.contract.Face f : faces) {
                        if (f.faceAttributes.gender.toString().equals("male")) {
                            malecount++;

                        } else {
                            femalecount++;
                        }

                        count++;
                    }
                    if (count == 2) {
                        speakOutadd("Hi to Both of you . Greetings for the day.");
                        if (malecount != 0 && femalecount != 0) {
                            speakOutadd("So ," + malecount + " Male and " + femalecount + " Female is what i have encountered , How can i help you?");
                        } else if (malecount == 0 && femalecount != 0) {
                            speakOutadd("So," + femalecount + " Female's is what i have encountered , How can i help you? ");
                        } else if (malecount != 0 && femalecount == 0) {
                            speakOutadd("So," + malecount + " male's is what i have encountered , How can i help you? ");
                        }
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Voicerecon(5000, VOICE_RECOGNITION_REQUEST_CODE);
                            }
                        }, 7000);
                    } else if (count > 2 && count <= 3) {
                        speakOutadd("Hi all 3 of you . Greetings for the day");

                        if (malecount != 0 && femalecount != 0) {
                            speakOutadd("So ," + malecount + " Male and " + femalecount + " Female is what i have encountered , How can i help you?");
                        } else if (malecount == 0 && femalecount != 0) {
                            speakOutadd("So," + femalecount + " Female's is what i have encountered , How can i help you?? ");
                        } else if (malecount != 0 && femalecount == 0) {
                            speakOutadd("So," + malecount + " male's is what i have encountered , How can i help you? ");

                        }
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Voicerecon(5000, VOICE_RECOGNITION_REQUEST_CODE);
                            }
                        }, 7000);

                    } else if (count > 3) {
                        speakOutadd("Hi to all of you . Greetings for the day .can i take a selfie for all of you");
                        group = 1;
                        new Handler().postDelayed(new Runnable() {


                            @Override
                            public void run() {

                                Voicerecon(5000, VOICE_RECOGNITION_REQUEST_CODE);

                            }
                        }, 3000);
                    } else {
                        if (genderwa.equals("male")) {
                            gen = true;
                            speakOutadd("hello ,Sir. Would you like me to read some sports news for you ?");
                            new Handler().postDelayed(new Runnable() {


                                @Override
                                public void run() {

                                    Voicerecon(2000, VOICE_RECOGNITION_REQUEST_CODE);

                                }
                            }, 4000);

                        } else {
                            gen = false;
                            speakOutadd("hello , Mam .Would you like to know about recent entertainment news ?");
                            new Handler().postDelayed(new Runnable() {


                                @Override
                                public void run() {

                                    Voicerecon(2000, VOICE_RECOGNITION_REQUEST_CODE);
                                }
                            }, 4000);
                        }
                    }

                } catch (Exception e) {
                    speakOutadd("So Is there any way I can help you ?");
                    new Handler().postDelayed(new Runnable() {


                        @Override
                        public void run() {

                            Voicerecon(2000, VOICE_RECOGNITION_REQUEST_CODE);
                        }
                    }, 5000);
                }


            }
        }
    }

}