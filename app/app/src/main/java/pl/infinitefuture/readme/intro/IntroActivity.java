package pl.infinitefuture.readme.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import java.util.Date;

import pl.infinitefuture.readme.R;
import pl.infinitefuture.readme.books.BooksActivity;
import pl.infinitefuture.readme.books.persistence.Book;
import pl.infinitefuture.readme.splash.SplashActivity;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add your slide fragments here.
        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getString(R.string.intro_1_title));
        sliderPage1.setDescription(getString(R.string.intro_1_desc));
        sliderPage1.setBgColor(getResources().getColor(R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.intro_2_title));
        sliderPage2.setDescription(getString(R.string.intro_2_desc));
        sliderPage2.setImageDrawable(R.mipmap.ic_intro_2);
        sliderPage2.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.intro_3_title));
        sliderPage3.setDescription(getString(R.string.intro_3_desc));
        sliderPage3.setImageDrawable(R.mipmap.ic_intro_3);
        sliderPage3.setBgColor(getResources().getColor(R.color.colorPrimaryDark));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getString(R.string.intro_4_title));
        sliderPage4.setDescription(getString(R.string.intro_4_desc));
        sliderPage4.setBgColor(getResources().getColor(R.color.colorPrimary));
        addSlide(AppIntroFragment.newInstance(sliderPage4));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        navigateToMainActivity();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        navigateToMainActivity();
    }

    private void navigateToMainActivity() { //and set first_run to false
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean("first_run", false)
                .apply();

        Intent intent = new Intent(IntroActivity.this, BooksActivity.class);
        startActivity(intent);
        finish();
    }
}