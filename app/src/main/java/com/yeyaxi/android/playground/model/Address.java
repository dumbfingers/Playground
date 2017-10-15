package com.yeyaxi.android.playground.model;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("street")
    private String street;

    @SerializedName("suite")
    private String suite;

    @SerializedName("city")
    private String city;

    @SerializedName("zipcode")
    private String zipcode;

    @SerializedName("geo")
    private Geo geo;
}
