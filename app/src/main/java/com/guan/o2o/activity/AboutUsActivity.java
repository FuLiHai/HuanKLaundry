package com.guan.o2o.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.guan.o2o.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 关于我们页面
 *
 * @author Guan
 * @file com.guan.o2o.activity
 * @date 2015/10/26
 * @Version 1.0
 */
public class AboutUsActivity extends FrameActivity {

    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_website)
    TextView tvWebsite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ButterKnife.inject(this);

        /**
         * 初始化变量
         */
        initVariable();
    }

    /**
     * 初始化变量
     */
    private void initVariable() {
        tvTitle.setText(R.string.title_about_us);
    }

    /**
     * 监听实现
     */
    @OnClick({R.id.iv_back,R.id.tv_website})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.tv_website:
                openActivity(AboutAppActivity.class);
                break;

            default:
                break;
        }

    }
}
