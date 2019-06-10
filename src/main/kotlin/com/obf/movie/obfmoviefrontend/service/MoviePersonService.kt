package com.obf.movie.obfmoviefrontend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.obf.movie.obfmoviefrontend.model.MoviePerson
import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.model.Role
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.client.RestTemplate

@Service
class MoviePersonService(private val restTemplate: RestTemplate,
                         private val roleService: RoleService) {

    fun addChosenActorToMovieDB(movieOid: Long, personOid: Long, charachter: String, roleTypeOid: Long) {
        val moviePerson = addActorToMovie( movieOid, personOid)
        val role = Role(null, charachter, moviePerson.oid!!, roleTypeOid)

        roleService.addRole(restTemplate, role)
    }

    private fun addActorToMovie( movieOid: Long, personOid: Long): MoviePerson {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;
        val moviePerson = MoviePerson(null, movieOid, personOid)

        val json = ObjectMapper().writeValueAsString(moviePerson)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(Constants.URL_MovieAddActor, entity, String::class.java)

        if (result.statusCode === HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            return ObjectMapper().readValue(jsonResult)
        } else {
            throw Exception("Could not save person")
        }
    }
}