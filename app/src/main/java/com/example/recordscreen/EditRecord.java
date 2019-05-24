package com.example.recordscreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import net.vrgsoft.videcrop.VideoCropActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRecord extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CROP_REQUEST = 200;
    File appFolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Play","editRecordStart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);
        appFolder = new File(Environment.getExternalStorageDirectory() + "/" + "Record Screen");
        if(!appFolder.exists()){
            appFolder.mkdir();
        }
        Intent intent = getIntent();
        String inputPath = intent.getStringExtra("fileInput");
        String filename = new StringBuilder("/Video_").append(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date())).append(".mp4").toString();
        // File outputFile = new File(appFolder, filename);
        String outputPath = appFolder.getAbsolutePath()+ filename;
        //String outputPath = "/storage/emulated/0/Record Screen/video_croped.mp4";
        Log.d("File đầu vào",inputPath);

        startActivityForResult(VideoCropActivity.createIntent(this, inputPath, outputPath), CROP_REQUEST);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_record) {
            Intent intent = new Intent(EditRecord.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        } else if (id == R.id.nav_view_record) {
            Intent intent = new Intent(EditRecord.this, ViewRecord.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_exit) {
            finish();
            System.exit(0);
        } else if (id == R.id.nav_send) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CROP_REQUEST && resultCode == RESULT_OK){
            //crop successful
        }
    }

}
