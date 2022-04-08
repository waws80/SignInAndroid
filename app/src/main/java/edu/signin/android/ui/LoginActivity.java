package edu.signin.android.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import edu.signin.android.MainActivity;
import edu.signin.android.bean.UserBean;
import edu.signin.android.common.BaseActivity;
import edu.signin.android.databinding.ActivityLoginBinding;
import edu.signin.android.utils.HttpUtils;
import edu.signin.android.utils.UserDefault;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState) {

        mViewBinding.tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 100);
            }
        });

        mViewBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    private void login() {
        String scode = mViewBinding.edtCode.getText().toString();
        String pwd = mViewBinding.edtPwd.getText().toString();

        if (scode.isEmpty() || pwd.isEmpty()){
            toast("请输入登录信息");
            return;
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("scode", scode);
        body.put("spwd", pwd);

        HttpUtils.getInstance().post("user/login", body, new HttpCallback(this) {
            @Override
            protected void onData(HttpUtils.ResultBean bean) {
                UserDefault.getInstance().putUserInfo(bean.getEntity(UserBean.class));
                LoginActivity.this.start(MainActivity.class);
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            start(MainActivity.class);
            onBackPressed();
        }
    }
}