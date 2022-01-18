import java.util.*

data class Game(
    val name: String,
    val minPlayers: Int,
    val maxPlayers: Int,
    val minTimeMinutes: Int,
    val price: Int,
    val rulesComplexity: Int,
    val minAge: Int,
    val funStrat: Int
) {
    constructor(name: String, params: List<Int>) : this(
        name,
        params[0],
        params[1],
        params[2],
        params[3],
        params[4],
        params[5],
        params[6]
    )

    val params = listOf(minPlayers, maxPlayers, minTimeMinutes, price, rulesComplexity, minAge, funStrat)

    companion object {
        val moreIsBetter = listOf(false, true, false, false, false, false, false)
    }
}

typealias BinRelation = Array<Array<Boolean>>

fun buildRelation(data: List<Int>, moreIsBetter: Boolean = true) =
    BinRelation(data.size) { index1 ->
        data.mapIndexed { index2, it ->
            if (index1 != index2)
                if (moreIsBetter)
                    data[index1] >= it
                else
                    data[index1] <= it
            else
                false
        }.toTypedArray()
    }

fun BinRelation.toStr() = joinToString("\n") { it.joinToString("\t") }
fun List<BinRelation>.toStr() = joinToString("\n---------\n") { it.toStr() }
fun BinRelation.findDominators() =
    mapIndexed { index, it -> index to (it.count { it } == it.size - 1) }.filter { it.second }.map { it.first }

fun main() {

    val input = readInput("input")
    println(input.joinToString("\n"))
    val weights = input[0].split("\t+".toRegex()).drop(1).map { it.toDouble() }
    val paramsNames = input[1].split("\t+".toRegex()).drop(1)
    val data =
        input.drop(2).map { it.split("\t+".toRegex()).mapIndexed { index, s -> if (index > 0) s.toInt() else s } }
//    println(data.joinToString("\n"))
    val games = data.map { Game(it[0] as String, it.drop(1) as List<Int>) }
//    println(games.joinToString("\n"))

    val binRelations = List(games[0].params.size) { paramIndex ->
        buildRelation(
            games.map { it.params[paramIndex] },
            Game.moreIsBetter[paramIndex]
        )
    }

//    механизм доминирования
    val placesIndexes = domination(binRelations, games, paramsNames, weights)
    println(placesIndexes)
    val places = buildList {
        for (indexes in placesIndexes) {
            add(indexes.map { games[it].name })
        }
    }
    println(places)
}

private fun domination(
    binRelations: List<BinRelation>,
    games: List<Game>,
    paramsNames: List<String>,
    weights: List<Double>
): List<List<Int>> {

    val dominators = binRelations.map { it.findDominators() }
    val dominationsCount = dominators.flatten().groupingBy { it }.eachCount().toSortedMap()
    (games.indices).forEach { dominationsCount.putIfAbsent(it, 0) }
    dominators.forEachIndexed { index, ints -> println("${paramsNames[index]} ${weights[index]} $ints") }
    dominationsCount.forEach { println(it) }

    val points: SortedMap<Int, Double> = mapOf<Int, Double>().toSortedMap()
    for ((index, property) in dominators.withIndex()) {
        for (game in property) {
            points[game] = points.getOrDefault(game, 0.0) + weights[index]
        }
    }
    println(points)

    val placesIndexes =
        points.toList().sortedByDescending { it.second }.groupBy { it.second }.values.map { it.map { it.first } }
    return placesIndexes
}