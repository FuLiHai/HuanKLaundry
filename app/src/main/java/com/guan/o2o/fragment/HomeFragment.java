package com.guan.o2o.fragment;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.guan.o2o.R;
import com.guan.o2o.adapter.PollPagerAdapter;
import com.guan.o2o.application.App;
import com.guan.o2o.common.Contant;
import com.guan.o2o.model.WashOrder;
import com.guan.o2o.utils.CustomMsyhTV;
import com.guan.o2o.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * 主页Fragment
 *
 * @author Guan
 * @file com.guan.o2o.fragment
 * @date 2015/9/29
 * @Version 1.0
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {

    @InjectView(R.id.tv_city)
    TextView tvCity;
    @InjectView(R.id.iv_below)
    ImageView ivBelow;
    @InjectView(R.id.viewpager)
    public
    ViewPager viewpager;
    @InjectView(R.id.iv_a_wash)
    ImageView ivAWash;
    @InjectView(R.id.iv_bag_wash)
    ImageView ivBagWash;
    @InjectView(R.id.iv_home_ariticles)
    ImageView ivHomeAriticles;
    @InjectView(R.id.iv_other_wash)
    ImageView ivOtherWash;
    @InjectView(R.id.iv_service_note)
    ImageView ivServiceNote;
    @InjectView(R.id.llyt_dots)
    LinearLayout llytDots;

    private int mNum;
    private int[] imageUrls;
    private int mCurrentItem;
    private TextView mTvNum;
    private WashOrder washOrder;
    private PopupWindow mPopupWindow;
    private ImageView[] mImageViews;
    private ImageHandler mImageHandler;
    private OnClickListener mCallback;
    public LocationClient mLocationClient;
    public BDLocationListener myListener;
    // 定时周期执行指定的任务
    private ScheduledExecutorService mScheduledExecutorService;

    /**
     * Handler来处理ViewPager的轮播,实现定时更新
     */
    private class ImageHandler extends Handler {

        private WeakReference<HomeFragment> mWeakReference;

        // 使用弱引用避免Handler泄露,泛型参数可以是Activity/Fragment
        public ImageHandler(HomeFragment fragment) {
            mWeakReference = new WeakReference<HomeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Contant.MSG_UPDATE_IMAGE:
                    mWeakReference.get().viewpager.setCurrentItem(mCurrentItem);
                    break;

                default:
                    break;
            }
        }
    }

    // 存放fragment的Activtiy必须实现的接口
    public interface OnClickListener {
        public void onHomeIntentSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // 为保证Activity容器实现以回调的接口,如果没会抛出一个异常。
        try {
            mCallback = (OnClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ButterKnife.inject(this, super.onCreateView(inflater, container, savedInstanceState));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 开始轮播图切换
     */
    @Override
    public void onStart() {
        super.onStart();
        mScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 2, 3, TimeUnit.SECONDS);
    }

    /**
     * 实现父类方法
     *
     * @param inflater
     * @return
     */
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View _view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, _view);
        return _view;
    }

    /**
     * 初始化位置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );
        option.setCoorType("bd09ll");
        int span = 1000;
        option.setScanSpan(span);
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);
        mLocationClient.setLocOption(option);
    }

    /**
     * 初始化变量
     */
    public void initVariable() {
        imageUrls = new int[]{
                R.mipmap.ic_poll_a, R.mipmap.ic_poll_c,
                R.mipmap.ic_poll_b, R.mipmap.ic_poll_d
        };
        washOrder = null;
        // 设定大大的值实现向左回播
        mCurrentItem = imageUrls.length * 1000;
        mImageHandler = new ImageHandler(HomeFragment.this);

        // 地图
        mLocationClient = new LocationClient(getActivity());
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        /*
         * 初始化ViewPager
         */
        initViewPager();
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPager() {
        LayoutInflater _inflater = LayoutInflater.from(getActivity());
        ArrayList<View> _listViews = new ArrayList<View>();
        mImageViews = new ImageView[imageUrls.length];
        for (int i = 0; i < imageUrls.length; i++) {
            // 图片
            View _view = (View) _inflater.inflate(R.layout.view_pager, null);
            _view.setBackgroundResource(imageUrls[i]);
            _listViews.add(_view);
            // 圆点
            mImageViews[i] = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(16, 16);
            params.setMargins(7, 10, 7, 10);
            mImageViews[i].setLayoutParams(params);
            if (0 == i)
                mImageViews[i].setBackgroundResource(R.mipmap.ic_dot_c);
            else
                mImageViews[i].setBackgroundResource(R.mipmap.ic_dot);
            llytDots.addView(mImageViews[i]);
        }
        viewpager.setAdapter(new PollPagerAdapter(_listViews));
        viewpager.addOnPageChangeListener(new onPageChangeListener());
        viewpager.setCurrentItem(mCurrentItem);
    }

    /**
     * 轮询页面监听
     */
    private class onPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            mCurrentItem = position;
            // 更新小圆点图标
            for (int i = 0; i < imageUrls.length; i++)
                if (position % imageUrls.length == i)
                    mImageViews[i].setBackgroundResource(R.mipmap.ic_dot_c);
                else
                    mImageViews[i].setBackgroundResource(R.mipmap.ic_dot);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    }

    /**
     * 执行轮播图切换任务
     */
    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (viewpager) {
                mCurrentItem++;
                // 通过handler切换图片
                mImageHandler.sendEmptyMessage(Contant.MSG_UPDATE_IMAGE);
            }
        }
    }

    /**
     * 监听实现
     */
    @OnClick({R.id.tv_city, R.id.iv_below, R.id.iv_a_wash, R.id.iv_bag_wash, R.id.iv_home_ariticles, R.id.iv_other_wash, R.id.iv_service_note})
    public void OnClick(View view) {

        switch (view.getId()) {
            case R.id.tv_city:
                break;

            case R.id.iv_below:
                break;

            case R.id.iv_a_wash:
                break;

            case R.id.iv_bag_wash:
                // popwindow
                if (mPopupWindow != null && mPopupWindow.isShowing())
                    mPopupWindow.dismiss();
                else
                    showPopupWindow(view);
                break;

            case R.id.iv_home_ariticles:
                break;

            case R.id.iv_other_wash:
                break;

            case R.id.iv_service_note:
                break;

            default:
                break;
        }
    }

    /**
     * 定义袋洗popupwindow
     *
     * @param view
     */
    private void showPopupWindow(View view) {
        View contentView = LayoutInflater.from(getActivity()).inflate(
                R.layout.view_pop_bagwash, null);
        CustomMsyhTV cvPrice = ButterKnife.findById(contentView, R.id.cv_price);
        RadioButton rbMin = ButterKnife.findById(contentView, R.id.rb_min);
        mTvNum = ButterKnife.findById(contentView, R.id.tv_num);
        RadioButton rbAdd = ButterKnife.findById(contentView, R.id.rb_add);
        Button btnPay = ButterKnife.findById(contentView, R.id.btn_pay);
        mNum = 1;
        cvPrice.setText(Contant.PRICE_BAGWASH);
        rbMin.setOnClickListener(this);
        rbAdd.setOnClickListener(this);
        btnPay.setOnClickListener(this);
        // PopupWindow显示位置
        mPopupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, 594, true);
        // 接收点击事件
        mPopupWindow.setFocusable(true);
        // 触摸
        mPopupWindow.setOutsideTouchable(true);
        // 必须实现,否则点击外部区域和Back键都无法dismiss
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
        backgroundAlpha(0.5f);
        // 显示
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 40);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
            }
        });
    }

    /**
     * 袋洗popupwindow内容的监听实现
     *
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_min:
                if (mNum > 1) {
                    mNum = mNum - 1;
                    mTvNum.setText(String.valueOf(mNum));
                } else
                    mTvNum.setText(String.valueOf(1));
                break;

            case R.id.rb_add:
                mNum = mNum + 1;
                mTvNum.setText(String.valueOf(mNum));
                break;

            case R.id.btn_pay:
                String bagWash = getString(R.string.text_bag_wash);
                if (App.washOrderList.size() == 0)
                    App.washOrderList.add(new WashOrder(bagWash, mNum, Contant.PRICE_BAGWASH));
                else
                    for (int i = 0; i < App.washOrderList.size(); i++) {
                        WashOrder washOrder = App.washOrderList.get(i);
                        if (washOrder.getWashCategory().equals(bagWash)) {
                            washOrder.setWashNum(washOrder.getWashNum() + mNum);
                            break;
                        }
                    }
                mPopupWindow.dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        // 0.0-1.0
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    /**
     * 位置监听
     */
    public class MyLocationListener implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation poiLocation) {
            tvCity.setText(poiLocation.getCity());
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    /**
     * 停止轮播图切换
     */
    @Override
    public void onStop() {
        super.onStop();
        mScheduledExecutorService.shutdown();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLocationClient.stop();
        ButterKnife.reset(this);
    }
}
