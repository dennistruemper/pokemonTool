package de.dennistruemper.pokemon.web

import de.dennistruemper.pokemon.web.PokewikiDataProvider.PokeListAttributePositionEnum.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.net.URI

class PokewikiDataProvider : PokeDataProvider {
    override fun getPokemonList(): MutableList<Pokemon> {
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

    override fun getPokemonWithDetails(pokemon:Pokemon): Pokemon{
        val prefix = "https://www.pokewiki.de"
        val pokemonDetails = Jsoup.connect(prefix + pokemon.url)
        val pokemon = getPokemonDetailsFromSite(pokemonDetails, pokemon)
        return pokemon
    }

    private fun getPokemonDetailsFromSite(pokemonDetails: Connection?, pokemon: Pokemon): Pokemon {
        val statusValueTable = getStatusValueTable(pokemonDetails)
        val statusValues = getValueLists(statusValueTable)


        return pokemon.copy(statusValues = statusValues)


    }

    private fun getValueLists(statusValueTable: List<Element>) : StatusValues{

        val tableEntries = statusValueTable.first().select("td")
        val statusValuesList: MutableList<MutableList<String>> = ArrayList()
        var tempList: MutableList<String> = ArrayList()
        val numberRegex = Regex("\\d+")
        (tableEntries as Elements).forEach { entrie ->
            val elementText = entrie.text()
            if (numberRegex.matches(elementText)) {
                tempList.add(elementText)
            } else {
                if (tempList.size > 1) {
                    statusValuesList.add(tempList)
                }
                tempList = ArrayList()
                tempList.add(elementText)
            }
        }


        val statusValues :StatusValues = generateStatusValues(statusValuesList)
        return statusValues
    }

    private fun generateStatusValues(statusValuesList: MutableList<MutableList<String>>): StatusValues {
        var returnValue = StatusValues()
        statusValuesList.forEach { list ->
            val enumName = list.first().replace(".","").replace("-","")
            val enum = StatusValueType.valueOf(enumName)
            returnValue = addData(enum, list, returnValue)
        }

        return returnValue
    }

    private fun addData(enum: StatusValueType, list: MutableList<String>, statusValue : StatusValues): StatusValues {
        return when(enum) {
            StatusValueType.KP -> statusValue.copy(health = list.get(1).toInt())
            StatusValueType.Angriff -> statusValue.copy(attack = list.get(1).toInt())
            StatusValueType.Vert -> statusValue.copy(defense = list.get(1).toInt())
            StatusValueType.SpezAngr -> statusValue.copy(specialAttack = list.get(1).toInt())
            StatusValueType.SpezVert -> statusValue.copy(specialDefense = list.get(1).toInt())
            StatusValueType.Init -> statusValue.copy(initiative = list.get(1).toInt())
            else -> TODO()
        }
    }

    enum class StatusValueType{
        KP,
        Angriff,
        Vert,
        SpezAngr,
        SpezVert,
        Init
    }

    private fun getStatusValueTable(pokemonDetails: Connection?): List<Element> {
        val normalSite = pokemonDetails?.get()?.select("h3 + table")?.filter { it.toString().contains("Statuswerte") } ?: ArrayList()
        val megaSite = pokemonDetails?.get()?.select("table")?.filter { it.toString().contains("Statuswerte") } ?: ArrayList()
        if(normalSite.size > 0){
            return normalSite
        } else if (megaSite.size > 0){
            return megaSite
        } else {
            TODO()
        }
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




