package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.*
import com.obf.movie.obfmoviefrontend.service.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RequestMapping
import java.lang.Exception
import javax.servlet.http.HttpSession


@Controller
class WebController(private val mainService: MainService,
                    private val movieService: MovieService,
                    private val categoryService: CategoryService,
                    private val countryService: CountryService,
                    private val userService: UserService) {

    private val log = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping("/")
    fun home(): String {
        return "redirect:/login-page"
    }

    @GetMapping("/index")
    fun customerForm(model: Model): String {
        return "redirect:/login-page"
    }


    @GetMapping("/main-page")
    fun mainPage(model: Model, httpSession: HttpSession): String {

        return when {
            !userService.checkUser(httpSession, model) -> "redirect:/login-page"
            else -> {
                mainService.setupMainPage(httpSession, model)
            }
        }
    }


    @GetMapping("/manage")
    fun manage(model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> if (userService.checkUserLevel(httpSession, 5)){mainService.setUpManage(model)} else {"redirect:/movieHome"}
        }

    }

    @GetMapping("/login-guest")
    fun loginGuest(model: Model, httpSession: HttpSession): String {

        var loggedOnUser = LoggedOnUser("guest", 0, 1)
        model.addAttribute("user", loggedOnUser)
        httpSession.setAttribute("user", loggedOnUser)
        return "mainPage"
    }

    @PostMapping("/login")
    fun loginError(@ModelAttribute("login") login: Login, model: Model, httpSession: HttpSession): String {
        var loggedOnUser: LoggedOnUser
        try {
            loggedOnUser = userService.getAndCheckUser(login.password, login.username)
        } catch (e: Exception) {
            login.loginError = "Wrong user or password"
            model.addAttribute("login", login)
            return "login"
        }
        model.addAttribute("user", loggedOnUser)
        httpSession.setAttribute("user", loggedOnUser)
        return "mainPage"
    }


    @PostMapping("/add-new-category")
    fun addNewCategory(@ModelAttribute("manage") manage: Manage, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession, model) -> "redirect:/login-page"
            else -> {
                categoryService.saveNewCategory(manage)
                movieService.setUpMovieHome(model, "")
            }
        }
    }

    @PostMapping("/add-new-country")
    fun addNewCountry(@ModelAttribute("manage") manage: Manage, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession, model) -> "redirect:/login-page"
            else -> {
                countryService.saveNewCountry(manage)
                movieService.setUpMovieHome(model, "")
            }
        }
    }

    @PostMapping("/search")
    fun search(@ModelAttribute("search") search: Search, model: Model, httpSession: HttpSession): String {
        return mainService.handelSearch(search, model, httpSession)
    }


    @RequestMapping("/login-page")
    fun login(model: Model): String {
        model.addAttribute("login", Login("", "", ""))
        return "login"
    }

    @RequestMapping("/logout")
    fun logout(model: Model, httpSession: HttpSession): String {
        httpSession.removeAttribute("user")
        return "redirect:/login-page"
    }

    @GetMapping("/manage-user")
    fun manageUser(model: Model, httpSession: HttpSession): String {

        return when {
            !userService.checkUser(httpSession, model) -> "redirect:/login-page"
            else -> if (userService.checkUserLevel(httpSession, 10)){userService.setupManageUser(model)} else {"mainPage"}
        }
    }

    @PostMapping("/save-new-user")
    fun addNewUser(@ModelAttribute("user") user: User, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession, model) -> "redirect:/login-page"
            else -> {
                userService.saveNewUser(user, httpSession, model)
            }
        }
    }

    @RequestMapping("/remove-user/{userOid}")
    fun removeUser(@PathVariable userOid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> userService.removeUser(userOid, model)
        }
    }


}