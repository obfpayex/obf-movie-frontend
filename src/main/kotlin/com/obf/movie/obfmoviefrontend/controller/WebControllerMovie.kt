package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.NewMovie
import com.obf.movie.obfmoviefrontend.model.Search
import com.obf.movie.obfmoviefrontend.service.CategoryService
import com.obf.movie.obfmoviefrontend.service.CountryService
import com.obf.movie.obfmoviefrontend.service.MovieService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Controller
class WebControllerMovie(private val restTemplate: RestTemplate,
                         private val movieService: MovieService,
                         private val countryService: CountryService,
                         private val categoryService: CategoryService){

    @RequestMapping("/movieHome")
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

    private fun setUpMovieHome(model: Model ,message : String): String {
        model.addAttribute("movieAllInfoLatest", movieService.getLatestMovie(restTemplate))
        model.addAttribute("search", Search())
        model.addAttribute("message", message)
        return "movieHome"
    }

    private fun setUpMovie(model: Model, oid: Long): String {
        model.addAttribute("movieAllInfo", movieService.getAllInfoOneMovie(restTemplate, oid))
        return "movie"
    }

    @GetMapping("/add-movie")
    fun addMovie(model: Model): String {
        model.addAttribute("newMovie", NewMovie())
        model.addAttribute("categories",categoryService.getAllCategories(restTemplate))
        return "addMovie"
    }

    @PostMapping("/save-new-movie")
    fun addNewMovie(@ModelAttribute("newMovie") newMovie: NewMovie, model: Model): String {
        return movieService.saveNewMovie(restTemplate, newMovie, model)
    }


    @RequestMapping("/deleteMovie/{oid}")
    fun getPerson(@PathVariable oid: Long, model: Model): String {
        movieService.deleteMovie(restTemplate,oid)
        return setUpMovieHome(model, "")
    }
}