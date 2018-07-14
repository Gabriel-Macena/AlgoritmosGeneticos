package operators

import Individual
import Population
import RND

/* SELECTION */

enum class SelectionType {
    ROULETTE, TOURNAMENT
}

inline fun <T> Iterable<T>.firstIndexed(predicate: (index: Int, T) -> Boolean): T {
    var index = 0
    for (element in this) if (predicate(index++, element)) return element
    throw NoSuchElementException("Collection contains no element matching the predicate.")
}

/*
    Roulette Wheel method: spin a wheel and chooses the individual belonging to the section
*/

fun rouletteWheel(population: List<Individual>, selectedSize: Int): List<Individual> {
    val selected = mutableListOf<Individual>()
    val popCopy = mutableListOf<Individual>()
    popCopy.addAll(population)
    repeat(selectedSize) {
        val totalFitness =
                popCopy.fold(0.0) { acc: Double, individual: Individual -> acc + individual.fitness }
        var accumulatedProbability = 0.0
        val probabilityWheel = mutableMapOf<Int, Double>()
        popCopy.forEachIndexed { i, individual ->
            accumulatedProbability += individual.fitness / totalFitness
            if (i == (popCopy.size - 1)) probabilityWheel[i] = 1.0
            else probabilityWheel[i] = accumulatedProbability
        }
        val prob = RND.nextDouble()
        val chosen = popCopy.firstIndexed { i, _ -> prob < probabilityWheel[i]!! }
        selected.add(chosen)
        popCopy.remove(chosen)
    }
    return selected
}

/*
    Tournament Selection: select a random number of individuals, compare them, and choose the fittest. May
    or may not repeat an individual.
*/

fun tournamentSelection(population: List<Individual>, selectedSize: Int, tournamentSize: Int):
        List<Individual> {
    val selected = mutableListOf<Individual>()
    val popCopy = mutableListOf<Individual>()
    popCopy.addAll(population)
    while(selected.size < selectedSize) {
        val participants = popCopy.shuffled(RND).take(tournamentSize).toMutableList()
        participants.sortBy { it.evaluated }
        val winner = participants.removeAt(0)
        selected.add(winner)
        popCopy.remove(winner)
    }
    return selected
}

fun pureElitism(population: Population, selectedSize: Int): List<Individual> {
    return population.individuals.sortedBy { it.evaluated }.take(selectedSize)
}

fun baseSelection(population: Population, selectedSize: Int, tournamentSize: Int, type: SelectionType): List<Individual> {
    return when (type) {
        SelectionType.ROULETTE -> rouletteWheel(population.individuals, selectedSize)
        SelectionType.TOURNAMENT -> tournamentSelection(population.individuals, selectedSize, tournamentSize)
    }
}