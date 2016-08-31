package io.github.ryanhoo.music.ui;

import android.os.Bundle;
import io.github.ryanhoo.music.R;
import io.github.ryanhoo.music.ui.base.BaseActivity;

public class SplashScreenActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }
}
