package com.codepath.apps.restclienttemplate.models

import android.nfc.Tag
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
class Tweet(var body: String = "", var createdAt: String = "", var user: User? = null):
    Parcelable {

    companion object {
        private const val SECOND_IN_MILLIS = 1000
        private const val MINUTE_IN_MILLIS = 60 * SECOND_IN_MILLIS
        private const val HOUR_IN_MILLIS = 60 * MINUTE_IN_MILLIS
        private const val DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS

        fun getFormattedTimestamp(rawJsonDate: String): String {
            val twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy"
            val sf = SimpleDateFormat(twitterFormat, Locale.ENGLISH)
            sf.isLenient = true
            try {
                val time: Long = sf.parse(rawJsonDate).getTime()
                val now = System.currentTimeMillis()
                val diff = now - time
                return if (diff < MINUTE_IN_MILLIS) {
                    "just now"
                } else if (diff < 2 * MINUTE_IN_MILLIS) {
                    "a minute ago"
                } else if (diff < HOUR_IN_MILLIS) {
                    diff.div(MINUTE_IN_MILLIS).toString() + "m"
                } else if (diff < 1.5 * HOUR_IN_MILLIS) {
                    "an hour ago"
                } else if (diff < 24 * HOUR_IN_MILLIS) {
                    diff.div(HOUR_IN_MILLIS).toString() + "h"
                } else if (diff < 1.5 * DAY_IN_MILLIS) {
                    "yesterday"
                } else {
                    diff.div(DAY_IN_MILLIS).toString() + "d"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return ""
        }

        fun fromJson(jsonObject: JSONObject): Tweet {
            val tweet = Tweet()
            tweet.body = jsonObject.getString("text")
            tweet.createdAt = jsonObject.getString("created_at")
            tweet.createdAt = getFormattedTimestamp(tweet.createdAt)
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"))
            return tweet
        }

        fun fromJsonArray(jsonArray: JSONArray): List<Tweet> {
            val tweets = ArrayList<Tweet>()
            for (i in 0 until jsonArray.length()) {
                tweets.add(fromJson(jsonArray.getJSONObject(i)))
            }
            return tweets
        }
    }
}