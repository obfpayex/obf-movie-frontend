package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.Manage
import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.service.PersonService
import com.obf.movie.obfmoviefrontend.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpSession

@Controller
class WebControllerPerson(private val restTemplate: RestTemplate,
                          private val personService: PersonService,
                          private val userService: UserService) {



    @RequestMapping("/get-person/{oid}")
    fun getPerson(@PathVariable oid: Long, model: Model, httpSession: HttpSession): String {
        return when {
            userService.checkUser(httpSession,model) -> personService.setUpOnePerson(model, oid)
            else -> "redirect:/login-page"
        }
    }

    @GetMapping("/add-person")
    fun addPerson(model: Model, httpSession: HttpSession): String {
        return when {
            !userService.checkUser(httpSession,model) -> "redirect:/login-page"
            else -> if (userService.checkUserLevel(httpSession, 5)){personService.setUpAddPerson(model)} else {"redirect:/movieHome"}
        }
    }


    @PostMapping("/save-new-person")
    fun addNewPerson(@ModelAttribute("newPerson") newPerson: NewPerson, model: Model): String {
        return personService.saveNewPerson(restTemplate, newPerson, model)
    }

    @RequestMapping("/edit-person/{oid}")
    fun editPerson(@PathVariable oid: Long, model: Model): String {
        return personService.editPerson(model, oid)
    }

    @PostMapping("/update-person")
    fun updatePerson(@ModelAttribute("newPerson") newPerson: NewPerson, model: Model): String {
        return personService.updatePerson( newPerson, model)
    }

    @RequestMapping("/load-import-actor/{oid}")
    fun loadImportActor(@PathVariable oid: Long, model: Model): String {
        return personService.setupLoadImportActor(oid, model)
    }

    @PostMapping("/import-actor")
    fun importActor(@ModelAttribute("manage") manage: Manage, model: Model): String {
        return personService.importActors(manage, model)
    }


}