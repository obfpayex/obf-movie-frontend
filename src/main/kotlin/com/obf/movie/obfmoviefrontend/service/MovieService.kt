package com.obf.movie.obfmoviefrontend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.obf.movie.obfmoviefrontend.model.*
import com.obf.movie.obfmoviefrontend.util.Constants
import org.slf4j.LoggerFactory
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.client.RestTemplate


inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class MovieService {

    private val log = LoggerFactory.getLogger(MovieService::class.java)
    fun getAllMovies(restTemplate: RestTemplate): List<Movie> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Movie>> = restTemplate.exchange(
                Constants.URL_ListMOvie + "?size=10&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Movie>>())


        return result.body as List

    }

    fun getOneMovie(restTemplate: RestTemplate, oid: Long): Movie {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<Movie> = restTemplate.exchange(
                Constants.URL_OneMOvie + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<Movie>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return result.body as Movie
        } else {
            throw Exception("Could not find movie with oid : " + oid);
        }
    }

    fun getAllInfoOneMovie(restTemplate: RestTemplate, oid: Long): MovieAllInfo {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<MovieAllInfo> = restTemplate.exchange(
                Constants.URL_AllInfoOneMOvie + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<MovieAllInfo>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return convertMovieAllInfo(result)
        } else {
            throw Exception("Could not find movie with oid : " + oid)
        }


    }

    fun getLatestMovie(restTemplate: RestTemplate): MovieAllInfo {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<MovieAllInfo> = restTemplate.exchange(
                Constants.URL_LatestMOvie,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<MovieAllInfo>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return convertLatestMovie(convertMovieAllInfo(result))
        } else {
            throw Exception("Some Thing went Wrong");
        }


    }

    fun searchOriginalTitle(restTemplate: RestTemplate, originalTitle: String): List<Movie> {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Movie>> = restTemplate.exchange(
                Constants.URL_SearchOriginalTitle + "?originalTitle=" + originalTitle + "&size=10&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Movie>>())


        return result.body as List
    }


    private fun convertLatestMovie(result: MovieAllInfo): MovieAllInfo {


        if (result.actorsToMovie.orEmpty().size > 3) {
            result.actorsToMovie = result.actorsToMovie.orEmpty().subList(0, 3)
        }

        if (result.directorsToMovie.orEmpty().size > 3) {
            result.directorsToMovie = result.directorsToMovie.orEmpty().subList(0, 3)
        }


        return result;
    }


    private fun convertMovieAllInfo(result: ResponseEntity<MovieAllInfo>): MovieAllInfo {
        var movieAllInfo: MovieAllInfo = result.body as MovieAllInfo
        var list: List<Category> = movieAllInfo.categories.orEmpty()
        if (list.isNotEmpty()) {
            var categoryString = ""
            for (cat: Category in list) {
                if (categoryString.length == 0) {
                    categoryString = cat.title
                } else {
                    categoryString = categoryString + ", " + cat.title
                }

            }
            movieAllInfo.categoriesAsString = categoryString
        }
        return movieAllInfo
    }

    fun saveNewMovie(restTemplate: RestTemplate, newMovie: NewMovie, model: Model): String {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val json = ObjectMapper().writeValueAsString(copyNewMovie(newMovie))
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(Constants.URL_Movie, entity, String::class.java)
        if (result?.statusCode == HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            val movie: Movie = ObjectMapper().readValue(jsonResult)

            saveCategoryOnMovie(newMovie, movie, headers, restTemplate, result)

            model.addAttribute("movieAllInfo", getAllInfoOneMovie(restTemplate, movie.oid!!))
        } else {
            throw Exception("Could not save person")
        }
        return "movie"
    }

    private fun saveCategoryOnMovie(newMovie: NewMovie, movie: Movie, headers: HttpHeaders, restTemplate: RestTemplate, result: ResponseEntity<String>) {
        if (newMovie.categoryOid != null) {
            val movieCategory = MovieCategory(movie.oid!!, newMovie.categoryOid!!)
            val jsonCat = ObjectMapper().writeValueAsString(movieCategory)
            val entityCat = HttpEntity(jsonCat, headers)
            val resultCat = restTemplate.postForEntity(Constants.URL_MovieAddCategory, entityCat, String::class.java)
            if (result?.statusCode != HttpStatus.CREATED) {
                throw java.lang.Exception("Category " + newMovie.categoryOid!! + " was not saved om movie " + movie.oid!!)
            }
        }
    }

    fun copyNewMovie(newMovie: NewMovie): Movie {
        val json = ObjectMapper().writeValueAsString(newMovie);
        return ObjectMapper().readValue<Movie>(json);

    }

    fun deleteMovie(restTemplate: RestTemplate, oid: Long) {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        restTemplate.delete(Constants.URL_MovieDelete + oid)
    }
}