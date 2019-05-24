package com.example.recordscreen;
import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import net.vrgsoft.videcrop.VideoCropActivity;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class ViewRecord extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemClickListener {
    private VideoView videoView;
    public static Uri fileuri;
    public static String fileuriPath ="";
    MediaController mediaControls;
    ListView videolist;
    ArrayList<Video> arrayvideo;
    VideoAdapter adapter;
    String path = Environment.getExternalStorageDirectory().toString()+"/Record Screen";
    File directory = new File(path);
    File[] files = directory.listFiles(new findVideo());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Play","viewRecordStart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_record);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        videolist = findViewById(R.id.listView);
        arrayvideo = new ArrayList<>();
        // Kiem tra quyen
        checkAndRequestPermissions();
        adapter = new VideoAdapter(this, R.layout.row_video, arrayvideo);
        videolist.setAdapter(adapter);
        // Import dữ liệu vào list
        getList();
        //Lựa chọn file để play
        videolist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewRecord.this, VideoPlay.class);
                int a = videolist.getItemAtPosition(position).hashCode();
                intent.putExtra("pathIntent", arrayvideo.get(a).getFilepath());
                Log.d("Path là", intent.getStringExtra("pathIntent"));
                startActivity(intent);

            }
        });

        // Chức năng xóa. đổi tên, crop khi long click vào file
        videolist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                showPictureDialog(arrayvideo.get(position).getFilepath());
                return true;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    protected void onStart() {
        Log.d("Play","onStartVideoView");
        super.onStart();
        getList();
    }
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
            Intent intent = new Intent(ViewRecord.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivityIfNeeded(intent, 0);
        } else if (id == R.id.nav_view_record) {

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

    // Hiển thị form lựa chọn chức năng khi nhấn giữ video
    private void showPictureDialog(final String filepath){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        String[] pictureDialogItems = {
                "Rename video",
                "Delete video",
                "Edit video"
        };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                         switch (which) {
                            case 0:
                                renameVideo(filepath);
                                Log.d("Click at","rename");
                                break;
                            case 1:
                                deleteVideo(filepath);
                                Log.d("Click at", "delete");
                                break;
                            case 2:
                                getVideoEdit(filepath);
                                Log.d("Click at","edit");
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public  void getVideoEdit(String filePath){
       Intent intentEdit = new Intent(this, EditRecord.class);
       intentEdit.putExtra("fileInput",filePath);
        Toast.makeText(this,"Select Successfuly, open Edit Video to Crop",Toast.LENGTH_SHORT).show();
        Log.d("File chuyển",filePath);
       startActivity(intentEdit);
    }

    private void deleteVideo(final String path){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Confirm Delete");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(path);
                file.delete();
                //finish();
                startActivity(getIntent());
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    // Đổi tên file
    private void renameVideo(final String path){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Rename");
        alert.setMessage("Enter new name");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String srt1 = input.getEditableText().toString();
                String type = "";
                File file = new File(path);
                Log.d("oldFile name",file.getName());
                Log.d("oldFile paremt", file.getParent());
                if (file.getAbsolutePath().endsWith(".mp4")) {
                    type = ".mp4";
                } else if (file.getAbsolutePath().endsWith(".avi")) {
                    type = ".avi";
                }
                String newpath = file.getParent() +"/"+ srt1+type;
                Log.d("NewPath",newpath);
                File dest = new File(newpath);
                file.renameTo(dest);
                Log.d("newfileName",file.getName());
                finish();
                startActivity(getIntent());
            }

        });

        alert.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();

    }



    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int a = videolist.getItemAtPosition(position).hashCode();
        String fileSelectPath = arrayvideo.get(a).getFilepath();
        Log.d("TAG", fileSelectPath);
        fileuri = Uri.parse(fileSelectPath);

//        fileuriPath = fileuri.toString();
//        Intent intent = new Intent(this, ViewVideoRecord.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivityIfNeeded(intent,0);
//        Log.d("TAG","selectViewVideoRecord");

        setContentView(R.layout.play_video);
        videoView = (VideoView) findViewById(R.id.playVideo);
        if (mediaControls == null) {
            mediaControls = new MediaController(ViewRecord.this);
            mediaControls.setAnchorView(videoView);
        }

        videoView.setMediaController(mediaControls);
        videoView.setVideoURI(fileuri);
        videoView.start();


    }
    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    public class findVideo implements FileFilter {
        // Chỉ chấp nhận 'pathname' là file và có 'phần mở rộng' (extension) là .mp4 hoặc avi
        @Override
        public boolean accept(File pathname) {
            if (!pathname.isFile()) {
                return false;
            }
            if (pathname.getAbsolutePath().endsWith(".mp4")|| pathname.getAbsolutePath().endsWith(".avi")) {
                return true;
            }
            return false;
        }
    }
    public void getList(){
        Log.d("List","PATH: "+path);
        Log.d("List", "SIZE: "+ files.length);
        arrayvideo.clear();
        for(int i = 0;i< files.length;i++){
            arrayvideo.add(new Video (files[i].getName(),files[i].getPath()));
            Log.d("List", "Name: "+ files[i].getName()+ " || PATH: "+ files[i].getPath());
        }
    }

}
