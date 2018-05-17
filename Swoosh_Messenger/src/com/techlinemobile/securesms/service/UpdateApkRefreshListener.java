package com.techlinemobile.securesms.service;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.techlinemobile.securesms.ApplicationContext;
import com.techlinemobile.securesms.BuildConfig;
import com.techlinemobile.securesms.jobs.UpdateApkJob;
import com.techlinemobile.securesms.util.TextSecurePreferences;

import java.util.concurrent.TimeUnit;

public class UpdateApkRefreshListener extends PersistentAlarmManagerListener {

  private static final String TAG = UpdateApkRefreshListener.class.getSimpleName();

  private static final long INTERVAL = TimeUnit.HOURS.toMillis(6);

  @Override
  protected long getNextScheduledExecutionTime(Context context) {
    return TextSecurePreferences.getUpdateApkRefreshTime(context);
  }

  @Override
  protected long onAlarm(Context context, long scheduledTime) {
    Log.w(TAG, "onAlarm...");

    if (scheduledTime != 0 && BuildConfig.PLAY_STORE_DISABLED) {
      Log.w(TAG, "Queueing APK update job...");
      ApplicationContext.getInstance(context)
                        .getJobManager()
                        .add(new UpdateApkJob(context));
    }

    long newTime = System.currentTimeMillis() + INTERVAL;
    TextSecurePreferences.setUpdateApkRefreshTime(context, newTime);

    return newTime;
  }

  public static void schedule(Context context) {
    new UpdateApkRefreshListener().onReceive(context, new Intent());
  }

}