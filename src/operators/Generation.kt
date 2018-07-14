package operators

import Population
import Individual
import RND

/* GENERATION */
fun generate(popSize: Int, indSize: Int): Population {
    val population = Population()
    repeat(popSize) {
        val ind = Individual(indSize, 0).apply {
            solution = (0..(indSize - 1)).shuffled(RND).toIntArray()
        }
        population.individuals.add(ind)
    }
    return population
}
