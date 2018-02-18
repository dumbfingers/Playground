package com.yeyaxi.android.playground.util;

import android.support.annotation.NonNull;

import com.yeyaxi.android.playground.constant.Params;

public class AvatarUriUtil {

    @NonNull
    public static String getAvatarUri(String email) {
        String hash = MD5Util.md5Hex(email.toLowerCase());
        return Params.IMAGE_BASE_PATH + hash + "?s=400&d=robohash";
    }
}
