package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Category
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CategoryService {
    fun getAllCategories(restTemplate: RestTemplate): List<Category> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Category>> = restTemplate.exchange(
                Constants.URL_SearchCategory + "?size=1000&page=0&sort=title,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Category>>())


        return result.body as List

    }
}