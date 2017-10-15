package com.yeyaxi.android.playground.model;

import com.google.gson.annotations.SerializedName;

public class Company {
    @SerializedName("name")
    private String name;

    @SerializedName("catchPhrase")
    private String catchPhrase;

    @SerializedName("bs")
    private String bs;
}
