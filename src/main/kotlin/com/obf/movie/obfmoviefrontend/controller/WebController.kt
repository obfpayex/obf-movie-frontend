package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.NewMovie
import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.model.Person
import com.obf.movie.obfmoviefrontend.model.Search
import com.obf.movie.obfmoviefrontend.service.CategoryService
import com.obf.movie.obfmoviefrontend.service.CountryService
import com.obf.movie.obfmoviefrontend.service.PersonService
import com.obf.movie.obfmoviefrontend.service.MovieService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.bind.annotation.RequestMapping




@Controller
class WebController(private val restTemplate: RestTemplate,
                    private val movieService: MovieService,
                    private val personService: PersonService,
                    private val countryService: CountryService,
                    private val categoryService: CategoryService) {

    private val log = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping("/")
    fun home(): String {
        return "redirect:/index"
    }

    @GetMapping("/index")
    fun customerForm(model: Model): String {
        return "index"
    }


    @PostMapping("/movieHome")
    fun movieHome(model: Model): String {
        return setUpMovieHome(model, "")
    }

    @GetMapping("/listMovies")
    fun listMovies(model: Model): String {
        val movies = movieService.getAllMovies(restTemplate)
        model.addAttribute("movieList", movies)
        return "movies"
    }

    @RequestMapping("/getMovie/{oid}")
    fun getMovie(@PathVariable oid: Long, model: Model): String {
        return setUpMovie(model, oid)
    }

    @RequestMapping("/getPerson/{oid}")
    fun getPerson(@PathVariable oid: Long, model: Model): String {
        return setUpOnePerson(model, oid)
    }

    @GetMapping("/add-movie")
    fun addMovie(model: Model): String {
        model.addAttribute("newMovie", NewMovie())
        model.addAttribute("categories",categoryService.getAllCategories(restTemplate))
        return "addMovies"
    }

    @GetMapping("/add-person")
    fun addPerson(model: Model): String {
        model.addAttribute("newPerson", NewPerson())
        model.addAttribute("countries",countryService.getAllCountries(restTemplate))
        return "addperson"
    }

    @PostMapping("/search")
    fun search(@ModelAttribute("search") search: Search, model: Model): String {
        return handelSearch(search, model)
    }

    @PostMapping("/save-new-person")
    fun addNewPerson(@ModelAttribute("newPerson") newPerson: NewPerson, model: Model): String {
        return personService.saveNewPerson(restTemplate, newPerson, model)
    }


    private fun handelSearch(search: Search, model: Model): String {
        if (search.searchCriteria.isNullOrEmpty()) {
            return setUpMovieHome(model, "")
        }
        if ("movie".equals(search.searchType.orEmpty(), true)) {
            return searchMovieOriginalTitle(search, model)
        } else if ("actor".equals(search.searchType.orEmpty(), true)) {
            return searchPersonName(search, model)
        } else if ("all".equals(search.searchType.orEmpty(), true)) {
            return setUpMovieHome(model, "")
        } else {
            return setUpMovieHome(model, "Search criteria " + search.searchType.orEmpty() + " not done")
        }
    }

    private fun searchPersonName(search: Search, model: Model): String {
        val persons = personService.searchName(restTemplate, search.searchCriteria.orEmpty())

        if (persons.isEmpty()){
            return setUpMovieHome(model, "No persons found with search criteria " + search.searchCriteria.orEmpty())
        }
        if (persons.size == 1) {
            return setUpOnePerson(model, persons.get(0).oid!!)
        }
        model.addAttribute("personList", persons)
        return "persons"
    }

    private fun setUpOnePerson(model: Model, oid: Long): String {
        model.addAttribute("personAllInfo", personService.getAllInfoOnePerson(restTemplate, oid))
        return "person"
    }

    private fun setUpMovieHome(model: Model ,message : String): String {
        model.addAttribute("movieAllInfoLatest", movieService.getLatestMovie(restTemplate))
        model.addAttribute("search", Search())
        model.addAttribute("message", message)
        return "movieHome"
    }

    private fun searchMovieOriginalTitle(search: Search, model: Model): String {
        val movies = movieService.searchOriginalTitle(restTemplate, search.searchCriteria.orEmpty())

        if (movies.isEmpty()){
            return setUpMovieHome(model, "No movies found with search criteria " + search.searchCriteria.orEmpty())
        }
        if (movies.size == 1) {
            return setUpMovie(model, movies.get(0).oid)
        }
        model.addAttribute("movieList", movies)
        return "movies"
    }

    private fun setUpMovie(model: Model, oid: Long): String {
        model.addAttribute("movieAllInfo", movieService.getAllInfoOneMovie(restTemplate, oid))
        return "movie"
    }

}