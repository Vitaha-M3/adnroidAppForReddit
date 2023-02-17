package com.example.appforreddit.utils;

import androidx.annotation.NonNull;

public class WhenPostedUtil {

    @NonNull
    public static String adaptWhenPosted(Long created){
        StringBuilder postedBy = new StringBuilder("Posted ");
        long timeAgo = System.currentTimeMillis()/1000-created;
        int day = 86400;
        int hour = 3600;
        int daysOut = (int) Math.floor(timeAgo/day);
        int hoursOut = (int) Math.floor((timeAgo - daysOut * day)/hour);
        int minutesOut = (int) Math.floor((timeAgo - daysOut * day - hoursOut * hour)/60);
        postedBy.append(daysOut>0 ? daysOut + " day " : hoursOut > 0 ? hoursOut + " h " : minutesOut > 0 ? minutesOut + " m " : " < 1 minute ");
        if(daysOut > 0 && hoursOut > 0){
            postedBy.append(hoursOut).append(" h ");
        }
        postedBy.append("ago");
        return postedBy.toString();
    }
}
