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
import javax.servlet.http.HttpSession


inline fun <reified T : Any> typeRef(): ParameterizedTypeReference<T> = object : ParameterizedTypeReference<T>() {}

@Service
class MovieService(private val personService: PersonService,
                   private val restTemplate: RestTemplate,
                   private val categoryService: CategoryService,
                   private val roleTypeService: RoleTypeService,
                   private val utilService: UtilService,
                   private val moviePersonService: MoviePersonService,
                   private val userService: UserService) {

    private val log = LoggerFactory.getLogger(MovieService::class.java)
    fun getAllMovies(restTemplate: RestTemplate): List<Movie> {

        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val result: ResponseEntity<List<Movie>> = restTemplate.exchange(
                Constants.URL_Movie + "?size=10&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Movie>>())


        return result.body as List

    }

    fun getOneMovie(oid: Long): Movie {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<Movie> = restTemplate.exchange(
                "${Constants.URL_Movie}/$oid",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<Movie>())

        if (result.statusCode === HttpStatus.OK && result.hasBody()) {
            return result.body as Movie
        } else {
            throw Exception("Could not find movie with oid : " + oid);
        }
    }

    fun getAllInfoOneMovie(oid: Long): MovieAllInfo {
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
                Constants.URL_LatestMovie,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<MovieAllInfo>())

        if (result.statusCode === HttpStatus.OK && result.hasBody()) {
            return convertLatestMovie(convertMovieAllInfo(result))
        } else {
            throw Exception("Some Thing went Wrong");
        }


    }

    fun searchOriginalTitle(originalTitle: String): List<Movie> {
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
                    categoryString = cat.title!!
                } else {
                    categoryString = categoryString + ", " + cat.title
                }

            }
            movieAllInfo.categoriesAsString = categoryString
        }
        return movieAllInfo
    }

    fun saveNewMovie(restTemplate: RestTemplate, newMovie: NewMovie, model: Model, httpSession: HttpSession): String {
        val headers = HttpHeaders()
        var movie: Movie
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val json = ObjectMapper().writeValueAsString(copyNewMovie(newMovie))
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(Constants.URL_Movie, entity, String::class.java)
        if (result.statusCode == HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            movie = ObjectMapper().readValue(jsonResult)
            saveCategoryOnMovie(newMovie.categoryOid!!, movie.oid!!)
        } else {
            throw Exception("Could not save movie")
        }
        return setUpMovie(model, movie.oid!!, httpSession, false)
    }

    private fun saveCategoryOnMovie(categoryOid: Long, movieOid: Long) {
        utilService.postObjectNoReplyOK(MovieCategory(movieOid, categoryOid), Constants.URL_MovieAddCategory)
    }

    fun copyNewMovie(newMovie: NewMovie): Movie {
        val json = ObjectMapper().writeValueAsString(newMovie)
        return ObjectMapper().readValue(json)

    }

    fun deleteMovie(restTemplate: RestTemplate, oid: Long) {
        restTemplate.delete("${Constants.URL_Movie}/$oid")
    }

    fun saveActorToMovie(restTemplate: RestTemplate, newActor: NewActor, model: Model, httpSession: HttpSession): String {
        val persons = personService.searchName(newActor.name!!)
        if (persons.isEmpty()) {
            return setUpNewActor(newActor, persons, model)
        } else if (persons.size == 1) {
            val person = persons[0]
            return addChosenActorToMovie(newActor.movieOid, person.oid!!, newActor.roleTypeOid!!, newActor.characterName!!, model, httpSession)
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


    fun setUpMovie(model: Model, oid: Long, httpSession: HttpSession, showAll: Boolean): String {
        var sessionObject = httpSession.getAttribute("movieAllInfoOriginal")

        if (sessionObject != null){
            val allInfoMovie = sessionObject as MovieAllInfo
            if (allInfoMovie.movie.oid == oid){
                return addAllInfoMovieToModel(model, allInfoMovie, showAll)
            }
        }
        return getMovieDBAndSetUp(oid, httpSession, model, showAll)
    }

    private fun getMovieDBAndSetUp(oid: Long, httpSession: HttpSession, model: Model, showAll: Boolean): String {
        val allInfoMovie = getAllInfoOneMovie(oid)
        httpSession.setAttribute("movieAllInfoOriginal", allInfoMovie)
        return addAllInfoMovieToModel(model, allInfoMovie, showAll )
    }


    fun setUpMovie(model: Model, oid: Long): String {
        addAllInfoMovieToModel(model, getAllInfoOneMovie(oid), false)
        return "movie"
    }

    fun addAllInfoMovieToModel(model: Model, movieAllInfo: MovieAllInfo, showAll: Boolean): String{
        var copyObject = movieAllInfo.copy()
        if (!showAll && copyObject.actorsToMovie!!.size > 15){
            var copyList = copyObject.actorsToMovie!!.subList(0,15)
            copyObject.actorsToMovie = copyList
        }
        model.addAttribute("movieAllInfo", copyObject)
        model.addAttribute("newActor", NewActor())
        model.addAttribute("showAll",showAll)
        return "movie"
    }


    fun addChosenActorToMovie(movieOid: Long, personOid: Long, roleTypeOid: Long, charachter: String, model: Model, httpSession: HttpSession): String {
        moviePersonService.addChosenActorToMovieDB(movieOid, personOid, charachter, roleTypeOid)
        httpSession.removeAttribute("movieAllInfoOriginal")
        return setUpMovie(model, movieOid, httpSession, true)
    }

    fun addActorToMovieAsNew(movieOid: Long, name: String, roleTypeOid: Long, charachter: String, model: Model, httpSession: HttpSession): String {
        val person = personService.saveNewPerson(name)
        return addChosenActorToMovie(movieOid, person.oid!!, roleTypeOid, charachter, model, httpSession)
    }

    fun addChosenDirectorToMovie(movieOid: Long, personOid: Long, model: Model, httpSession: HttpSession): String {
        addDirectorToMovie(restTemplate, movieOid, personOid)
        httpSession.removeAttribute("movieAllInfoOriginal")
        return setUpMovie(model, movieOid, httpSession, false)
    }

    fun addDirectorToMovie(restTemplate: RestTemplate, movieOid: Long, personOid: Long) {
        val movieDirector = MovieDirector(null, movieOid, personOid)
        utilService.postObjectNoReplyCreated(movieDirector, Constants.URL_MovieAddDirector)
    }

    fun addDirectorToMovieAsNew(movieOid: Long, name: String, model: Model, httpSession: HttpSession): String {
        val person = personService.saveNewPerson(name)
        return addChosenDirectorToMovie(movieOid, person.oid!!, model, httpSession)
    }

    fun saveDirectorToMovie(restTemplate: RestTemplate, newDirector: NewDirector, model: Model, httpSession: HttpSession): String {

        val persons = personService.searchName(newDirector.name!!)
        return when {
            persons.isEmpty() -> setUpNewDirector(newDirector, persons, model)
            persons.size == 1 -> {
                val person = persons[0]
                addChosenDirectorToMovie(newDirector.movieOid, person.oid!!, model, httpSession)
            }
            else -> setUpNewDirector(newDirector, persons, model)
        }
    }

    fun removeDirector(model: Model, movieDirectorOid: Long, movieOid: Long, httpSession: HttpSession): String {
        val directorToMovie = DirectorToMovie(null, null, null, 0, movieDirectorOid)

        utilService.postObjectNoReplyOK(directorToMovie, Constants.URL_MovieRemoveDirector)
        removeDirectorFromSessionObject(httpSession, movieDirectorOid)
        return setUpMovie(model, movieOid, httpSession, false)
    }


    fun removeActor(model: Model, movieActorOid: Long, movieOid: Long, httpSession: HttpSession): String {
        val actorToMovie = ActorToMovie(null, null, null, 0, null, null, null, null, movieActorOid)
        utilService.postObjectNoReplyOK(actorToMovie, Constants.URL_MovieRemoveActor)
        removeActorFromSessionObject(httpSession, movieActorOid)

        return setUpMovie(model, movieOid, httpSession, true)
    }

    private fun removeActorFromSessionObject(httpSession: HttpSession, movieActorOid: Long) {
        var movieAllInfo = httpSession.getAttribute("movieAllInfoOriginal") as MovieAllInfo
        movieAllInfo.actorsToMovie = movieAllInfo.actorsToMovie!!.filter { it.movieActorOid != movieActorOid }
        httpSession.setAttribute("movieAllInfoOriginal", movieAllInfo)
    }

    private fun removeDirectorFromSessionObject(httpSession: HttpSession, movieDirectorOid: Long) {
        var movieAllInfo = httpSession.getAttribute("movieAllInfoOriginal") as MovieAllInfo
        movieAllInfo.directorsToMovie = movieAllInfo.directorsToMovie!!.filter { it.movieDirectorOid != movieDirectorOid }
        httpSession.setAttribute("movieAllInfoOriginal", movieAllInfo)
    }

    fun addCategory(movieOid: Long, model: Model): String {
        val newCategory = NewCategory()
        newCategory.categories = categoryService.getAllCategories()
        newCategory.movieOid = movieOid
        model.addAttribute("newCategory", newCategory)
        return "addCategory"
    }

    fun saveNewCategory(newCategory: NewCategory, model: Model): String {
        saveCategoryOnMovie(newCategory.categoryOid!!, newCategory.movieOid)
        return setUpMovie(model, newCategory.movieOid)
    }

    fun setUpMovieHome(model: Model, message: String): String {
        val searchTypes = listOf("movie", "actor", "all")
        model.addAttribute("movieAllInfoLatest", getLatestMovie(restTemplate))
        model.addAttribute("search", Search(null, null, searchTypes))
        model.addAttribute("message", message)
        return "movieHome"
    }

    fun setUpAddMovie(model: Model): String {
        model.addAttribute("newMovie", NewMovie())
        model.addAttribute("categories", categoryService.getAllCategories())
        return "addMovie"
    }

    fun setUpAddDirectorToMovie(oid: Long, model: Model): String {
        val newDirector = NewDirector()
        newDirector.movieOid = oid
        return newDirector(model, newDirector)
    }

    private fun setUpNewDirector(newDirector: NewDirector, persons: List<Person>, model: Model): String {
        newDirector.persons = persons
        return newDirector(model, newDirector)
    }

    private fun newDirector(model: Model, newDirector: NewDirector): String {
        model.addAttribute("newDirector", newDirector)
        return "newDirector"
    }

    fun setUpAddActorToMovie(oid: Long, model: Model): String {
        val newActor = NewActor()
        newActor.movieOid = oid
        model.addAttribute("newActor", newActor)
        model.addAttribute("roleTypes", roleTypeService.getAllRoleType())
        return "newActor"
    }

    fun editMovie(model: Model, oid: Long): String {
        model.addAttribute("newMovie",copyMovieToNewMovie(getOneMovie(oid)))
        return "editMovie"
    }


    fun copyMovieToNewMovie(movie: Movie): NewMovie{
        val json = ObjectMapper().writeValueAsString(movie)
        return ObjectMapper().readValue<NewMovie>(json)

    }


    fun copyNewMovieToMovie(newMovie: NewMovie): Movie{
        val json = ObjectMapper().writeValueAsString(newMovie)
        return ObjectMapper().readValue<Movie>(json)

    }

    fun updateMovie(newMovie: NewMovie, model: Model, httpSession: HttpSession): String {
        updateMovieDb(newMovie)
        return setUpMovie(model, newMovie.oid!!, httpSession, false)
    }

    fun updateMovieDb(newMovie: NewMovie): Movie {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val movie = copyNewMovieToMovie(newMovie)
        val json = ObjectMapper().writeValueAsString(movie)
        val entity = HttpEntity(json, headers)
        restTemplate.put(Constants.URL_Movie, entity, String::class.java)
        return movie
    }
}