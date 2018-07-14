import java.io.File
import kotlin.system.measureTimeMillis

val fileList = listOf("chr25a.dat","nug30.dat", "rou20.dat", "sko42.dat", "ste36a.dat", "tai50a.dat")

fun isValidSolution(ind: Individual): Boolean {
    val positions = (0 until ind.size).toMutableSet()
    positions.removeAll(ind.solution.toList())
    return positions.isEmpty()
}

fun individualVerification (args: Array<String>) {
    val files = listOf(
        "AG-Base",
        "AG-E",
        "AG-MP",
        "AG-MPE",
        "AGH-Base",
        "AGH-E",
        "AGH-MP",
        "AGH-MPE"
    )
    val elitist = arrayOf(false, true, false, true, false, true, false, true)
    val multiparent = arrayOf(false, false, true, true, false, false, true, true)
    val hybrid = arrayOf(false, false, false, false, true, true, true, true)
    for ((index, f) in files.withIndex()) {
        fileList.forEach { file ->
            val csv = File("/home/gabriel/Files/Verification/individual-verification-$file")
            val matrices = readFile(File("${args[0]}/$file"))
            val algorithm = GeneticAlgorithm(matrices)
            algorithm.loop(elitist[index], multiparent[index], hybrid[index], true)
            algorithm.bestSolutions.forEach {
                csv.appendText("${it.evaluated};")
            }
            csv.appendText("$f\n")
        }
    }
}

fun elitismSizeVerification (args: Array<String>) {
    val files = listOf(
        "AG-E",
        "AG-MPE",
        "AGH-E",
        "AGH-MPE"
    )
    val eliteSizes = listOf (1, 2, 5, 10)
    val multiparent = arrayOf(false, true, false, true)
    val hybrid = arrayOf(false, false, true, true)
    for ((index, f) in files.withIndex()) {
        eliteSizes.forEach { size ->
            val csv = File("/home/gabriel/Files/Verification/EliteSize/elitism-size:$size-verification-$f")
            if (csv.exists()) {
                csv.delete()
                csv.createNewFile()
            }
            eliteSize = size
            File(args[0]).walk().forEach { file ->
                if (file.extension == "dat") {
                    val matrices = readFile(file)
                    val times = mutableListOf<Long>()
                    repeat(5) {
                        val algorithm = GeneticAlgorithm(matrices)
                        lateinit var individual: Individual
                        val time = measureTimeMillis {
                            individual = algorithm.loop(true, multiparent[index], hybrid[index])
                        }
                        times.add(time)
                        csv.appendText("${individual.evaluated};")
                        if (isValidSolution(individual)) {
                            individual.print()
                            println("Time elapsed: ${time}ms")
                        } else
                            println("--- INVALID INDIVIDUAL ---")
                    }
                    repeat(5) {
                        if (it == 4)
                            csv.appendText("${times[it]};${file.name}\n")
                        else
                            csv.appendText("${times[it]};")
                    }
                }
            }
        }
    }
    eliteSize = 20
}

fun elitismCrossoverVerification (args: Array<String>) {
    val files = listOf(
        "AG-E",
        "AG-MPE",
        "AGH-E",
        "AGH-MPE"
    )
    eliteCrossover = false
    val multiparent = arrayOf(false, true, false, true)
    val hybrid = arrayOf(false, false, true, true)
    for ((index, f) in files.withIndex()) {
        val csv = File("/home/gabriel/Files/Verification/EliteCrossover/elitism-crossover-verification-$f")
        if (csv.exists()) {
            csv.delete()
            csv.createNewFile()
        }
        File(args[0]).walk().forEach { file ->
            if (file.extension == "dat") {
                val matrices = readFile(file)
                val times = mutableListOf<Long>()
                repeat(5) {
                    val algorithm = GeneticAlgorithm(matrices)
                    lateinit var individual: Individual
                    val time = measureTimeMillis {
                        individual = algorithm.loop(true, multiparent[index], hybrid[index])
                    }
                    times.add(time)
                    csv.appendText("${individual.evaluated};")
                    if (isValidSolution(individual)) {
                        individual.print()
                        println("Time elapsed: ${time}ms")
                    } else
                        println("--- INVALID INDIVIDUAL ---")
                }
                repeat(5) {
                    if (it == 4)
                        csv.appendText("${times[it]};${file.name}\n")
                    else
                        csv.appendText("${times[it]};")
                }
            }
        }
    }
    eliteCrossover = true
}

