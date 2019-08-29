package soptqs.paste.activities;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import soptqs.paste.R;

/**
 * Created by S0ptq on 2018/2/5.
 */

public class MainIntroActivity extends IntroActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setButtonBackVisible(true);
        setButtonNextVisible(true);

        addSlide(new SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.intro_content)
                .image(R.mipmap.ic_hires)
                .background(R.color.colorGreyBG)
                .backgroundDark(R.color.colorNotThatWhite)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title2)
                .description(R.string.intro_content2)
                .image(R.mipmap.intro2)
                .background(R.color.colorLime)
                .backgroundDark(R.color.colorLimeDark)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title(R.string.intro_title3)
                .description(R.string.intro_content3)
                .image(R.mipmap.intro1)
                .background(R.color.colorCyan)
                .backgroundDark(R.color.colorCyanDark)
                .build());

    }
}
