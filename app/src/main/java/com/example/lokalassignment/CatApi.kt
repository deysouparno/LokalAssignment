package com.example.lokalassignment

import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.Headers
import retrofit2.http.Query

interface CatApi {

    companion object {
        const val API_KEY = "live_rIAeVnuBiUkJIKXAqpwk6iRYok39WZm08kUMgvzfMJVrlDUFyyfl4xhGITKBC94j"
        const val BASE_URL = "https://api.thecatapi.com/v1/images/"
    }

    @GET("search")
    @Headers(
        "x-api-key:$API_KEY",
    )
    suspend fun getCats(
        @Query("has_breeds") query: Boolean = true,
        @Query("page") page: Int,
        @Query("limit") includeAdult: Int = 20,
        ): List<Cat>?

}