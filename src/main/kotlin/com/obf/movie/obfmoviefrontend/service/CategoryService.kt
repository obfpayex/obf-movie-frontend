package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Category
import com.obf.movie.obfmoviefrontend.model.Manage
import com.obf.movie.obfmoviefrontend.util.Constants
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CategoryService(private val restTemplate: RestTemplate,
                      private val utilService: UtilService) {

    fun getAllCategories(): List<Category> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Category>> = restTemplate.exchange(
                Constants.URL_Category + "?size=1000&page=0&sort=title,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Category>>())


        return result.body as List

    }

    fun saveNewCategory(manage: Manage) {
        utilService.postObjectNoReplyCreated(Category(null, manage.text), Constants.URL_Category)
    }

}