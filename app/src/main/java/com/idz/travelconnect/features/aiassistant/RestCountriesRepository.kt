package com.idz.travelconnect.features.aiassistant

import org.json.JSONArray
import java.net.URL

object RestCountriesRepository {

    data class CountryInfo(
        val name: String,
        val flagUrl: String,
        val region: String,
        val languages: String,
        val currencies: String
    )

    fun fetchCountry(name: String): CountryInfo {
        val url = "https://restcountries.com/v3.1/name/${name.trim()}?fields=name,flags,region,languages,currencies"
        val json = URL(url).readText()
        val obj = JSONArray(json).getJSONObject(0)

        val commonName = obj.getJSONObject("name").getString("common")
        val flagUrl = obj.getJSONObject("flags").getString("png")
        val region = obj.getString("region")

        val languagesObj = obj.getJSONObject("languages")
        val languages = languagesObj.keys().asSequence()
            .map { languagesObj.getString(it) }
            .joinToString(", ")

        val currenciesObj = obj.getJSONObject("currencies")
        val currencies = currenciesObj.keys().asSequence().map { key ->
            val curr = currenciesObj.getJSONObject(key)
            val currName = curr.getString("name")
            val symbol = if (curr.has("symbol")) " (${curr.getString("symbol")})" else ""
            "$currName$symbol"
        }.joinToString(", ")

        return CountryInfo(
            name = commonName,
            flagUrl = flagUrl,
            region = region,
            languages = languages,
            currencies = currencies
        )
    }
}
