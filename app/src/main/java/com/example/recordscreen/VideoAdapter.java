package com.example.recordscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class VideoAdapter extends BaseAdapter {
    public VideoAdapter(Context context, int layout, List<Video> videoList) {
        this.context = context;
        this.layout = layout;
        this.videoList = videoList;
    }
    private Context context;
    public List<Video> videoList;
    private int layout;

    @Override
    public int getCount()
    {
        return videoList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    private class ViewHolder{
        ImageView imgThumbnail;
        TextView txtTitle;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewgroup) {
        ViewHolder holder;
        if(convertview == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertview = inflater.inflate(layout,null);
            holder.txtTitle = (TextView) convertview.findViewById(R.id.titleView);
            holder.imgThumbnail = (ImageView) convertview.findViewById(R.id.imageView);
            convertview.setTag(holder);
        }else{
            holder = (ViewHolder) convertview.getTag();
        }
        Video video  = videoList.get(i);
        holder.txtTitle.setText(video.getFilename());
        Uri uri = Uri.fromFile(new File(video.getFilepath()));
        Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(ThumbnailUtils.createVideoThumbnail(video.getFilepath(),
                        MediaStore.Video.Thumbnails.MINI_KIND), 60, 80);
        if(bmThumbnail != null) {
            holder.imgThumbnail.setImageBitmap(bmThumbnail);
        }
        return convertview;
    }
}
