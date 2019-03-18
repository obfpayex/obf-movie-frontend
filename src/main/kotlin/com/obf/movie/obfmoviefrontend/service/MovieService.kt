package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Category
import com.obf.movie.obfmoviefrontend.model.Movie
import com.obf.movie.obfmoviefrontend.model.MovieAllInfo
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*


inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class MovieService{

    private val log = LoggerFactory.getLogger(MovieService::class.java)
    fun getAllMovies(restTemplate: RestTemplate):List<Movie>{

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Movie>> = restTemplate.exchange(
                "http://localhost:20200/api/movie?size=10&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Movie>>())


        return result.body as List

    }

    fun getOneMovie(restTemplate: RestTemplate, oid: Long ): Movie{
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<Movie> = restTemplate.exchange(
                "http://localhost:20200/api/movie/" + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<Movie>())

        if(result?.statusCode === HttpStatus.OK && result.hasBody()){
            return result.body as Movie
        } else {
            throw Exception("Could not find movie with oid : " +oid);
        }


    }

    fun getAllInfoOneMovie(restTemplate: RestTemplate, oid: Long ): MovieAllInfo{
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<MovieAllInfo> = restTemplate.exchange(
                "http://localhost:20200/api/movie/complete-movie-info/" + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<MovieAllInfo>())

        if(result?.statusCode === HttpStatus.OK && result.hasBody()){
            var movieAllInfo : MovieAllInfo = result.body as MovieAllInfo
            var list : List<Category>  = movieAllInfo.categories.orEmpty()
            if (list.isNotEmpty() ){
                var categoryString = ""
                for (cat : Category in list){
                    if (categoryString.length == 0){
                        categoryString = cat.title
                    } else {
                        categoryString = categoryString + ", " + cat.title
                    }

                }
                movieAllInfo.categoriesAsString = categoryString
            }
            return movieAllInfo
        } else {
            throw Exception("Could not find movie with oid : " +oid)
        }


    }
}