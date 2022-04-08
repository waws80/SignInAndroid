package edu.signin.android;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;

import edu.signin.android.adapter.ActivityAdapter;
import edu.signin.android.bean.ActivityBean;
import edu.signin.android.common.BaseActivity;
import edu.signin.android.databinding.ActivityMainBinding;
import edu.signin.android.utils.HttpUtils;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private ActivityAdapter activityAdapter = new ActivityAdapter();

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState) {
        requestPermission();


        mViewBinding.rv.setLayoutManager(new GridLayoutManager(this, 1));

        mViewBinding.rv.setAdapter(activityAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getActivityList();
    }

    private void getActivityList() {

        HttpUtils.getInstance().get("activity/search", new HttpCallback(this) {
            @Override
            protected void onData(HttpUtils.ResultBean bean) {
                activityAdapter.setData(bean.getList(ActivityBean.class));
            }
        });
    }

    private void requestPermission(){
        String [] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BODY_SENSORS, Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.BODY_SENSORS, Manifest.permission.ACTIVITY_RECOGNITION};
        }
        requestPermissions(permissions, 100);
    }
}