fun multiparentSizeVerification (args: Array<String>) {
    val files = listOf(
        "AG-MP",
        "AG-MPE",
        "AGH-MP",
        "AGH-MPE"
    )
    val totalParents = listOf (4, 5, 6, 7)
    val elitist = arrayOf(false, true, false, true)
    val hybrid = arrayOf(false, false, true, true)
    for ((index, f) in files.withIndex()) {
        totalParents.forEach { size ->
            val csv = File("/home/gabriel/Files/Verification/MultiParent/multiparent-size:$size-verification-$f")
            if (csv.exists()) {
                csv.delete()
                csv.createNewFile()
            }
            numberParents = size
            numberEliteParents = size / 2
            File(args[0]).walk().forEach { file ->
                if (file.extension == "dat") {
                    val matrices = readFile(file)
                    val times = mutableListOf<Long>()
                    repeat(5) {
                        val algorithm = GeneticAlgorithm(matrices)
                        lateinit var individual: Individual
                        val time = measureTimeMillis {
                            individual = algorithm.loop(elitist[index], true, hybrid[index])
                        }
                        times.add(time)
                        csv.appendText("${individual.evaluated};")
                        if (isValidSolution(individual)) {
                            individual.print()
                            println("Time elapsed: ${time}ms")
                        } else
                            println("--- INVALID INDIVIDUAL ---")
                    }
                    repeat(5) {
                        if (it == 4)
                            csv.appendText("${times[it]};${file.name}\n")
                        else
                            csv.appendText("${times[it]};")
                    }
                }
            }
        }
    }
    numberParents = 3
    numberEliteParents = 1
}

fun results (args: Array<String>) {
    val files = listOf(
        "result-base.csv",
        "result-elitism.csv",
        "result-multi-parent.csv",
        "result-multi-parent-elitism.csv",
        "result-hybrid-ils.csv",
        "result-hybrid-ils-elitism.csv",
        "result-hybrid-ils-multi-parent.csv",
        "result-hybrid-ils-multi-parent-elitism.csv"
    )
    val elitist = arrayOf(false, true, false, true, false, true, false, true)
    val multiparent = arrayOf(false, false, true, true, false, false, true, true)
    val hybrid = arrayOf(false, false, false, false, true, true, true, true)
    for ((index, f) in files.withIndex()) {
        val csv = File("/home/gabriel/Files/$f")
        if (csv.exists()) {
            csv.delete()
            csv.createNewFile()
        }
        File(args[0]).walk().forEach { file ->
            if (file.extension == "dat") {
                val matrices = readFile(file)
                val times = mutableListOf<Long>()
                repeat(5) {
                    val algorithm = GeneticAlgorithm(matrices)
                    lateinit var individual: Individual
                    val time = measureTimeMillis {
                        individual = algorithm.loop(elitist[index], multiparent[index], hybrid[index])
                    }
                    times.add(time)
                    csv.appendText("${individual.evaluated};")
                    if (isValidSolution(individual)) {
                        individual.print()
                        println("Time elapsed: ${time}ms")
                    }
                    else
                        println("--- INVALID INDIVIDUAL ---")
                }
                repeat(5) {
                    if (it == 4)
                        csv.appendText("${times[it]};${file.name}\n")
                    else
                        csv.appendText("${times[it]};")
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    results(args)
//    individualVerification(args)
//    elitismSizeVerification(args)
//    elitismCrossoverVerification(args)
//    multiparentSizeVerification(args)
}