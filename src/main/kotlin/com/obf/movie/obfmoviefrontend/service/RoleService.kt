package com.obf.movie.obfmoviefrontend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.obf.movie.obfmoviefrontend.model.Role
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RoleService {

    fun addRole(restTemplate: RestTemplate, role: Role): Role {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val json = ObjectMapper().writeValueAsString(role)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(Constants.URL_Role, entity, String::class.java)
        if (result?.statusCode == HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            return ObjectMapper().readValue(jsonResult)
        } else {
            throw Exception("Could not save person")
        }
    }
}