package com.obf.movie.obfmoviefrontend.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.obf.movie.obfmoviefrontend.model.NewPerson
import com.obf.movie.obfmoviefrontend.model.Person
import com.obf.movie.obfmoviefrontend.model.PersonAllInfo
import com.obf.movie.obfmoviefrontend.util.Constants
import com.obf.movie.obfmoviefrontend.util.Constants.URL_AllInfoPerson
import com.obf.movie.obfmoviefrontend.util.Constants.URL_Person
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.ui.Model
import org.springframework.web.client.RestTemplate


@Service
class PersonService (private val restTemplate: RestTemplate,
                     private val countryService: CountryService){

    fun getAllInfoOnePerson(restTemplate: RestTemplate, oid: Long): PersonAllInfo {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<PersonAllInfo> = restTemplate.exchange(
                URL_AllInfoPerson + oid,
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<PersonAllInfo>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return result.body as PersonAllInfo
        } else {
            throw Exception("Could not find actor with oid : " + oid)
        }
    }

    fun getPerson(oid: Long): Person {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)

        val result: ResponseEntity<Person> = restTemplate.exchange(
                "$URL_Person/$oid",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<Person>())

        if (result?.statusCode === HttpStatus.OK && result.hasBody()) {
            return result.body as Person
        } else {
            throw Exception("Could not find person with oid : " + oid)
        }
    }


    fun searchName(restTemplate: RestTemplate, name: String): List<Person> {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        val urlParameters = StringBuilder()


        val names = name.split(" ")

        if (names.size > 1) {
            when {
                names.size == 2 -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&lastName=").append(names[1])
                }
                names.size == 3 -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&middleName=").append(names[1])
                    urlParameters.append("&lastName=").append(names[2])
                }
                else -> {
                    urlParameters.append("?firstName=").append(names[0])
                    urlParameters.append("&middleName=")
                    for (i in 1..names.size - 2) {
                        if (i > 1) {
                            urlParameters.append(" ")
                        }
                        urlParameters.append(names[i])
                    }

                    urlParameters.append("&lastName=").append(names[names.size - 1])
                }
            }
            urlParameters.append("&useOr=false")
        } else {
            urlParameters.append("?firstName=").append(name)
            urlParameters.append("&middleName=").append(name)
            urlParameters.append("&lastName=").append(name)
            urlParameters.append("&useOr=true")
        }

        val result: ResponseEntity<List<Person>> = restTemplate.exchange(
                Constants.URL_SearchPersonName + urlParameters.toString() + "&size=100&page=0&sort=oid,asc",
                HttpMethod.GET,
                HttpEntity("parameters", headers),
                typeRef<List<Person>>())


        return result.body as List
    }

    fun saveNewPerson(restTemplate: RestTemplate, newPerson: NewPerson, model: Model): String {
        val person = saveNewPersonDb(newPerson)
        model.addAttribute("personAllInfo", getAllInfoOnePerson(restTemplate, person.oid!!))
        return "person"
    }

    fun saveNewPersonDb(newPerson: NewPerson): Person {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val json = ObjectMapper().writeValueAsString(newPerson)
        val entity = HttpEntity(json, headers)
        val result = restTemplate.postForEntity(URL_Person, entity, String::class.java)

        if (result?.statusCode === HttpStatus.CREATED && result.hasBody()) {
            val jsonResult = result.body as String
            val person: Person = ObjectMapper().readValue(jsonResult)
            return person

        } else {
            throw Exception("Could not save person")
        }
    }

    fun updatePersonDb(newPerson: NewPerson): Person {
        val headers = HttpHeaders()
        headers.set("Session-Id", null)
        headers.contentType = MediaType.APPLICATION_JSON;

        val person = copyNewPersonToPerson(newPerson)
        val json = ObjectMapper().writeValueAsString(person)
        val entity = HttpEntity(json, headers)
        restTemplate.put(URL_Person, entity, String::class.java)
        return person
    }

    fun saveNewPerson(name: String): Person {
        var firstname = ""
        var middleName = ""
        var lastName = ""
        val names = name.split(" ")

        if (names.size > 1) {
            when {
                names.size == 2 -> {
                    firstname = names[0]
                    lastName = names[1]
                }
                names.size == 3 -> {
                    firstname = names[0]
                    middleName = names[1]
                    lastName = names[2]
                }
                else -> {
                    firstname = names[0]
                    for (i in 1..names.size - 2) {
                        if (i > 1) {
                            middleName = middleName +" "
                        }
                        middleName = middleName + names[i]
                    }
                    lastName = names[names.size - 1]
                }
            }
        } else {
            lastName = name
        }


        val newPerson = NewPerson(null,firstname,lastName,null,null,null,null,null,middleName,name)

        return saveNewPersonDb(newPerson)

    }

    fun editPerson(model: Model, oid: Long): String {
        model.addAttribute("newPerson",copyPersonToNewPerson(getPerson(oid)))
        model.addAttribute("countries",countryService.getAllCountries())
        return "editPerson"
    }

    fun updatePerson(newPerson: NewPerson, model: Model): String {

        updatePersonDb(newPerson)

        return setUpOnePerson(model, newPerson.oid!!)
    }


    fun setUpOnePerson(model: Model, oid: Long): String {
        model.addAttribute("personAllInfo", getAllInfoOnePerson(restTemplate, oid))
        return "person"
    }

    fun setUpAddPerson(model: Model): String {
        model.addAttribute("newPerson", NewPerson())
        model.addAttribute("countries", countryService.getAllCountries())
        return "addperson"
    }


    fun copyPersonToNewPerson(person: Person): NewPerson{
        val json = ObjectMapper().writeValueAsString(person);
        return ObjectMapper().readValue<NewPerson>(json);

    }

    fun copyNewPersonToPerson(newPerson: NewPerson): Person{
        val json = ObjectMapper().writeValueAsString(newPerson);
        return ObjectMapper().readValue<Person>(json);

    }
}