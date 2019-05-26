package com.obf.movie.obfmoviefrontend.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class Person(var oid: Long?,
                  var firstName: String?,
                  var lastName: String?,
                  var born: Date?,
                  var deceased: Date?,
                  var bornCountryOid: Long?,
                  var picture: String?,
                  var gender: String?,
                  var middleName: String?,
                  var bornName: String?) {
    constructor() : this(0, "", "", null, null, 0, "", "", "", "")
}