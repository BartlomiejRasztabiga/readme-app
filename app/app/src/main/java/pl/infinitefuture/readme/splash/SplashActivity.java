package pl.infinitefuture.readme.splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import pl.infinitefuture.readme.books.BooksActivity;
import pl.infinitefuture.readme.notifications.AlarmReceiver;
import pl.infinitefuture.readme.notifications.NotificationScheduler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //start notification alarm
        NotificationScheduler.setReminder(SplashActivity.this, AlarmReceiver.class, 17, 0);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putLong("last_visit", new Date().getTime())
                .apply();

        Intent intent = new Intent(SplashActivity.this, BooksActivity.class);
        startActivity(intent);
        finish();
    }
}
