import kotlin.math.*
import kotlin.random.Random

data class Price(val firstYear: Int, val secondYear: Int, val thirdYear: Int, val cost: Int) {
    constructor(prices: List<Int>) : this(prices[0], prices[1], prices[2], prices[3])
}

fun Array<IntArray>.toStr(sums: FloatArray? = null) = joinToString("\n") {
    it.joinToString("\t", postfix = "\t") {
        it.toString().padEnd(5)
    } + (sums?.get(indexOf(it)) ?: "")
}

fun Float.round(digits: Int) = times(10f.pow(digits)).roundToInt().div(10f.pow(digits))

operator fun String.times(number: Int) = let { str -> buildString { repeat(number) { append(str) } }.toString() }

fun generateProbabilities(size: Int) = buildList {
    var maxProbability = 1.0
    for (i in 0 until size) {
        val probability = Random.nextDouble(maxProbability)
        maxProbability -= probability
        add(probability)
    }
}.map { it.toFloat() }


fun main() {

    val taskInput = readInput("task")
    val price = Price(taskInput[0].split(' ').map { it.toInt() })
    val quantity =
        taskInput[1].split(' ').map { it.toInt() }.run { component1()..component2() step component3() }.toList()
    println(price)
    println(quantity)

    val firstYear = Array(quantity.size) { IntArray(quantity.size) }
    val secThirdYear = Array(quantity.size) { IntArray(quantity.size) }

    val firstYearProb = //listOf(0.1, 0.12, 0.3, 0.18, 0.14, 0.11, 0.05).map { it.toFloat() }.toFloatArray()
        generateProbabilities(quantity.size)
    val secThirdYearProb = //listOf(0.05, 0.07, 0.1, 0.2, 0.28, 0.2, 0.1).map { it.toFloat() }.toFloatArray()
        generateProbabilities(quantity.size)

//    filling tables
    for (i in quantity.indices) {
        for (j in quantity.indices) {
            firstYear[i][j] = min(quantity[i], quantity[j]) * price.firstYear - quantity[i] * price.cost
            secThirdYear[i][j] = min(quantity[i], quantity[j]) * price.secondYear + max(0, quantity[i] - quantity[j])
        }
    }

    println(firstYear.toStr())
    println("-------")
    println(secThirdYear.toStr())

    val firstYearSums = FloatArray(quantity.size)
    val secThirdYearSums = FloatArray(quantity.size)

    for (i in quantity.indices) {
        firstYearSums[i] = firstYear[i].mapIndexed { j, eij -> eij * firstYearProb[j] }.sum().round(2)
        secThirdYearSums[i] = secThirdYear[i].mapIndexed { j, eij -> eij * secThirdYearProb[j] }.sum().round(2)
    }

    println("First year" + "\t" * 12 + "Weighted sum")
    println(firstYear.toStr(firstYearSums))
    println("-------")
    println("Second/Third year" + "\t" * 10 + "Weighted sum")
    println(secThirdYear.toStr(secThirdYearSums))

    val totalSums = FloatArray(quantity.size)

    for (i in quantity.indices) {
        totalSums[i] = firstYearSums[i] +
                secThirdYearSums.sliceArray((i downTo 0).toList())
                    .mapIndexed { j, S -> firstYearProb[j] * S }.sum()
    }

    println("--------")
    println(totalSums.joinToString("\n") { it.round(2).toString() })
    val bestVariant = totalSums.withIndex().maxByOrNull { it.value }!!
    println("max = ${bestVariant.value} best variant = ${bestVariant.index} quantity = ${quantity[bestVariant.index]}")
}