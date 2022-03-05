package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)

        client = TwitterApplication.getRestClient(this)

        // Handling the user's click on the tweet button
        btnTweet.setOnClickListener {

            // Grab the content of the edittext (etCompose)
            val tweetContent = etCompose.text.toString()

            // 1. Make sure the tweet isn't empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            } else
                // 2. Make sure the tweet is under character count
                if (tweetContent.length > 280) {
                    Toast.makeText(this, "Tweet is too long! Limit is 280 characters", Toast.LENGTH_SHORT).show()
                } else {
                   // TODO: Make an api call to Twitter to publish the tweet
                    Toast.makeText(this, tweetContent, Toast.LENGTH_SHORT).show()
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON) {
                            Log.i(TAG, "Successfully published tweet!")
                            // Send the tweet back to TimelineActivity
                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet", throwable)
                        }


                    })
                }
        }

        val getChar = findViewById<EditText>(R.id.etTweetCompose)
        val etValue = findViewById<TextView>(R.id.charCount)
        getChar.addTextChangedListener(object : TextWatcher {

            var limit = 280
            override fun onTextChanged(s: CharSequence, start: Int, counter: Int, count: Int) {
                limit += counter
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                limit -= after
            }

            override fun afterTextChanged(s: Editable) {
                if(limit < 1) {
                    etValue.text = "Character limit reached"
                } else {
                    val total = 280 - limit
                    etValue.text = total.toString() + "/280"
                }
            }
        })
    }
    companion object {
        val TAG = "ComposeActivity"
    }
}