package udacity.com.capstone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import udacity.com.capstone.R;

public class IntroductionActivity extends AppIntro {

//    @BindView(R.id.viewpagerr)
//    public ViewPager mViewPager;
//    @BindView(R.id.icon_pager_indicator)
//    IconPageIndicator mIconPageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_one),
                getString(R.string.tutorial_one), R.drawable.ic_gps,
                getResources().getColor(R.color.slide_1)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_two),
                getString(R.string.tutorial_two), R.drawable.ic_help,
                getResources().getColor(R.color.slide_2)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_three),
                getString(R.string.tutorial_three), R.drawable.ic_track,
                getResources().getColor(R.color.slide_3)));
        addSlide(AppIntroFragment.newInstance(getString(R.string.tutorial_title_four),
                getString(R.string.tutorial_four), R.drawable.ic_thumb_up,
                getResources().getColor(R.color.slide_4)));
        showSkipButton(false);
        setProgressButtonEnabled(true);
    }


    private void showHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        showHome();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        showHome();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
