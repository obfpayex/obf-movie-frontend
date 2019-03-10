package com.obf.movie.obfmoviefrontend.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping


@Controller
class WebController {

    private val log = LoggerFactory.getLogger(WebController::class.java)

    @GetMapping("/")
    fun home(): String{
        return "redirect:/index"
    }

    @GetMapping("/index")
    fun customerForm(model: Model): String{
        //model.addAttribute("customer", Customer())
        return "index"
    }

    @PostMapping("/index")
    fun customerSubmit(model: Model): String{
        return "movies"
    }



//    @PostMapping("/form")
//    fun customerSubmit(@ModelAttribute("customer") customer: Customer, model: Model): String{
//        model.addAttribute("customer", customer)
//        val info = String.format("Customer Submission: name = %s, age = %d, street = %s, postcode = %s",
//                customer.name, customer.age, customer.address.street, customer.address.postcode)
//        log.info(info)
//        return "result"
//    }
}