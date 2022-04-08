package edu.signin.android.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import edu.signin.android.bean.ActivityBean;
import edu.signin.android.common.BaseActivity;
import edu.signin.android.databinding.ActivityDetailBinding;
import edu.signin.android.utils.GsonUtils;
import edu.signin.android.utils.HttpUtils;

public class ActivityDetailActivity extends BaseActivity<ActivityDetailBinding> {

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState) {
        mViewBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        String item = getIntent().getStringExtra("item");

        if (item == null || item.isEmpty()){
            toast("非法访问");
            return;
        }

        ActivityBean bean = GsonUtils.toEntity(ActivityBean.class, item);


    }


    private void checkSign(){
        HttpUtils.getInstance().get("signin/search_sign_user", new HttpCallback(this) {
            @Override
            protected void onData(HttpUtils.ResultBean bean) {

            }
        });
    }
}