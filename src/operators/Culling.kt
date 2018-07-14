package operators

import Population

/* CULLING */

/*
    The base culling limits the number of individuals
    to the size of the original population by selecting only
    the fittest.
*/
fun cull(population: Population, maxSize: Int) {
    val survivors = population.individuals.sortedBy { it.evaluated }.take(maxSize)
    population.individuals = survivors.toMutableList()
}