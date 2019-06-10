package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Country
import com.obf.movie.obfmoviefrontend.model.Manage
import com.obf.movie.obfmoviefrontend.util.Constants
import com.obf.movie.obfmoviefrontend.util.Constants.URL_Country
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class CountryService(private val restTemplate: RestTemplate,
                     private val utilService: UtilService) {
    private val log = LoggerFactory.getLogger(MovieService::class.java)
    fun getAllCountries(): List<Country> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Country>> = restTemplate.exchange(
                Constants.URL_Country + "?size=1000&page=0&sort=title,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Country>>())


        return result.body as List

    }

    fun saveNewCountry(manage: Manage) {
        if (!manage.text.isNullOrEmpty())
            utilService.postObjectNoReplyCreated(Country(null, manage.text!!), URL_Country)

    }
}