package com.obf.movie.obfmoviefrontend.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class UtilService (private val restTemplate: RestTemplate){

    fun postObjectNoReplyCreated(request: Any, url: String) {
        val result = postObject(request, url)

        if (!(result.statusCode === HttpStatus.CREATED)) {
            throw Exception("Error executing post request fro create:" + result.statusCode)
        }
    }

    fun postObjectNoReplyOK(request: Any, url: String) {
        val result = postObject(request, url)

        if (!(result.statusCode === HttpStatus.OK)) {
            throw Exception("Error executing post request:" + result.statusCode)
        }
    }

    fun postObject(request: Any, url: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.set("", null)
        headers.contentType = MediaType.APPLICATION_JSON;


        val json = ObjectMapper().writeValueAsString(request)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(url, entity, String::class.java)
        return result
    }
}