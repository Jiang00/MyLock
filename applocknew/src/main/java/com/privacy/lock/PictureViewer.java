package com.privacy.lock;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;

import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable;
import com.diegocarloslima.byakugallery.lib.TouchImageView;
import com.security.manager.AppsCore;
import com.security.manager.NormalApi;
import com.security.manager.SafeApi;
import com.security.manager.lib.async.LoadingTask;
import com.privacy.model.FileType;
import com.privacy.model.FolderEntry;

/**
 * Created by superjoy on 2014/9/19.
 */
public class PictureViewer extends AbsActivity {
    boolean normal;

    Animation fadein, fadeout;

    @InjectView(R.id.bottom_action_bar)
    LinearLayout bottomAB;

    @InjectView(R.id.action_bar)
    FrameLayout topAB;

    @Optional
    @InjectView(R.id.viewer)
    ViewPager pager;

    @InjectView(R.id.title)
    TextView title;

    @Override
    protected int getBackImage() {
        return R.drawable.ic_action_back;
    }

    ProgressDialog dialog;

    LoadingTask safeTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            String file = entry.getFile(pager.getCurrentItem());
//            cursor.moveToPosition(pager.getCurrentItem());
//            String fileName = cursor.getString(1);
//            long id = cursor.getLong(0);
            HandleFileService.handleSingleSafeFile(entry, file, 0);
            SafeApi.instance(getApplicationContext()).waiting(action);
//            currentPath = entry.bucketUrl + fileName;
//            MSafeFile.update(action, entry);
            modified = true;
        }
    };

    LoadingTask normalTask = new LoadingTask() {
        @Override
        protected void doInBackground() {
            String file = entry.getFile(pager.getCurrentItem());
            long id = entry.getFileId(pager.getCurrentItem());
//            cursor.moveToPosition(pager.getCurrentItem());
//            String fileName = cursor.getString(1);
//            long id = cursor.getLong(0);
            HandleFileService.handleSingleNormalFile(entry, file, id);
            NormalApi.instance(getApplicationContext()).waiting(FileType.TYPE_PIC, action);
//            MNormalFile.update(action, entry);
            modified = true;
        }
    };

    boolean modified;

    @Override
    public void onBackPressed() {
        if (modified) {
            setResult(RESULT_OK);
        }
        askForExit();
    }

    public void showProgressDialog() {
        try {
            dialog = new ProgressDialog(context);
            dialog.setTitle(normal ? R.string.encrypt_title : R.string.decrypt_title);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected LoadingTask getTask(boolean normal) {
        return normal ? normalTask : safeTask;
    }

    @OnClick(R.id.encrypt)
    public void onAction() {
        if (normal) {
            new AlertDialog.Builder(context).setTitle(R.string.encrypt_title).setIcon(R.drawable.icon).setMessage(R.string.encrypt_desc).setPositiveButton(R.string.encrypt_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showProgressDialog();
                    getTask(true).start();
                }
            }).setNegativeButton(android.R.string.no, null).create().show();
        } else {
            new AlertDialog.Builder(context).setTitle(R.string.decrypt_title).setIcon(R.drawable.icon).setMessage(R.string.decrypt_desc).setPositiveButton(R.string.decrypt_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    showProgressDialog();
                    getTask(false).start();
                }
            }).setNegativeButton(android.R.string.no, null).create().show();
        }
    }

    View.OnClickListener toggleUI = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (bottomAB.getVisibility() == View.VISIBLE) {
                bottomAB.setVisibility(View.GONE);
                topAB.setVisibility(View.GONE);
                topAB.startAnimation(fadeout);
                bottomAB.startAnimation(fadeout);
            } else {
                bottomAB.setVisibility(View.VISIBLE);
                topAB.setVisibility(View.VISIBLE);
                topAB.startAnimation(fadein);
                bottomAB.startAnimation(fadein);
            }
        }
    };

    @Override
    protected boolean hasHelp() {
        return false;
    }

    int which;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("which", which);
        outState.putBoolean("normal", normal);
        outState.putInt("entry", entryIdx);
    }

    @Override
    protected void onRestoreInstanceStateOnCreate(Bundle savedInstanceState) {
        super.onRestoreInstanceStateOnCreate(savedInstanceState);
        which = savedInstanceState.getInt("which");
        normal = savedInstanceState.getBoolean("normal");
        entryIdx = savedInstanceState.getInt("entry");
    }

    int entryIdx;
    @Override
    protected void onIntent(Intent intent) {
        entryIdx = intent.getIntExtra("entry", 0);
        which = intent.getIntExtra("file", 0);
        normal = intent.getBooleanExtra("normal", false);
    }

    int count;
    protected FolderEntry entry;
    String currentPath;

    @Override
    public void setupView() {
        setContentView(R.layout.photo_view);
        ButterKnife.inject(this);

        findViewById(R.id.help).setVisibility(View.GONE);
        bottomAB.setVisibility(View.VISIBLE);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fadein);
        fadeout = AnimationUtils.loadAnimation(this, R.anim.fadeout);
        bottomAB.findViewById(R.id.select_all).setVisibility(View.GONE);
        setup(R.string.empty);

        pager.setOffscreenPageLimit(1);
        final PagerAdapter adapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return count;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ViewGroup tmp = (ViewGroup) LayoutInflater.from(container.getContext())
                        .inflate(R.layout.security_gallary_item, container, false);

                String fileName = entry.getFile(position);
                if (!normal) {
                    fileName = AppsCore.p(fileName, true);
                }

                final View progress = tmp.findViewById(R.id.gallery_view_pager_sample_item_progress);
                ImageView image = (ImageView) tmp.findViewById(R.id.gallery_view_pager_sample_item_image);
                image.setOnClickListener(toggleUI);
                TileBitmapDrawable.attachTileBitmapDrawable(image, fileName, null, new TileBitmapDrawable.OnInitializeListener() {

                    @Override
                    public void onStartInitialization() {
                        progress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onEndInitialization() {
                        progress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception ex) {
                        progress.setVisibility(View.GONE);
                    }
                });

                container.addView(tmp, 0);
                return tmp;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                if (!normal) {
                    AppsCore.p(currentPath, false);
                }
                ViewGroup v = (ViewGroup) object;
                container.removeView(v);
                ((TouchImageView) v.getChildAt(0)).setImageDrawable(null);
            }
        };
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                String file = entry.getFile(i);
                title.setText(file.substring(file.lastIndexOf('/') + 1));
                which = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setViewVisible(View.GONE, R.id.search_button, R.id.setting, R.id.del);

        if (normal) {
            NormalApi.instance(this).waiting(FileType.TYPE_PIC, action);
        } else {
            ImageButton decrypt = (ImageButton) findViewById(R.id.encrypt);
            decrypt.setImageResource(R.drawable.icon);
            SafeApi.instance(this).waiting(action);
        }
    }

    private Runnable action = new Runnable() {
        @Override
        public void run() {
            try {
                entry = normal ? NormalApi.instance(getApplicationContext()).getFolder(entryIdx)
                    : SafeApi.instance(getApplicationContext()).getFolder(FileType.TYPE_PIC, entryIdx);
                count = entry.count();
                if (count == 0) throw new RuntimeException();
                pager.getAdapter().notifyDataSetChanged();
                pager.setCurrentItem(which);
                String file = entry.getFile(which);
                title.setText(file.substring(file.lastIndexOf('/') + 1));
            } catch (Exception e) {
                e.printStackTrace();
                setResult(RESULT_OK);
                finish();
            } finally {
                if (dialog != null) {
                    dialog.cancel();
                }
            }
        }
    };
}
