package com.obf.movie.obfmoviefrontend.controller

import com.obf.movie.obfmoviefrontend.service.RecipeService
import com.obf.movie.obfmoviefrontend.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpSession


@Controller
class WebControllerRecipe(private val userService: UserService,
                          private val recipeService: RecipeService) {


    @RequestMapping("/recipeHome")
    fun recipeHome(model: Model, httpSession: HttpSession): String {
        return when {
            userService.checkUser(httpSession, model) -> recipeService.setUpRecipeHome(model, "")
            else -> "redirect:/login-page"
        }

    }
}