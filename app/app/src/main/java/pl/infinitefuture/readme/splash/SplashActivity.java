package pl.infinitefuture.readme.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import pl.infinitefuture.readme.books.BooksActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, BooksActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_TIME_OUT);

    }
}
