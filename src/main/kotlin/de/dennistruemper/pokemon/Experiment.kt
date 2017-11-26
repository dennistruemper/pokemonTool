package de.dennistruemper.pokemon

import de.dennistruemper.pokemon.web.PokeDataProvider
import de.dennistruemper.pokemon.web.Pokemon
import de.dennistruemper.pokemon.web.PokewikiDataProvider
import kotlinx.coroutines.experimental.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis



fun main(args: Array<String>) {
    val dataProvider : PokeDataProvider = PokewikiDataProvider()
    val pokemonList : MutableList<Pokemon> = dataProvider.getPokemonList()

    /* Slower alternatives
    val timePrimitive = measureTimeMillis {
        pokemonList.forEach {
            val pokemonDetails = dataProvider.getPokemonWithDetails(it)
        }
    }
    println("Took ${timePrimitive} milliseconds with naive")



    val timeParallelStream = measureTimeMillis {
        pokemonList.parallelStream().map {
            pokemon ->
            dataProvider.getPokemonWithDetails(pokemon)
        }

    }
    println("Took ${timeParallelStream} milliseconds with parallelStream")
*/


    val timeCoroutines = measureTimeMillis {
        runBlocking<Unit> {
            // given
            val counter = AtomicInteger(0)
            val numberOfCoroutines = pokemonList.size

            // when
            val jobs = List(numberOfCoroutines) {
                launch(CommonPool) {
                    val count = counter.getAndIncrement()
                    pokemonList.set(count,  getPokemonWithDetailsAsync(dataProvider, pokemonList.get(count)))
                }
            }
            jobs.forEach { it.join() }
        }
        

    }
    println("Took ${timeCoroutines} milliseconds with coroutines")


    pokemonList.forEach {
        //println(it)
    }

    val orderedByStatussum = pokemonList.sortBy{ it.getStatusSum() }

    pokemonList.forEach {
        println(it)
    }
}

suspend fun susTest(delay: Int){
    delay(delay.toLong())
}


suspend fun getPokemonWithDetailsAsync(dataProvider: PokeDataProvider, pokemon: Pokemon): Pokemon {
    return dataProvider.getPokemonWithDetails(pokemon)
}