package com.keepa.kpa;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.util.Log;

public class JobSchedulerService extends JobService {

    public static final String TAG = "JobSchedulerService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "JobSchedulerService onStartJob");
        try {
            startService(new Intent(this, PersistService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "JobSchedulerService onStopJob");
        return false;
    }
}
