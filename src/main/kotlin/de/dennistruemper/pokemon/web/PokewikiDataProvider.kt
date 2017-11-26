package de.dennistruemper.pokemon.web

import de.dennistruemper.pokemon.web.PokewikiDataProvider.PokeListAttributePositionEnum.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URI

class PokewikiDataProvider : PokeDataProvider {
    override fun getPokemonList(): List<Pokemon> {
        var pokemons:MutableList<Pokemon> = ArrayList<Pokemon>()
        val doc = Jsoup.connect ("https://www.pokewiki.de/Pok%C3%A9mon-Liste").get()
        val tableWithAllPokemon = doc.select(".pwtable1")
        val tableBodyRows = tableWithAllPokemon.select("tbody tr")
        tableBodyRows.forEach {row ->
            var pokemon = Pokemon()
            val values = row.select("td")
            values.forEachIndexed { index, element ->
                pokemon = parseValue(index, element, pokemon)
            }
            if (pokemon.isValid()) {
                pokemons.add(pokemon)
            }
        }


        return pokemons
    }

    private fun parseValue(index: Int, element: Element?, pokemon: Pokemon): Pokemon {
        return when(index) {
            Id.ordinal -> setId(element, pokemon)
            NameGermany.ordinal -> setNameAndUrl(element, pokemon)
            Type.ordinal -> setType(element, pokemon)
            else -> pokemon
        }
    }

    override fun getPokemonDetails(pokemon:Pokemon): String{
        val prefix = "https://www.pokewiki.de"
        val pokemonDetails = Jsoup.connect(prefix + pokemon.url)
        return pokemon.name + pokemonDetails.get().toString().length
    }





    private fun setId(element: Element?, pokemon: Pokemon): Pokemon {
        val newIdPokemon =  pokemon.copy(id = element?.textNodes()?.last()?.wholeText?.trim()?.toInt() ?: -1);
        return newIdPokemon
    }

    private fun setNameAndUrl(element: Element?, pokemon: Pokemon): Pokemon {
        val link = element?.child(0)?.attr("href") ?: "";
        val name = element?.child(0)?.attr("title")?: "";

        return pokemon.copy(name = name, url = URI(link))
    }

    private fun setType(element: Element?, pokemon: Pokemon): Pokemon {
        val string = element.toString()
        val rawTypes = Regex("title=\"\\w+\"").findAll(string).toList()
        return pokemon.copy(type = getTypeFromInput(rawTypes))
    }

    private fun getTypeFromInput(rawTypes: List<MatchResult>): PokeType {
        var returnType = PokeType()
        val normalTypes = ArrayList<PokeTypeEnum>()
        rawTypes.forEach {
            match ->
            val typeAsString = match.value.subSequence(7,match.value.length-1).toString()
            normalTypes.add(PokeTypeEnum.valueOf(typeAsString))
        }
        return returnType.copy(normal = normalTypes)
    }


    enum class PokeListAttributePositionEnum {
        Id,
        Picture,
        NameGermany,
        NameEngland,
        NameFrance,
        NameJapan,
        NameKorea,
        NameChina,
        Type
    }

}




