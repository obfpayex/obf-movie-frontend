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
class MovieService(private val personService: PersonService,
                   private val roleService: RoleService,
                   private val restTemplate: RestTemplate) {

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

        if (result.statusCode === HttpStatus.OK && result.hasBody()) {
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

        if (result.statusCode === HttpStatus.OK && result.hasBody()) {
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

        if (result.statusCode === HttpStatus.OK && result.hasBody()) {
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
        if (result.statusCode == HttpStatus.CREATED && result.hasBody()) {
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
            if (result.statusCode != HttpStatus.CREATED) {
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

    fun saveActorToMovie(restTemplate: RestTemplate, newActor: NewActor, model: Model): String {
        val persons = personService.searchName(restTemplate,newActor.name!!)
        if (persons.isEmpty()){
            return setUpNewActor(newActor, persons, model)
        } else if (persons.size == 1){
            val person = persons[0]
            return addChosenActorToMovie(newActor.movieOid, person.oid!!, newActor.roleTypeOid!!, newActor.characterName!!, model)
        } else {
            return setUpNewActor(newActor, persons, model)
        }
    }

    private fun setUpNewActor(newActor: NewActor, persons: List<Person>, model: Model): String {
        newActor.persons = persons
        val roleTypes: List<RoleType>
        roleTypes = arrayListOf()
        model.addAttribute("roleTypes", roleTypes)
        model.addAttribute("newActor", newActor)
        return "newActor"
    }


    private fun setUpNewDirector(newDirector: NewDirector, persons: List<Person>, model: Model): String {
        newDirector.persons = persons
        model.addAttribute("newDirector", newDirector)
        return "newDirector"
    }

    fun addActorToMovie(restTemplate: RestTemplate, movieOid: Long, personOid: Long): MoviePerson {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;
        val moviePerson = MoviePerson(null,movieOid,personOid)

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

    fun setUpMovie( model: Model, oid: Long): String {
        model.addAttribute("movieAllInfo", getAllInfoOneMovie(restTemplate, oid))
        model.addAttribute("newActor", NewActor())
        return "movie"
    }

    fun addChosenActorToMovie(movieOid: Long, personOid: Long, roleTypeOid: Long, charachter: String, model: Model): String {
        val moviePerson = addActorToMovie(restTemplate,movieOid,personOid)
        val role = Role(null,charachter, moviePerson.oid!!, roleTypeOid)

        roleService.addRole(restTemplate,role)
        return setUpMovie(model,movieOid)
    }

    fun addActorToMovieAsNew(movieOid: Long, name: String, roleTypeOid: Long, charachter: String, model: Model): String {
        val person = personService.saveNewPerson(name)
        return addChosenActorToMovie(movieOid, person.oid!!, roleTypeOid, charachter, model)
    }

    fun addChosenDirectorToMovie(movieOid: Long, personOid: Long, model: Model): String {
        addDirectorToMovie(restTemplate,movieOid,personOid)
        return setUpMovie(model,movieOid)
    }

    fun addDirectorToMovie(restTemplate: RestTemplate, movieOid: Long, personOid: Long){
        val movieDirector = MovieDirector(null,movieOid,personOid)
        postObjectNoReplyCreated(movieDirector, Constants.URL_MovieAddDirector)
    }

    fun addDirectorToMovieAsNew(movieOid: Long, name: String, model: Model): String {
        val person = personService.saveNewPerson(name)
        return addChosenDirectorToMovie(movieOid, person.oid!!, model)
    }

    fun saveDirectorToMovie(restTemplate: RestTemplate, newDirector: NewDirector, model: Model): String {
        val persons = personService.searchName(restTemplate,newDirector.name!!)
        if (persons.isEmpty()){
            return setUpNewDirector(newDirector, persons, model)
        } else if (persons.size == 1){
            val person = persons[0]
            return addChosenDirectorToMovie(newDirector.movieOid, person.oid!! , model)
        } else {
            return setUpNewDirector(newDirector, persons, model)
        }
    }

    fun removeDirector(model: Model, movieDirectorOid: Long, movieOid: Long): String {
        val directorToMovie = DirectorToMovie(null, null, null, 0, movieDirectorOid)

        postObjectNoReplyOK(directorToMovie, Constants.URL_MovieRemoveDirector)

        return setUpMovie(model, movieOid)
    }




    fun removeActor(model: Model, movieActorOid: Long, movieOid: Long): String {
        val actorToMovie = ActorToMovie(null,null,null,0, null, null,null, null, movieActorOid)
        postObjectNoReplyOK(actorToMovie, Constants.URL_MovieRemoveActor)

        return setUpMovie(model, movieOid)
    }


    private fun postObjectNoReplyCreated(request: Any, url: String ) {
        val result = postObject(request, url)

        if (!(result.statusCode === HttpStatus.CREATED)) {
            throw Exception("Error executing post request fro create:" +result.statusCode)
        }
    }

    private fun postObjectNoReplyOK(request: Any, url: String ) {
        val result = postObject(request, url)

        if (!(result.statusCode === HttpStatus.OK)) {
            throw Exception("Error executing post request:" +result.statusCode)
        }
    }

    private fun postObject(request: Any, url: String): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;


        val json = ObjectMapper().writeValueAsString(request)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(url, entity, String::class.java)
        return result
    }

}