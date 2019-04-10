package com.pubnub.crc.examples.util;

import android.annotation.SuppressLint;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Helper {

    private Helper() {
    }

    public static <T> T getRandomElement(T[] array) {
        return array[(int) (Math.random() * array.length + 1)];
    }

    public static <T> T getRandomElement(List<T> array) {
        return array.get((int) (Math.random() * array.size()));
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public static String parseDateTime(long timetoken) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetoken);
        return sdf.format(calendar.getTime());
    }

    public static String parseDateTimeIso8601(long timetoken) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timetoken);
        return sdf.format(calendar.getTime());
    }
}
