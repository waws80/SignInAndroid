package edu.signin.android.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.HashMap;

import edu.signin.android.MainActivity;
import edu.signin.android.bean.UserBean;
import edu.signin.android.common.BaseActivity;
import edu.signin.android.databinding.ActivityRegisterBinding;
import edu.signin.android.utils.HttpUtils;
import edu.signin.android.utils.UserDefault;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    @Override
    protected void onLoad(@Nullable Bundle savedInstanceState) {

        mViewBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mViewBinding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    private void register() {

        String scode = mViewBinding.edtNumber.getText().toString();
        String pwd = mViewBinding.edtPwd.getText().toString();
        String rePwd = mViewBinding.edtPwdRe.getText().toString();
        String name = mViewBinding.edtName.getText().toString();
        String sclass = mViewBinding.edtClass.getText().toString();

        if (scode.isEmpty() || pwd.isEmpty() || rePwd.isEmpty() || name.isEmpty() || sclass.isEmpty()){
            toast("请输入信息");
            return;
        }
        if (!pwd.equals(rePwd)){
            toast("两次输入密码不一致");
            return;
        }

        HashMap<String, String> body = new HashMap<>();
        body.put("scode", scode);
        body.put("sname", name);
        body.put("spwd", pwd);
        body.put("sclass", sclass);

        HttpUtils.getInstance().post("user/register", body, new HttpCallback(this) {
            @Override
            protected void onData(HttpUtils.ResultBean bean) {
                UserDefault.getInstance().putUserInfo(bean.getEntity(UserBean.class));
                setResult(Activity.RESULT_OK);
                onBackPressed();
            }
        });

    }
}