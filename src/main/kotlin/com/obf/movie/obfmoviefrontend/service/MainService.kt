package com.obf.movie.obfmoviefrontend.service

import com.obf.movie.obfmoviefrontend.model.Manage
import com.obf.movie.obfmoviefrontend.model.Search
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import javax.servlet.http.HttpSession

@Service
class MainService(private val movieService: MovieService,
                  private val personService: PersonService,
                  private val userService: UserService) {


    fun handelSearch(search: Search, model: Model, httpSession: HttpSession): String {
        if (search.searchCriteria.isNullOrEmpty()) {
            return movieService.setUpMovieHome(model, "")
        }
        if ("movie".equals(search.searchType.orEmpty(), true)) {
            return searchMovieOriginalTitle(search, model, httpSession)
        } else if ("actor".equals(search.searchType.orEmpty(), true)) {
            return searchPersonName(search, model, httpSession)
        } else if ("all".equals(search.searchType.orEmpty(), true)) {
            model.addAttribute("user", httpSession.getAttribute("user"))
            return movieService.setUpMovieHome(model, "")
        } else {
            model.addAttribute("user", httpSession.getAttribute("user"))
            return movieService.setUpMovieHome(model, "Search criteria " + search.searchType.orEmpty() + " not done")
        }
    }

    private fun searchPersonName(search: Search, model: Model, httpSession: HttpSession): String {
        val persons = personService.searchName(search.searchCriteria.orEmpty())

        if (persons.isEmpty()) {
            return when {
                userService.checkUser(httpSession, model) -> movieService.setUpMovieHome(model, "No persons found with search criteria " + search.searchCriteria.orEmpty())
                else -> "redirect:/login-page"
            }
        }
        if (persons.size == 1) {
            return when {
                userService.checkUser(httpSession, model) -> personService.setUpOnePerson(model, persons[0].oid!!)
                else -> "redirect:/login-page"
            }
        }
        model.addAttribute("personList", persons)
        return "persons"
    }

    private fun searchMovieOriginalTitle(search: Search, model: Model, httpSession: HttpSession): String {
        if (!userService.checkUser(httpSession, model)) {
            return "redirect:/login-page"
        }

        val movies = movieService.searchOriginalTitle(search.searchCriteria.orEmpty())

        if (movies.isEmpty()) {
            return movieService.setUpMovieHome(model, "No persons found with search criteria " + search.searchCriteria.orEmpty())
        }
        if (movies.size == 1) {
            return movieService.setUpMovie(model, movies.get(0).oid!!, httpSession, false)
        }
        model.addAttribute("movieList", movies)
        return "movies"
    }

    fun setupMainPage(httpSession: HttpSession, model: Model): String {
        model.addAttribute("user", httpSession.getAttribute("user"))
        return "mainPage"
    }

    fun setUpManage(model: Model): String {
        model.addAttribute("manage", Manage())
        return "manage"
    }
}