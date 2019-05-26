package com.obf.movie.obfmoviefrontend.model


data class Search(
        var searchCriteria: String? = null,
        var searchType: String? = null,
        var searchTypes: List<String>?
){
    constructor(): this(null,null, arrayListOf())
}