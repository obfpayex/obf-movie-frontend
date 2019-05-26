package com.obf.movie.obfmoviefrontend.model

data class NewCategory(var movieOid: Long,
                       var categories: List<Category>?,
                       var categoryOid: Long?)
{
    constructor() : this(0, arrayListOf(), null )
}