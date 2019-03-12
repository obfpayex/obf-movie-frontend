package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Movie
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class MovieService{

    fun getAllMovies(restTemplate: RestTemplate){

        val headers : HttpHeaders = HttpHeaders();
        val entity : HttpEntity = HttpEntity();
        restTemplate.exchange("",HttpMethod.GET,entity, String::class);

    }
}