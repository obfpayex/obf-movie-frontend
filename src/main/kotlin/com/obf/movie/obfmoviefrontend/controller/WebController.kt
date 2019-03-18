package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.Movie
import com.obf.movie.obfmoviefrontend.service.MovieService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate


@Controller
class WebController(private val restTemplate: RestTemplate,private val movieService: MovieService) {

    private val log = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping("/")
    fun home(): String{
        return "redirect:/index"
    }

    @GetMapping("/index")
    fun customerForm(model: Model): String{
        return "index"
    }

    @PostMapping("/getMovies")
    fun getMovies(model: Model): String{
        val movies = movieService.getAllMovies(restTemplate)
        log.debug("Number of movies: " + movies.size)
        model.addAttribute("movieList",movies)
        return "movies"
    }

    @RequestMapping("/getMovie/{oid}")
    fun getMovie(@PathVariable oid: Long, model: Model): String{
        //model.addAttribute("movie",movieService.getOneMovie(restTemplate,oid))
        model.addAttribute("movieAllInfo",movieService.getAllInfoOneMovie(restTemplate,oid))
        return "movie"
    }


}