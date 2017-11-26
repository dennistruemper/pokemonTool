package de.dennistruemper.pokemon.web

data class PokeType (val normal : List<PokeTypeEnum>, val alola :List<PokeTypeEnum>){
    constructor() : this(ArrayList<PokeTypeEnum>(),ArrayList<PokeTypeEnum>())
}