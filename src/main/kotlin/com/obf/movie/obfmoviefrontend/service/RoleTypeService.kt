package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.RoleType
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class RoleTypeService {
    fun getAllRoleType(restTemplate: RestTemplate): List<RoleType> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<RoleType>> = restTemplate.exchange(
                Constants.URL_ListRoleType + "?size=10&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<RoleType>>())


        return result.body as List

    }
}