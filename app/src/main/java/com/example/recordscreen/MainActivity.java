package com.example.recordscreen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int REQUEST_CODE = 1000;
    private static final int REQUEST_PERMISSION = 1001;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private int mScreenDensity;
    private static int DISPALY_WIDTH ;
    private static int DISPLAY_HEIGHT ;
    private String videoUri = "";
    private String videoUri1 = "";
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    private MediaRecorder mediaRecorder;
    public static ToggleButton toggleButton;
    private ToggleButton toggleButtonQuality;
    private ToggleButton toggleButtonDelay;
    private ToggleButton toggleButtonPreview;
    private ToggleButton toggleButtonFrameRate;
    private ToggleButton toggleButtonFormat;
    private VideoView videoView;
    private RelativeLayout drawer;
    private TextView countText;
    private MediaController mediaController;
    public int isRecord;

    private static final int MY_REQUEST_CODE = 100;

    private final String CHANNEL_ID = " display_notification";
    private  final int NOTIFICATION_ID = 001;
    public static  String TAG = MainActivity.class.getSimpleName();

    File appFolder;
    static{
        ORIENTATIONS.append(Surface.ROTATION_0,90);
        ORIENTATIONS.append(Surface.ROTATION_90,0);
        ORIENTATIONS.append(Surface.ROTATION_180,270);
        ORIENTATIONS.append(Surface.ROTATION_270,180);
    }
    CountDownTimer t = new CountDownTimer(1000*1000, 1000) {

        public void onTick(long millisUntilFinished) {
            int timeRecord = (int) (1000*1000 - millisUntilFinished);
            int hh = (int) timeRecord/3600000;
            int mm = (int) (timeRecord-hh*3600000)/60000;
            int ss = (int) (timeRecord-hh*3600000-mm*60000)/1000;
            countText.setText(String.format("%02d:%02d:%02d", hh,mm, ss));
        }
        public void onFinish() {
            countText.setText("");
        }
    };
    @SuppressLint("MissingSuperCall")
    protected void onStop() {

        super.onStop();
        Log.d("ON","onStop");
        if (videoView.isPlaying()) {
            videoView.setVisibility(View.INVISIBLE);

        }
    }
    protected void onStart() {
        super.onStart();
        Log.d("ON","onStart");

        if (videoView.isPlaying()) {
            videoView.setVisibility(View.VISIBLE);
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onResume() {
        super.onResume();
        Log.d("ON","onResume");
        if(videoUri1.equals("")){
            Log.d("Play","No UriVideo!");
        }else playVideoView(videoUri1);

    }
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ON","onResume");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("ON","mainActivityStart");
        if(isRecord ==2){
            onBackPressed();
        }
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics  = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        DISPLAY_HEIGHT = metrics.heightPixels;
        DISPALY_WIDTH = metrics.widthPixels;

        appFolder = new File(Environment.getExternalStorageDirectory() + "/" + "Record Screen");
        if(!appFolder.exists()){
            appFolder.mkdir();
        }
        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager)getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
        toggleButtonQuality = (ToggleButton)findViewById(R.id.toggleButtonQuality);
        toggleButtonDelay = (ToggleButton)findViewById(R.id.toggleButtonDelay);
        toggleButtonPreview = (ToggleButton)findViewById(R.id.toggleButtonPreview);
        toggleButtonFrameRate = (ToggleButton)findViewById(R.id.toggleButtonFrameRate);
        toggleButtonFormat = (ToggleButton) findViewById(R.id.toggleButtonFormat) ;

        videoView = (VideoView)findViewById(R.id.videoView);

        countText = (TextView)findViewById(R.id.countText);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        isRecord = 0;

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                        + ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECORD_AUDIO)){
                        toggleButton.setChecked(false);
                        Snackbar.make(drawer,"Permission",Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE
                                },REQUEST_PERMISSION);
                            }
                        }).show();
                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE
                        },REQUEST_PERMISSION);
                    }
                }else{
                    toggleScreenShare(v);
                }
            }
        });
    }

    public  int getVideoQuality() {
        Log.d("Play","getVideoQuality");
        int videoQuality = 0;
        if(toggleButtonQuality.isChecked()){
            videoQuality = 3000000;
        }else videoQuality = 1000000;
        return videoQuality;
    }

    public int getDelay(){
        Log.d("Play","getDelay");
        int delay = 0;
        if(toggleButtonDelay.isChecked()){
            delay = 3;
        }else delay = 0;
        return delay;
    }

    public int getFrameRate(){
        Log.d("Play","getFrameRate");
        int frameRate = 0;
        if(toggleButtonFrameRate.isChecked()){
            frameRate = 60;
        } else frameRate = 30;
        return frameRate;
    }

    public int getPreview(){
        Log.d("Play","getSound");
        if(toggleButtonPreview.isChecked()){
            return 1;
        } else return 0;
    }

    public String getFormat(){
        Log.d("Play","getFormat");
        if(toggleButtonFormat.isChecked()){
            return ".avi";
        } else return ".mp4";
    }

    @Override
    public void onBackPressed() {
        Log.d("Play","onBackPressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_new_record) {

        } else if (id == R.id.nav_view_record) {
            Intent intent = new Intent(MainActivity.this, ViewRecord.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Log.d("Play","selectViewRecord");
        } else if (id == R.id.nav_exit) {
            Log.d("Play","selectExit");
            finish();
            System.exit(0);
        } else if (id == R.id.nav_send) {
            Log.d("Play","selectSend");
            Intent Email = new Intent(Intent.ACTION_SEND);
            Email.setType("text/email");
            Email.putExtra(Intent.EXTRA_EMAIL, new String[] { "anhtuan01041998@gmail.com" });
            Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            Email.putExtra(Intent.EXTRA_TEXT, "Dear ...," + "");
            startActivity(Intent.createChooser(Email, "Send Feedback:"));
            return true;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void toggleScreenShare(View v) {
        if(((ToggleButton)v).isChecked()){
            try {
                Thread.sleep(getDelay()*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initRecorder();
            recordScreen();
            isRecord = 1;
            t.start();
            displayNotification(v);
        }else{
            mediaProjection.stop();
            mediaRecorder.reset();
            stopRecordScreen();
            isRecord=0;
            t.cancel();
            countText.setText("");

            if(getPreview()==1){
                playVideoView(videoUri);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void playVideoView(String videoUri){
        Log.d("Play","playVideoView");
        videoView.setVisibility(View.VISIBLE);
        videoView.setVideoURI(Uri.parse(videoUri));
        if (mediaController == null) {
            mediaController = new MediaController(MainActivity.this);
            mediaController.setAnchorView(videoView);
            //mediaController.setBackgroundColor(Color.parseColor("#000000"));
            videoView.setMediaController(mediaController);
        }
        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void recordScreen() {
        Log.d("Play","recordScreen");
        if(mediaProjection == null){
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(),REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        Log.d("Play","createVirtualDisplay");
        return mediaProjection.createVirtualDisplay("MainActivity",DISPALY_WIDTH,DISPLAY_HEIGHT,mScreenDensity
                ,DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,mediaRecorder.getSurface(),null,null);
    }

    @SuppressLint("SimpleDateFormat")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void initRecorder() {
        Log.d("Play","initRecorder");
        try{
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            String filename = new StringBuilder("/Record_").append(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).append(getFormat()).toString();
            // File outputFile = new File(appFolder, filename);
            // videoUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            //      + new StringBuilder("/Record_").append(new SimpleDateFormat("yyyy-mm-dd_HH-mm-ss").format(new Date())).append(getFormat()).toString();
            videoUri = appFolder.getAbsolutePath()+ filename;
            mediaRecorder.setOutputFile(videoUri);
            mediaRecorder.setVideoSize(DISPALY_WIDTH,DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mediaRecorder.setVideoEncodingBitRate(getVideoQuality());
            mediaRecorder.setVideoFrameRate(getFrameRate());
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != REQUEST_CODE){
            Toast.makeText(this,"Unk Error!",Toast.LENGTH_SHORT).show();
            return;
        }
        if(resultCode != RESULT_OK){
            Toast.makeText(this,"Pemission Denied!",Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);
            return;
        }
        mediaProjectionCallback = new MediaProjectionCallback();
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
        mediaProjection.registerCallback(mediaProjectionCallback,null);
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            if(toggleButton.isChecked()){
                toggleButton.setChecked(false);
                mediaRecorder.stop();
                mediaRecorder.reset();
            }
            mediaProjection = null;
            stopRecordScreen();
            super.onStop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopRecordScreen() {
        Log.d("Play","stopRecord");
        if(getPreview()==0){
            videoUri1 = "";
        }else videoUri1 = videoUri;
        if(virtualDisplay == null){
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void destroyMediaProjection() {
        Log.d("Play","destroyMediaProjection");
        if(mediaProjection != null){
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION: {
                if((grantResults.length > 0)&& (grantResults[0]+grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                    toggleScreenShare(toggleButton);
                }else{
                    toggleButton.setChecked(false);
                    Snackbar.make(drawer,"Permission",Snackbar.LENGTH_INDEFINITE).setAction("ENABLE", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO
                            },REQUEST_PERMISSION);
                        }
                    }).show();
                }
                return;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void displayNotification(View view) {

        Log.d("Play", "displayNotification");
        createNotificationChannel();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_notification);
        final NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        isRecord = 2;
        builder.setContentTitle("Recording...");
        builder.setContentText("Stop Record!");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);
        Intent intent = new Intent(this, Exit.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.addAction(R.drawable.ic_notification, "Stop", pendingIntent);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
    public void createNotificationChannel(){
        Log.d("Play","createNotificationChannel");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Notification";
            String description = "Include all notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
