package de.dennistruemper.pokemon.web

import java.net.URI

data class Pokemon (val id:Int, val name: String, val type : PokeType, val url: URI){
    constructor() : this(id = 0, name = "", type = PokeType(), url = URI(""))

    fun isValid(): Boolean {
        var returnValue = true;
        returnValue = returnValue && id != 0;
        returnValue = returnValue && name != "";
        //returnValue = returnValue && type.normal.size > 0;
        return returnValue;
    }
}


