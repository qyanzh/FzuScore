package com.example.fzuscore.Activities;

import com.example.fzuscore.R;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class BaseActivity extends SwipeBackActivity {
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.base_slide_remain, R.anim.base_slide_right_out);
    }

}
