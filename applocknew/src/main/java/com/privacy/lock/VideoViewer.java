package com.privacy.lock;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.security.manager.AppsCore;
import com.security.manager.NormalApi;
import com.security.manager.SafeApi;
import com.security.manager.lib.async.LoadingTask;
import com.privacy.model.FileType;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by superjoy on 2014/9/12.
 */
public class VideoViewer extends PictureViewer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    String fileName;
    MediaController ctrl;

    @InjectView(R.id.video_preview)
    VideoView vv;
    int idx = 0;
    int which;
    long id;

    @Override
    public void onBackPressed() {
        askForExit();
    }

    LoadingTask normalTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            vv.pause();
            HandleFileService.handleSingleNormalFile(entry, fileName, id);
//            MNormalFile.update(action, entry);
            setResult(RESULT_OK);
            finish();
        }
    };

    LoadingTask safeTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            vv.pause();
            HandleFileService.handleSingleSafeFile(entry, fileName, id);
//            MSafeFile.update(action, entry);
            setResult(RESULT_OK);
            finish();
        }
    };

    @Override
    protected LoadingTask getTask(boolean normal) {
        return normal ? normalTask : safeTask;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vp_idx", idx);
        outState.putString("fileName", fileName);
        outState.putLong("long_id", id);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        super.onRestoreInstanceStateOnCreate(savedInstanceState);
        idx = savedInstanceState.getInt("vp_idx");
        id = savedInstanceState.getLong("long_id");
        fileName = savedInstanceState.getString("fileName");
    }

    @Override
    protected void onIntent(Intent intent) {
        super.onIntent(intent);
        which = intent.getIntExtra("entry", 0);
        fileName = intent.getStringExtra("name");
        id = intent.getLongExtra("id", 0L);
    }

    @Override
    public void setupView() {
        setContentView(R.layout.video_view_new);
        ButterKnife.inject(this);

        findViewById(R.id.help).setVisibility(View.GONE);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        bottomAB.setVisibility(View.VISIBLE);
        setup(R.string.empty);

        final Timer t = new Timer("wa");
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomAB.setVisibility(View.INVISIBLE);
                        topAB.setVisibility(View.INVISIBLE);
                        bottomAB.startAnimation(fadeout);
                        topAB.startAnimation(fadeout);
                    }
                });
                t.cancel();
            }
        }, 2000);

        try {
            setViewVisible(View.GONE, R.id.search_button, R.id.setting, R.id.del, R.id.select_all);
            if (!normal) {
                title.setText(fileName);
                ImageButton decrypt = (ImageButton) findViewById(R.id.encrypt);
                decrypt.setImageResource(R.drawable.icon);
                String p = AppsCore.p(fileName, true);
                vv.setVideoPath(p);
            } else {
                title.setText(fileName.substring(fileName.lastIndexOf('/') + 1));
                vv.setVideoPath(fileName);
            }
            vv.setOnCompletionListener(this);
            vv.setOnErrorListener(this);
            findViewById(R.id.video).setOnClickListener(new View.OnClickListener() {
                byte showctrl = 0;

                @Override
                public void onClick(View view) {
                    switch (showctrl) {
                        case 2:
                            showctrl = 0;
                            ctrl.show();
                            bottomAB.setVisibility(View.INVISIBLE);
                            topAB.setVisibility(View.INVISIBLE);
                            break;

                        case 0:
                            ctrl.hide();
                            bottomAB.setVisibility(View.VISIBLE);
                            topAB.setVisibility(View.VISIBLE);
                            bottomAB.startAnimation(fadein);
                            topAB.startAnimation(fadein);
                            showctrl = 1;
                            break;

                        case 1:
                            ctrl.hide();
                            bottomAB.setVisibility(View.INVISIBLE);
                            topAB.setVisibility(View.INVISIBLE);
                            bottomAB.startAnimation(fadeout);
                            topAB.startAnimation(fadeout);
                            showctrl = 2;
                            break;
                    }
                }
            });
            if (ctrl == null) {
                ctrl = new MediaController(this, true);
                vv.setMediaController(ctrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        if (normal) {
            NormalApi.instance(this).waiting(FileType.TYPE_VIDEO, action);
        } else {
            SafeApi.instance(this).waiting(action);
        }
    }

    private Runnable action = new Runnable() {
        @Override
        public void run() {
            try {
                if (normal) {
                    entry = NormalApi.instance(getApplicationContext()).getFolder(which);
                } else {
                    entry = SafeApi.instance(getApplicationContext()).getFolder(FileType.TYPE_VIDEO, which);
                }
                vv.start();
                vv.seekTo(idx);
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        }
    };

    Animation fadein, fadeout;

    @Override
    protected void onPause() {
        idx = vv.getCurrentPosition();
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (!normal) {
            AppsCore.p(fileName, false);
        }
        super.onStop();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        finish();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        finish();
        return true;
    }
}
