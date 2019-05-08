package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.model.NewPerson
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



    @RequestMapping("/getPerson/{oid}")
    fun getPerson(@PathVariable oid: Long, model: Model): String {
        return setUpOnePerson(model, oid)
    }

    @GetMapping("/add-person")
    fun addPerson(model: Model): String {
        model.addAttribute("newPerson", NewPerson())
        model.addAttribute("countries",countryService.getAllCountries(restTemplate))
        return "addperson"
    }

    @PostMapping("/save-new-person")
    fun addNewPerson(@ModelAttribute("newPerson") newPerson: NewPerson, model: Model): String {
        return personService.saveNewPerson(restTemplate, newPerson, model)
    }

    private fun setUpOnePerson(model: Model, oid: Long): String {
        model.addAttribute("personAllInfo", personService.getAllInfoOnePerson(restTemplate, oid))
        return "person"
    }
}