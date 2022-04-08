package edu.signin.android.utils;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import edu.signin.android.SignInApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * http
 */
public final class HttpUtils {

    private static final String IP = "192.168.1.145:8081";

    public static final String BASE_URL = "http://" + IP + "/signin/";


    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();



    private HttpUtils(){}

    public static HttpUtils getInstance(){
        return new HttpUtils();
    }


    public void get(String path, DataCallback callback){
        this.call(buildRequest("GET", path, new HashMap<>()), callback);

    }

    public void post(String path, HashMap<String, String> body, DataCallback callback){
        this.call(buildRequest("POST", path, body), callback);
    }

    public void put(String path, HashMap<String, String> body, DataCallback callback){
        this.call(buildRequest("PUT", path, body), callback);
    }

    private Request buildRequest(String method, String path, HashMap<String, String> body){

        MultipartBody.Builder  builder = new MultipartBody.Builder();
        for (Map.Entry<String, String> stringStringEntry : body.entrySet()) {
            builder.addFormDataPart(stringStringEntry.getKey(), stringStringEntry.getValue());
        }

        RequestBody body1 = null;
        if (method.equals("GET")){
            body1 = null;
        }else {
            body1 = builder.build();
        }

        return new Request.Builder().method(method, body1)
                .url(BASE_URL + path)
                .build();
    }


    private void call(Request request, DataCallback callback){
        ThreadUtils.runMain(callback::start);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ThreadUtils.runMain(() -> {
                    callback.complete();
                    callback.setResultData(new ResultBean(-1, "请求失败", ""));
                    callback.onFailed();

                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d("HttpResult", "http结果:code=========== " + response.code() + "================");
                if (response.isSuccessful()){
                    ResponseBody body = response.body();
                    if (body == null){
                        ThreadUtils.runMain(() -> {
                            callback.complete();
                            callback.setResultData(new ResultBean(-1, "请求失败", ""));
                            callback.onFailed();
                        });
                    }else {
                        String string = body.string();
                        Log.d("HttpResult", "http结果:data===========\n " + string + "\n================");
                        ThreadUtils.runMain(() -> {
                            callback.complete();
                            callback.setResultData(new ResultBean(200, "请求成功", string));
                            callback.onSuccess();
                        });
                    }
                }else {
                    ResponseBody body = response.body();
                    String msg = "请求出错";
                    if (body != null){
                        msg = body.string();
                    }
                    Log.d("HttpResult", "http结果:code=========== " + msg + "================");
                    if (response.code() == 401){
                        msg = "请登录";
                    }else if (response.code() == 403){
                        msg = "您已被拉黑";
                    }else if (response.code() == 400){
                        msg = "参数错误";
                    }
                    String finalMsg = msg;
                    ThreadUtils.runMain(() -> {
                        callback.complete();
                        callback.setResultData(new ResultBean(response.code(), finalMsg, ""));
                        callback.onFailed();
                    });
                }
            }
        });
    }




    @MainThread
    public abstract static class DataCallback{

        private final LifecycleOwner owner;

        private ResultBean resultBean;

        protected DataCallback(LifecycleOwner owner){
            this.owner = owner;
        }

        private void setResultData(ResultBean resultData){
            this.resultBean = resultData;
        }

        protected void start(){}

        protected void complete(){}


        private void onFailed(){
            if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED){
                return;
            }
            if (resultBean != null){
                resultBean.toast();
            }
            onError(resultBean);
        }

        private void onSuccess(){
            if (owner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED){
                return;
            }

            onData(resultBean);
        }

        protected abstract void onError(ResultBean bean);

        protected abstract void onData(ResultBean bean);



    }


    public static class ResultBean{

        public static final int SUCCESS_CODE = 200;

        private int code;
        private String msg;
        private Object data;

        public ResultBean(int code, String msg, Object data){
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public boolean isSuccessful(){
            return  this.getCode() == SUCCESS_CODE;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public Object getData() {
            return data;
        }

        public <E> E getEntity(Class<E> e){
            return GsonUtils.toEntity(e, getData());
        }

        public <E> List<E> getList(Class<E> e){
            return GsonUtils.toList(e, getData());
        }

        public void toast(){
            toast(getMsg());
        }

        public void toast(String msg){
            Toast.makeText(SignInApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();
        }
    }

}
