package com.mattdrzazga.retrofitrxretry

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val single = Single.error<String>(
            HttpException(
                Response.error<String>(
                    501,
                    ResponseBody.create(MediaType.parse(""), "")
                )
            )
        )
            .doOnSubscribe {
                Log.v("MainActivity", "doOnSubscribe()")
            }

        single.wrapServerErrors {
            when (it.code()) {
                HttpURLConnection.HTTP_INTERNAL_ERROR -> ServerException(it)
                HttpURLConnection.HTTP_NOT_IMPLEMENTED -> RecoverableServerException(it)
                HttpURLConnection.HTTP_BAD_GATEWAY -> RecoverableServerException(it)
                else -> it
            }
        }
        .attemptRecoveryFromServerError()
        .subscribe({
            Log.v("MainActivity", "Success it.toString()")
        }, {
            Log.v("MainActivity", it.toString())
        })
    }
}