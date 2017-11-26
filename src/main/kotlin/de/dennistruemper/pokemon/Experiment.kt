package de.dennistruemper.pokemon

import de.dennistruemper.pokemon.web.PokeDataProvider
import de.dennistruemper.pokemon.web.Pokemon
import de.dennistruemper.pokemon.web.PokewikiDataProvider
import kotlinx.coroutines.experimental.*
import org.jsoup.Jsoup
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis


fun test() {
    val doc = Jsoup.connect ("http://example.com/").get()
}

fun main(args: Array<String>) {
    val dataProvider : PokeDataProvider = PokewikiDataProvider()
    val pokemonList = dataProvider.getPokemonList()
/*
    val time = measureTimeMillis {
        pokemonList.forEach {
            val pokemonDetails = dataProvider.getPokemonDetails(it)
        }
    }
    println("Took ${time} milliseconds")
    */

    val time2 = measureTimeMillis {
        pokemonList.parallelStream().forEach {dataProvider.getPokemonDetails(it)}
    }
    println("Took ${time2} milliseconds")


    val time3 = measureTimeMillis {
        runBlocking<Unit> {
            // given
            val counter = AtomicInteger(0)
            val numberOfCoroutines = pokemonList.size

            // when
            val jobs = List(numberOfCoroutines) {
                launch(CommonPool) {
                    getPokemonDetailsAsync(dataProvider, pokemonList.get(counter.getAndIncrement()))
                }
            }
            jobs.forEach { it.join() }
        }
        

    }
    println("Took ${time3} milliseconds")
    //jobs.forEach { it.join() }





    //pokemonList.forEach{p -> println(p.toString())}

}

suspend fun susTest(delay: Int){
    delay(delay.toLong())
}


suspend fun getPokemonDetailsAsync(dataProvider: PokeDataProvider, pokemon: Pokemon): String {
    return dataProvider.getPokemonDetails(pokemon)
}