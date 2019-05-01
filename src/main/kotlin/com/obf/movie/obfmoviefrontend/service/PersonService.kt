package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.model.Person
import com.obf.movie.obfmoviefrontend.model.PersonAllInfo
import com.obf.movie.obfmoviefrontend.util.Constants
import com.obf.movie.obfmoviefrontend.util.Constants.URL_Person
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.client.RestTemplate
import java.lang.StringBuilder
import org.springframework.http.HttpEntity
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.thymeleaf.expression.Lists


@Service
class PersonService {

    fun getAllInfoOnePerson(restTemplate: RestTemplate, oid: Long): PersonAllInfo {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<PersonAllInfo> = restTemplate.exchange(
                "http://localhost:20200/api/person/complete-person-info/" + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<PersonAllInfo>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return result.body as PersonAllInfo
        } else {
            throw Exception("Could not find actor with oid : " + oid)
        }
    }

    fun searchName(restTemplate: RestTemplate, name: String): List<Person> {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val urlParameters = StringBuilder()


        val names = name.split(" ")

        if (names.size > 1){
            when {
                names.size == 2 -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&lastName=").append(names[1])
                }
                names.size == 3 -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&middleName=").append(names[1])
                    urlParameters.append("&lastName=").append(names[2])
                }
                else -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&middleName=")
                    for (i in 1..names.size-2) {
                        if (i > 1){
                            urlParameters.append(" ")
                        }
                        urlParameters.append(names[i])
                    }

                    urlParameters.append("&lastName=").append(names[names.size-1])
                }
            }
            urlParameters.append("&useOr=false")
        } else {
            urlParameters.append("?firstName=").append(name)
            urlParameters.append("&middleName=").append(name)
            urlParameters.append("&lastName=").append(name)
            urlParameters.append("&useOr=true")
        }

        val result: ResponseEntity<List<Person>> = restTemplate.exchange(
                Constants.URL_SearchPersonName + urlParameters.toString() + "&size=100&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Person>>())


        return result.body as List
    }

    fun saveNewPerson(restTemplate: RestTemplate, newPerson: NewPerson, model: Model): String {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val json = ObjectMapper().writeValueAsString(newPerson)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(URL_Person, entity, String::class.java)

        if (result?.statusCode === HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            val person : Person = ObjectMapper().readValue(jsonResult)
            val personAllInfo = PersonAllInfo(person, ArrayList(), ArrayList())
            model.addAttribute("personAllInfo", personAllInfo)

        } else {
            throw Exception("Could not save person")
        }
        return "person"
    }
}