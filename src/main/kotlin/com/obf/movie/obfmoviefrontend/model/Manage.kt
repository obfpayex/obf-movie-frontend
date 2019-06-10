package com.obf.movie.obfmoviefrontend.model

data class Manage(var oid: Long?,
                  var text: String?,
                  var number: Long?)
{
    constructor(): this(null, null, null)
}