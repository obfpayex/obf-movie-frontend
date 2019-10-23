package com.obf.movie.obfmoviefrontend.service

import org.springframework.stereotype.Service
import org.springframework.ui.Model


@Service
class RecipeService {
    fun setUpRecipeHome(model: Model, s: String): String {
        return "recipeHome"
    }
}