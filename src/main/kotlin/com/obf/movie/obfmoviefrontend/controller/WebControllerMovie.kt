package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.*
import com.obf.movie.obfmoviefrontend.service.*
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpSession

@Controller
class WebControllerMovie(private val restTemplate: RestTemplate,
                         private val movieService: MovieService,
                         private val userService: UserService) {

    @RequestMapping("/movieHome")
    fun movieHome(model: Model, httpSession: HttpSession): String {
        return when {
            userService.checkUser(httpSession, model) -> movieService.setUpMovieHome(model, "")
            else -> "redirect:/login-page"
        }

    }

    @GetMapping("/listMovies")
    fun listMovies(model: Model): String {
        val movies = movieService.getAllMovies(restTemplate)
        model.addAttribute("movieList", movies)
        return "movies"
    }

    @RequestMapping("/getMovie/{oid}")
    fun getMovie(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return setupMovie(httpSession, model, oid, false)
    }

    @RequestMapping("/show-all-actor-to-movie/{oid}")
    fun showAllActorsToMovie(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return setupMovie(httpSession, model, oid, true)
    }

    @RequestMapping("/hide-all-actor-to-movie/{oid}")
    fun hideAllActorsToMovie(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return setupMovie(httpSession, model, oid, false)
    }
    private fun setupMovie(httpSession: HttpSession, model: Model, oid: Long, showAll: Boolean): String {
        return when {
            userService.checkUser(httpSession, model) -> movieService.setUpMovie(model, oid, httpSession, showAll)
            else -> "redirect:/login-page"
        }
    }

    @RequestMapping("/save-chosen-actor-to-movie/{movieOid}/{personOid}/{roleTypeOid}/{charachter}")
    fun saveChosenActorToMovie(@PathVariable movieOid: Long,@PathVariable personOid: Long,@PathVariable roleTypeOid: Long,@PathVariable charachter: String, model: Model,  httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.addChosenActorToMovie(movieOid, personOid, roleTypeOid, charachter, model, httpSession)
        }
    }

    @RequestMapping("/add-actor-to-movie-as-new/{movieOid}/{name}/{roleTypeOid}/{charachter}")
    fun addActorToMovieAsNew(@PathVariable movieOid: Long, @PathVariable name: String , @PathVariable roleTypeOid: Long,@PathVariable charachter: String, model: Model,   httpSession: HttpSession): String {

        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else ->  movieService.addActorToMovieAsNew(movieOid, name,  roleTypeOid,charachter, model, httpSession)
        }

    }

    @RequestMapping("/add-actor-to-movie/{oid}")
    fun addActorToMovie(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.setUpAddActorToMovie(oid, model)
        }
    }

    @PostMapping("/save-actor-to-movie")
    fun saveActorToMovie(@ModelAttribute("newActor") newActor: NewActor, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.saveActorToMovie(restTemplate, newActor, model, httpSession)
        }
    }

    @RequestMapping("/add-director-to-movie/{oid}")
    fun addDirectorToMovie(@PathVariable oid: Long, model: Model): String {
        return movieService.setUpAddDirectorToMovie(oid, model)
    }


    @RequestMapping("/add-director-to-movie-as-new/{movieOid}/{name}")
    fun addDirectorToMovieAsNew(@PathVariable movieOid: Long, @PathVariable name: String , model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.addDirectorToMovieAsNew(movieOid, name, model, httpSession)
        }
    }

    @RequestMapping("/save-chosen-director-to-movie/{movieOid}/{personOid}")
    fun saveChosenActorToMovie(@PathVariable movieOid: Long,@PathVariable personOid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.addChosenDirectorToMovie(movieOid, personOid, model, httpSession)
        }
    }

    @PostMapping("/save-director-to-movie")
    fun saveDirectorToMovie(@ModelAttribute("newDirector") newDirector: NewDirector, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.saveDirectorToMovie(restTemplate, newDirector, model, httpSession)
        }
    }

    @RequestMapping("/remove-director/{movieDirectorOid}/{movieOid}")
    fun removeDirector(@PathVariable movieDirectorOid: Long, @PathVariable movieOid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.removeDirector(model, movieDirectorOid, movieOid, httpSession)
        }
    }

    @RequestMapping("/remove-actor/{movieActorOid}/{movieOid}")
    fun removeActor(@PathVariable movieActorOid: Long, @PathVariable movieOid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.removeActor(model, movieActorOid, movieOid, httpSession)
        }
    }

    @GetMapping("/add-movie")
    fun addMovie(model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> if (userService.checkUserLevel(httpSession, 5)){movieService.setUpAddMovie(model)} else {"redirect:/movieHome"}
        }
    }

    @PostMapping("/save-new-movie")
    fun addNewMovie(@ModelAttribute("newMovie") newMovie: NewMovie, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.saveNewMovie(restTemplate, newMovie, model, httpSession)
        }
    }

    @RequestMapping("/deleteMovie/{oid}")
    fun deletePerson(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> {
                movieService.deleteMovie(restTemplate, oid)
                movieService.setUpMovieHome(model, "")
            }
        }

    }

    @RequestMapping("/add-category/{movieOid}")
    fun getCategories(@PathVariable movieOid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.addCategory(movieOid, model)
        }
    }

    @PostMapping("/save-new-category")
    fun saveNewCategory(@ModelAttribute("newCategory") newCategory: NewCategory, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.saveNewCategory(newCategory, model, httpSession)
        }
    }

    @RequestMapping("/edit-movie/{oid}")
    fun editMovie(@PathVariable oid: Long, model: Model): String {
        return movieService.editMovie(model, oid)
    }


    @PostMapping("/update-movie")
    fun updateMovie(@ModelAttribute("newMovie") newMovie: NewMovie, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> movieService.updateMovie( newMovie, model, httpSession)
        }
    }



}