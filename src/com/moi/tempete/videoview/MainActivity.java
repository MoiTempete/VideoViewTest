package com.moi.tempete.videoview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by MoiTempete.
 */
public class MainActivity extends Activity implements FullScreenUtil.OnViewZoomListener{

    private static final String TAG = MainActivity.class.getSimpleName();

    private VideoView mVideoView;

    private RelativeLayout mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        FullScreenUtil.initParamsWithDisplay(this);
        mVideoView = (VideoView) findViewById(R.id.vv_main);
        mPlayer = (RelativeLayout) findViewById(R.id.rl_player);
        final ImageView iv = (ImageView) findViewById(R.id.iv0);
        mVideoView.requestFocus();
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                mVideoView.pause();
                FullScreenUtil.zoomView(MainActivity.this, mPlayer, MainActivity.this, 0);
            }
        });

        String url1 = "/storage/external_storage/sda1/the.big.bang.theory.0812.mp4";
        mVideoView.setVideoPath(url1);

        Log.i(TAG, "path = " + url1);
        Log.i(TAG, "file exists? " + isFileExist(url1));
        mVideoView.start();
    }

    private boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    @Override
    public void onViewZoomStart() {

    }

    @Override
    public void onViewZoomEnd() {
        mVideoView.start();
        mVideoView.requestFocus();
    }

    @Override
    public void onViewZoomRepeat() {

    }

}
