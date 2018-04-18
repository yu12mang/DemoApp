package com.example.retrofitdemo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface LoganApi {

        @GET("/users/{user}")
        Call<GitLoginData> reposForUser(@Path("user") String user);
}
