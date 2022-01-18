import java.io.File

fun readInput(name: String) = File("src/main/resources", "$name.txt").readLines()

fun <K, V> mapOf(list: List<Pair<K, V>>) = mapOf(*list.toTypedArray())
fun <K, V> mutableMapOf(list: List<Pair<K, V>>) = mutableMapOf(*list.toTypedArray())