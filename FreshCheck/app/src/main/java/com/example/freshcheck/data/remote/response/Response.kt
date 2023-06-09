package com.example.freshcheck.data.remote.response

import com.google.gson.annotations.SerializedName

data class Response(
    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("prediction")
    val prediction: Int,
)
