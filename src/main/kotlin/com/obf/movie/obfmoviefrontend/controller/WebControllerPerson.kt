package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.model.Person
import com.obf.movie.obfmoviefrontend.service.CountryService
import com.obf.movie.obfmoviefrontend.service.PersonService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@Controller
class WebControllerPerson(private val restTemplate: RestTemplate,
                          private val personService: PersonService,
                          private val countryService: CountryService) {



    @RequestMapping("/get-person/{oid}")
    fun getPerson(@PathVariable oid: Long, model: Model): String {
        return personService.setUpOnePerson(model, oid)
    }

    @GetMapping("/add-person")
    fun addPerson(model: Model): String {
        return personService.setUpAddPerson(model)
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


}