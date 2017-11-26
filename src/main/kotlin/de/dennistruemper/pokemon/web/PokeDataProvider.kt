package de.dennistruemper.pokemon.web

interface PokeDataProvider {
    fun getPokemonList() : MutableList<Pokemon>
    fun getPokemonWithDetails(pokemon:Pokemon) : Pokemon
}

