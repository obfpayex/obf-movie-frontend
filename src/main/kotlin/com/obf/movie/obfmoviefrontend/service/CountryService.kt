package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Country
import com.obf.movie.obfmoviefrontend.util.Constants
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class CountryService (private val restTemplate: RestTemplate){
    private val log = LoggerFactory.getLogger(MovieService::class.java)
    fun getAllCountries(): List<Country> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Country>> = restTemplate.exchange(
                Constants.URL_SearchCountry + "?size=1000&page=0&sort=title,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Country>>())


        return result.body as List

    }
}