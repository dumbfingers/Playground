package com.yeyaxi.android.playground;


import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.model.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface EndPoint {
    String BASE_URL = "http://jsonplaceholder.typicode.com/";

    @GET("posts")
    Observable<List<Post>> getPosts();

    @GET("users")
    Observable<List<User>> getUsers();

    @GET("comments")
    Observable<List<Comment>> getComments();

}
