package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.NewActor
import com.obf.movie.obfmoviefrontend.model.NewDirector
import com.obf.movie.obfmoviefrontend.model.NewMovie
import com.obf.movie.obfmoviefrontend.model.Search
import com.obf.movie.obfmoviefrontend.service.CategoryService
import com.obf.movie.obfmoviefrontend.service.CountryService
import com.obf.movie.obfmoviefrontend.service.MovieService
import com.obf.movie.obfmoviefrontend.service.RoleTypeService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Controller
class WebControllerMovie(private val restTemplate: RestTemplate,
                         private val movieService: MovieService,
                         private val countryService: CountryService,
                         private val categoryService: CategoryService,
                         private val roleTypeService: RoleTypeService) {

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
        return movieService.setUpMovie(model, oid)
    }

    @RequestMapping("/save-chosen-actor-to-movie/{movieOid}/{personOid}/{roleTypeOid}/{charachter}")
    fun saveChosenActorToMovie(@PathVariable movieOid: Long,@PathVariable personOid: Long,@PathVariable roleTypeOid: Long,@PathVariable charachter: String, model: Model): String {
        return movieService.addChosenActorToMovie(movieOid, personOid, roleTypeOid,charachter, model)
    }

    @RequestMapping("/add-actor-to-movie-as-new/{movieOid}/{name}/{roleTypeOid}/{charachter}")
    fun addActorToMovieAsNew(@PathVariable movieOid: Long, @PathVariable name: String , @PathVariable roleTypeOid: Long,@PathVariable charachter: String, model: Model): String {
        return movieService.addActorToMovieAsNew(movieOid, name,  roleTypeOid,charachter, model)
    }





    @RequestMapping("/add-actor-to-movie/{oid}")
    fun addActorToMovie(@PathVariable oid: Long, model: Model): String {
        val newActor = NewActor()
        newActor.movieOid = oid
        model.addAttribute("newActor",newActor)
        model.addAttribute("roleTypes", roleTypeService.getAllRoleType(restTemplate))
        return "newActor"
    }

    @PostMapping("/save-actor-to-movie")
    fun saveActorToMovie(@ModelAttribute("newActor") newActor: NewActor, model: Model): String {
        return movieService.saveActorToMovie(restTemplate, newActor, model)
    }


    @RequestMapping("/add-director-to-movie/{oid}")
    fun addDirectorToMovie(@PathVariable oid: Long, model: Model): String {
        val newDirector = NewDirector()
        newDirector.movieOid = oid
        model.addAttribute("newDirector",newDirector)
        return "newDirector"
    }

    @RequestMapping("/add-director-to-movie-as-new/{movieOid}/{name}")
    fun addDirectorToMovieAsNew(@PathVariable movieOid: Long, @PathVariable name: String , model: Model): String {
        return movieService.addDirectorToMovieAsNew(movieOid, name,  model)
    }

    @RequestMapping("/save-chosen-director-to-movie/{movieOid}/{personOid}")
    fun saveChosenActorToMovie(@PathVariable movieOid: Long,@PathVariable personOid: Long, model: Model): String {
        return movieService.addChosenDirectorToMovie(movieOid, personOid, model)
    }

    @PostMapping("/save-director-to-movie")
    fun saveDirectorToMovie(@ModelAttribute("newDirector") newDirector: NewDirector, model: Model): String {
        return movieService.saveDirectorToMovie(restTemplate, newDirector, model)
    }

    @RequestMapping("/remove-director/{movieDirectorOid}/{movieOid}")
    fun removeDirector(@PathVariable movieDirectorOid: Long, @PathVariable movieOid: Long, model: Model): String {
        return movieService.removeDirector(model, movieDirectorOid, movieOid)
    }

    @RequestMapping("/remove-actor/{movieActorOid}/{movieOid}")
    fun removeActor(@PathVariable movieActorOid: Long, @PathVariable movieOid: Long, model: Model): String {
        return movieService.removeActor(model, movieActorOid, movieOid)
    }


    private fun setUpMovieHome(model: Model, message: String): String {
        model.addAttribute("movieAllInfoLatest", movieService.getLatestMovie(restTemplate))
        model.addAttribute("search", Search())
        model.addAttribute("message", message)
        return "movieHome"
    }



    @GetMapping("/add-movie")
    fun addMovie(model: Model): String {
        model.addAttribute("newMovie", NewMovie())
        model.addAttribute("categories", categoryService.getAllCategories(restTemplate))
        return "addMovie"
    }

    @PostMapping("/save-new-movie")
    fun addNewMovie(@ModelAttribute("newMovie") newMovie: NewMovie, model: Model): String {
        return movieService.saveNewMovie(restTemplate, newMovie, model)
    }


    @RequestMapping("/deleteMovie/{oid}")
    fun deletePerson(@PathVariable oid: Long, model: Model): String {
        movieService.deleteMovie(restTemplate, oid)
        return setUpMovieHome(model, "")
    }
}