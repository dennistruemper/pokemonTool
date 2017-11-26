package de.dennistruemper.pokemon.test

import de.dennistruemper.pokemon.web.PokeDataProvider
import de.dennistruemper.pokemon.web.PokeType
import de.dennistruemper.pokemon.web.PokeTypeEnum
import de.dennistruemper.pokemon.web.PokewikiDataProvider
import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Test


class PokemonTest() {
    @Test
    fun pokewikiIstErreichbar() {
        val doc = Jsoup.connect ("http://www.pokewiki.de/").get()
        Assert.assertTrue(doc.body().toString().length > 0)
    }


    @Test
    fun getListOfAllPokemon(){
        val dataProvider : PokeDataProvider = PokewikiDataProvider()
        val pokemonList = dataProvider.getPokemonList()
        Assert.assertTrue("Not more than 150 Pokemon found, it was "+pokemonList.size,pokemonList.size > 150)

        val bisasamId = 1;
        val bisasam = pokemonList.get(bisasamId-1);

        Assert.assertEquals(bisasamId,bisasam.id)
        Assert.assertEquals("Bisasam",bisasam.name )
        Assert.assertEquals(PokeType(normal = listOf(PokeTypeEnum.Pflanze, PokeTypeEnum.Gift),alola = listOf()), bisasam.type)


        val kopplosioId = 806
        val kopplosio = pokemonList.get(kopplosioId - 1)

        Assert.assertEquals(kopplosioId,kopplosio.id)
        Assert.assertEquals("Kopplosio",kopplosio.name )
        Assert.assertEquals(PokeType(normal = listOf(PokeTypeEnum.Feuer, PokeTypeEnum.Geist),alola = listOf()), kopplosio.type)


    }
}