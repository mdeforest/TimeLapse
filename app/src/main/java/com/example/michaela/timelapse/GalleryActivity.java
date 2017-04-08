package com.example.michaela.timelapse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import java.io.File;


//Activity to show all recorded timelapse videos in a gallery
public class GalleryActivity extends AppCompatActivity {
    private String pathName;
    private File mediaStorageDir;
    private File[] videoFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "TimeLapse");

        videoFiles = mediaStorageDir.listFiles();

        GridView gridView = (GridView) findViewById(R.id.gallery_grid);
        gridView.setAdapter(new ImageAdapter(this));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Open and play video

                pathName = videoFiles[position].getPath();

                Intent i = new Intent(GalleryActivity.this, PlaybackActivity.class);
                i.putExtra("Video File", pathName);
                startActivity(i);

            }
        });
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumb.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        //create a new ImageView for each video
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {
                //if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(192, 192));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(mThumb[position]);
            return imageView;
        }
        //getImages
        private Bitmap[] mThumb = getThumbnails();
    }

    //gets Thumbnails for saved Video Files
    public Bitmap[] getThumbnails() {
        //Get thumbnails
        Bitmap[] mThumb = new Bitmap[videoFiles.length];

        for(int i = 0; i < videoFiles.length; i++) {
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoFiles[i].getPath(),
                    MediaStore.Images.Thumbnails.MINI_KIND);

            mThumb[i] = thumb;
        }
        return mThumb;
    }
}
