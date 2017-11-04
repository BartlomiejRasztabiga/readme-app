package pl.infinitefuture.readme.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.ReadMeApplication;
import pl.infinitefuture.readme.books.BooksActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final long ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                NotificationScheduler.setReminder(context, AlarmReceiver.class, 17, 0);
                return;
            }
        }

        //Trigger the notification if time offline is more than 24 hours
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Date lastVisit = new Date(preferences.getLong("last_visit", 0));

        //Send debug info to analytics
        ReadMeApplication application = (ReadMeApplication) context.getApplicationContext();
        Tracker tracker = application.getDefaultTracker();

        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Debug")
                .setAction("AlarmReceiver onReceive")
                .set("date_now", String.valueOf(new Date().getTime()))
                .set("last_visit", String.valueOf(lastVisit.getTime()))
                .build());


        if (new Date().getTime() - lastVisit.getTime() >= ONE_DAY_IN_MILLIS) {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("Action")
                    .setAction("Show notification")
                    .build());

            NotificationScheduler.showNotification(context, BooksActivity.class,
                    context.getString(R.string.notification_title),
                    context.getString(R.string.notification_content));
        }
    }
}