package com.idz.travelconnect.data.repository

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

private interface RestCountriesApi {
    @GET("v3.1/name/{name}")
    suspend fun getCountry(
        @Path("name") name: String,
        @Query("fields") fields: String = "name,flags,region,languages,currencies"
    ): List<CountryResponse>
}

private data class CountryResponse(
    val name: NameDto,
    val flags: FlagsDto,
    val region: String,
    val languages: Map<String, String>,
    val currencies: Map<String, CurrencyDto>
)

private data class NameDto(@SerializedName("common") val common: String)
private data class FlagsDto(@SerializedName("png") val png: String)
private data class CurrencyDto(val name: String, val symbol: String?)

object RestCountriesRepository {

    data class CountryInfo(
        val name: String,
        val flagUrl: String,
        val region: String,
        val languages: String,
        val currencies: String
    )

    private val api = Retrofit.Builder()
        .baseUrl("https://restcountries.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RestCountriesApi::class.java)

    suspend fun fetchCountry(name: String): CountryInfo {
        val country = api.getCountry(name.trim()).first()
        return CountryInfo(
            name = country.name.common,
            flagUrl = country.flags.png,
            region = country.region,
            languages = country.languages.values.joinToString(", "),
            currencies = country.currencies.entries.joinToString(", ") { (_, v) ->
                val symbol = v.symbol?.let { " ($it)" } ?: ""
                "${v.name}$symbol"
            }
        )
    }
}
