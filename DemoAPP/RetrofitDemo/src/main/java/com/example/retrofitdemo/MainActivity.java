package com.example.retrofitdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private final String BASE_URL = "https://api.github.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        retrofit();
    }

    private void retrofit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                LoganApi api = retrofit.create(LoganApi.class);
                Call<GitLoginData> call = api.reposForUser("simonws");
                // 用法和OkHttp的call如出一辙,
                // 不同的是如果是Android系统回调方法执行在主线程
                call.enqueue(new Callback<GitLoginData>() {
                    @Override
                    public void onResponse(Call<GitLoginData> call, Response<GitLoginData> data) {
                        try {
                            Log.e("logan", "onResponse: "+data.body().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<GitLoginData> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        }).start();
    }
}
