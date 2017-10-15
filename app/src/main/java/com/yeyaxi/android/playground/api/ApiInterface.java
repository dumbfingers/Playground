package com.yeyaxi.android.playground.api;


import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    String BASE_URL = "http://jsonplaceholder.typicode.com/";

    @GET("posts")
    Observable<List<Post>> getPosts();

    @GET("posts/{id}")
    Observable<Post> getPost(@Path("id") Long postId);

    @GET("users")
    Observable<List<User>> getUsers();

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") Long id);

    @GET("comments/")
    Observable<List<Comment>> getComments(@Query("postId") Long postId);

}
