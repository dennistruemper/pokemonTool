package de.dennistruemper.pokemon.web

interface PokeDataProvider {
    fun getPokemonList() : List<Pokemon>
    fun getPokemonDetails(pokemon:Pokemon) : String
}

