import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round



fun MM(matrix: List<List<Int>>): Pair<Int, Int> {
    val mins = matrix.map { row -> min(row) }
    println("mins = $mins")
    val evalFunc = max(mins)
    println("evaluation function = $evalFunc")
    val bestVariant = mins.indexOf(evalFunc)
    println("best variant: $bestVariant")
    return evalFunc to bestVariant
}

fun Sevidge(matrix: List<List<Int>>): Pair<Int, Int> {
//    val maxs = matrix.map {row -> max(row) }
    val columns = matrix.first().size
    val transposed =
        matrix.first().indices.map { column -> matrix.flatten().run { slice(column until size step columns) } }
//    println("transposed: ")
//    println(transposed.joinToString("\n") { it.joinToString("\t\t") })
    val maxs =
        transposed.map { row -> row.maxOf { it } } //matrix.first().indices.map { column -> matrix.map { it[column] }.maxOf { it } }
    println("max result for each variant: $maxs")
    val loseMatrix = matrix.map { row -> row.mapIndexed { j, eij -> maxs[j] - eij } }
//    println("lose matrix:")
//    println(loseMatrix.joinToString("\n") { it.joinToString("\t\t") })
    val maxLoses = loseMatrix.map { it.maxOf { it } }
    val iterator = maxLoses.iterator()
    println("lose matrix: \t\t\t\t\t\t\tmaxj:")
    println(loseMatrix.joinToString("\n") { it.joinToString("\t\t", postfix = "\t|\t") + iterator.next() })
    val min = maxLoses.minOf { it }
    println("min max lose: $min")
    val bestVariant = maxLoses.indexOf(min)
    println("best variant: $bestVariant")
    return min to bestVariant
}

fun Hurwitz(matrix: List<List<Int>>, c: Float): Pair<Float, Int> {
    val minmaxs = matrix.map { row -> min(row) to max(row) }
    val ers = minmaxs.map { c * it.first + (1 - c) * it.second }
    println("matrix: \t\t\t\t\t\t\t\tmin: \t\t max: \t\t eir:")
    matrix.forEachIndexed { j, row -> println("${row.joinToString("\t\t")} \t|\t${minmaxs[j].first} \t\t|\t ${minmaxs[j].second} \t|\t ${ers[j]}") }
//    println(matrix.joinToString("\n") { it.joinToString("\t\t", postfix = "\t|\t") + erIter.next() })
    val max = max(ers)
    println("max: $max")
    val bestVariant = ers.indexOf(max)
    println("best variant: $bestVariant")
    return max to bestVariant
}

fun task1(matrix: List<List<Int>>, coefficient: Float) {
    matrix.forEachIndexed { index, ints -> println("$index ${ints.joinToString(",\t\t", "[", "\t]")}") }

    println("-----------")
    MM(matrix)
    println("-----------")
    Sevidge(matrix)
    println("-----------")
    Hurwitz(matrix, coefficient)
}

data class Price(val firstHalf: Int, val secondHalf: Int, val cost: Int) {
    constructor(prices: List<Int>) : this(prices[0], prices[1], prices[2])
}

data class DemandProbability(val quantity: Int, val probability: Float) {
    constructor(demand: Pair<Int, Float>) : this(demand.first,demand.second)
}

fun <K, V> mapOf(list: List<Pair<K, V>>) = mapOf(*list.toTypedArray())
fun <K, V> mutableMapOf(list: List<Pair<K, V>>) = mutableMapOf(*list.toTypedArray())


fun task2(price: Price, demProbs: List<DemandProbability>) {
    val orderDemand = mapOf(demProbs.map { it to mutableMapOf(demProbs.map { it.quantity.toFloat() } zip Array(demProbs.size) { 0f }) })

    println(demProbs.map { it.quantity }.joinToString("\t\t\t", prefix = "\t\t"))
    for((order,demand) in orderDemand){
        for(FHDemand in demand.keys){
            demand[FHDemand] = with(price) { firstHalf * min(order.quantity.toFloat(),FHDemand) + secondHalf * max(0f,order.quantity - FHDemand) - cost * order.quantity}
        }

        println("${order.quantity} \t${demand.values.joinToString(" \t\t")}")
    }
    println()
//    normalizing
    val ers = mutableMapOf(demProbs.map { it.quantity } zip Array(demProbs.size) {0f})
    println(demProbs.map{it.quantity}.joinToString("\t\t\t", prefix = "\t\t"))
    for((order,demand) in orderDemand){
        for(dem in demand.keys){
            demand[dem] = demand[dem]!! * demProbs.find { it.quantity.toFloat() == dem }!!.probability

        }
        ers[order.quantity] = demand.values.sum()
        println("${order.quantity} \t${demand.values.map { round(it * 100) / 100}.joinToString(" \t\t")} \t|\t ${ers[order.quantity]}")
    }
    val max = ers.maxOf { it.value }
    println("max: $max")
    val bestVariant = ers.entries.find { it.value == max }
    println("best variant: $bestVariant")
}

fun main() {

    val task1Input = readInput("task1")
    val coefficient = task1Input.first().toFloat()
    val matrix = task1Input.drop(1).map { it.split(' ').map { it.toInt() } }
    task1(matrix,coefficient)

    val task2Input = readInput("task2")
    val price = Price(task2Input[0].split(' ').map { it.toInt() })
    val demands = task2Input[1].split(' ').map { it.toInt() }
    val probabilities = task2Input[2].split(' ').map { it.toFloat() }
    val demProbs = (demands zip probabilities).map{ DemandProbability(it) }
    task2(price, demProbs)
}