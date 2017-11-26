package de.dennistruemper.pokemon

import de.dennistruemper.pokemon.web.PokeDataProvider
import de.dennistruemper.pokemon.web.Pokemon
import de.dennistruemper.pokemon.web.PokewikiDataProvider
import kotlinx.coroutines.experimental.*
import org.jsoup.Jsoup
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis



fun main(args: Array<String>) {
    val dataProvider : PokeDataProvider = PokewikiDataProvider()
    val pokemonList = dataProvider.getPokemonList()

    val timePrimitive = measureTimeMillis {
        pokemonList.forEach {
            val pokemonDetails = dataProvider.getPokemonDetails(it)
        }
    }
    println("Took ${timePrimitive} milliseconds with naive")


    val timeParallelStream = measureTimeMillis {
        pokemonList.parallelStream().forEach {dataProvider.getPokemonDetails(it)}
    }
    println("Took ${timeParallelStream} milliseconds with parallelStream")


    val timeCoroutines = measureTimeMillis {
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
    println("Took ${timeCoroutines} milliseconds with coroutines")


}

suspend fun susTest(delay: Int){
    delay(delay.toLong())
}


suspend fun getPokemonDetailsAsync(dataProvider: PokeDataProvider, pokemon: Pokemon): String {
    return dataProvider.getPokemonDetails(pokemon)
}