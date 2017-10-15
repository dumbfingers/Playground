package com.yeyaxi.android.playground.model;

import com.google.gson.annotations.SerializedName;

public class Comment {

    @SerializedName("postId")
    private Long postId;

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("body")
    private String body;
}
