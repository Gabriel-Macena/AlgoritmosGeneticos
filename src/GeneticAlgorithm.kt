import operators.*
import java.util.*

//Definição de parâmetros
//const val seed = 10L
val RND = Random()
const val populationSize = 200
const val maxGenerations = 1000
const val mutationRate = 0.05
const val crossoverRate = 0.85

var selectedSize = 40
var selectType = SelectionType.ROULETTE
var tournamentSize = 20

var crossoverType = CrossoverType.FB_SCAN

var eliteSize = 20
var eliteCrossover = true

var numberParents = 3
var numberEliteParents = 1

fun List<Individual>.pickRandom(): Individual {
    val index = RND.nextInt(this.size)
    return this[index]
}

fun List<Individual>.pick(n: Int): List<Individual> {
    if (n <= 0) return listOf()
    if (n >= this.size) return this
    val chosen = mutableListOf<Individual>()
    val indices = (0 until this.size).shuffled(RND).take(n)
    for (i in indices) {
        chosen.add(this[i])
    }
    return chosen
}


class GeneticAlgorithm(val matrices: QAP) {
    private var generation = 0
    val bestSolutions = mutableListOf<Individual>()
    lateinit var population: Population

    private fun crossover (futureParents: List<Individual>, hybrid: Boolean, multiparent: Boolean, elite:
    List<Individual>?):
            List<Individual> {
        val offspring = mutableListOf<Individual>()
        repeat (population.size) {
            val parents = mutableListOf<Individual>()
            if (elite != null && eliteCrossover) {
                if (multiparent) {
                    parents.addAll(elite.pick(numberEliteParents))
                    parents.addAll(futureParents.pick(numberParents - numberEliteParents))
                } else {
                    val p1 = elite.pickRandom()
                    var p2: Individual
                    do {
                        p2 = futureParents.pickRandom()
                    } while (p1 == p2)
                    parents.add(p1)
                    parents.add(p2)
                }
            } else if (multiparent) {
                parents.addAll(futureParents.pick(numberParents))
            } else {
                parents.addAll(futureParents.pick(2))
            }
            if (RND.nextFloat() < crossoverRate) {
                var child = baseCrossover(parents, generation, crossoverType)
                child.evaluate(matrices)
                if (hybrid && parents.all { child.evaluated > it.evaluated })
                    child = iteratedLocalSearch(matrices, child)
                offspring.add(child)
            }
        }
        return offspring
    }

    private fun mutation (individuals: List<Individual>, hybrid: Boolean): List<Individual> {
        val mutants = mutableListOf<Individual>()
        for (individual in individuals) {
            if (RND.nextFloat() <= mutationRate) {
                var mutant = baseMutation(individual, generation)
                mutant.evaluate(matrices)
                //Only perform ILS if the mutant is worse than the individual
                if (hybrid && mutant.evaluated > individual.evaluated)
                    mutant = iteratedLocalSearch(matrices, mutant)
                mutants.add(mutant)
            }
        }
        return mutants
    }

    fun loop (elitist: Boolean, multiparent: Boolean, hybrid: Boolean, addBest: Boolean = false): Individual {
        //Generates the population
        population = generate(populationSize, matrices.size)
        population.evaluateAll(matrices)
        while (generation < maxGenerations) {
            generation++
            val newPopulation = Population()
            var selected: List<Individual>
            var offspring: List<Individual>
            var elite: List<Individual>? = null
            var popNoElite: Population? = null
            if (elitist) {
                elite = pureElitism(population, eliteSize)
                popNoElite = Population()
                popNoElite.individuals = population.individuals.filterNot { ind -> ind in elite }.toMutableList()
                //Selects the fittest individuals to be parents
                selected = baseSelection(popNoElite,
                        selectedSize - eliteSize,
                        tournamentSize,
                        selectType)
                        .toMutableList()
                val futureParents = mutableListOf<Individual>()
                futureParents.addAll(elite)
                futureParents.addAll(selected)
                offspring = crossover(futureParents, hybrid, multiparent, elite)
            }
            else {
                //Selects the fittest individuals to be parents
                selected = baseSelection(population, selectedSize, tournamentSize, selectType)
                //Combines the parents until the amount of individuals
                //equals the original population size
                offspring = crossover(selected, hybrid, multiparent, null)
            }
            //Each offspring can generate a potential mutated copy
            val mutants = mutation(selected, hybrid)
            newPopulation.individuals.addAll(offspring)
            newPopulation.individuals.addAll(mutants)
            if (elitist) {
                newPopulation.individuals.addAll(popNoElite!!.individuals)
                cull(newPopulation, populationSize - eliteSize)
                newPopulation.individuals.addAll(elite!!)
            } else {
                newPopulation.individuals.addAll(population.individuals)
                cull(newPopulation, populationSize)
            }
            population = newPopulation
            if (addBest) bestSolutions.add(population.best())
        }
        return population.best()
    }

}
//
//fun baseGeneticAlgorithm(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population, selectedSize, tournamentSize, selectType)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = selected.pickPair()
//            if (random.nextFloat() < crossoverRate) {
//                val child = baseCrossover(parents.first, parents.second, generation, crossoverType)
//                child.evaluate(matrices)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in selected) {
//            if (random.nextFloat() <= mutationRate) {
//                val mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun elitistGeneticAlgorithm(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the elite
//        val elite = pureElitism(population, eliteSize)
//        population.individuals.removeAll(elite)
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population,
//                selectedSize - eliteSize,
//                tournamentSize,
//                selectType)
//                .toMutableList()
//        val futureParents = mutableListOf<Individual>()
//        futureParents.addAll(elite)
//        futureParents.addAll(selected)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val p1 = elite.pickRandom()
//            var p2: Individual
//            do {
//                p2 = futureParents.pickRandom()
//            } while (p1 == p2)
//            val parents = p1 to p2
//            if (random.nextFloat() < crossoverRate) {
//                val child = baseCrossover(parents.first, parents.second, generation, crossoverType)
//                child.evaluate(matrices)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in futureParents) {
//            if (random.nextFloat() <= mutationRate) {
//                val mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize - eliteSize)
//        //The elite is preserved for at least one generation
//        newPopulation.individuals.addAll(elite)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun multiParentGeneticAlgorithm(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population, selectedSize, tournamentSize, selectType)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = selected.pick(numberParents)
//            if (random.nextFloat() < crossoverRate) {
//                val child = multiSlide(parents, generation)
//                child.evaluate(matrices)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in selected) {
//            if (random.nextFloat() <= mutationRate) {
//                val mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun multiParentElitistGeneticAlgorithm(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the elite
//        val elite = pureElitism(population, eliteSize)
//        population.individuals.removeAll(elite)
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population,
//                selectedSize - eliteSize,
//                tournamentSize,
//                selectType)
//                .toMutableList()
//        val futureParents = mutableListOf<Individual>()
//        futureParents.addAll(elite)
//        futureParents.addAll(selected)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = mutableListOf<Individual>()
//            parents.addAll(elite.pick(numberEliteParents))
//            parents.addAll(futureParents.pick(numberParents - numberEliteParents))
//            if (random.nextFloat() < crossoverRate) {
//                val child = multiSlide(parents, generation)
//                child.evaluate(matrices)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in futureParents) {
//            if (random.nextFloat() <= mutationRate) {
//                val mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize - eliteSize)
//        //The elite is preserved for at least one generation
//        newPopulation.individuals.addAll(elite)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun hybridGeneticAlgorithmILS(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    population.individuals.replaceAll { localSearch(it, matrices) }
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population, selectedSize, tournamentSize, selectType)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = selected.pickPair()
//            if (random.nextFloat() < crossoverRate) {
//                var child = baseCrossover(parents.first, parents.second, generation, crossoverType)
//                child.evaluate(matrices)
//                //Only perform ILS if the child is worse than both parents
//                if (child.evaluated > parents.first.evaluated && child.evaluated > parents.second.evaluated)
//                    child = iteratedLocalSearch(matrices, child)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in selected) {
//            if (random.nextFloat() <= mutationRate) {
//                var mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                //Only perform ILS if the mutant is worse than the individual
//                if (mutant.evaluated > individual.evaluated)
//                    mutant = iteratedLocalSearch(matrices, mutant)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun hybridMultiParentElitistGeneticAlgorithmILS(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    population.individuals.replaceAll { localSearch(it, matrices) }
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the elite
//        val elite = pureElitism(population, eliteSize)
//        population.individuals.removeAll(elite)
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population,
//                selectedSize - eliteSize,
//                tournamentSize,
//                selectType)
//                .toMutableList()
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val futureParents = mutableListOf<Individual>()
//        futureParents.addAll(elite)
//        futureParents.addAll(selected)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = mutableListOf<Individual>()
//            parents.addAll(elite.pick(numberEliteParents))
//            parents.addAll(futureParents.pick(numberParents - numberEliteParents))
//            if (random.nextFloat() < crossoverRate) {
//                var child = multiSlide(parents, generation)
//                child.evaluate(matrices)
//                //Only perform ILS if the child is worse than all parents
//                if (parents.all { child.evaluated > it.evaluated })
//                    child = iteratedLocalSearch(matrices, child)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in selected) {
//            if (random.nextFloat() <= mutationRate) {
//                var mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                //Only perform ILS if the mutant is worse than the individual
//                if (mutant.evaluated > individual.evaluated)
//                    mutant = iteratedLocalSearch(matrices, mutant)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize - eliteSize)
//        newPopulation.individuals.addAll(elite)
//        population = newPopulation
//    }
//    return population.best()
//}
//
//fun hybridMultiParentGeneticAlgorithmILS(matrices: QAP): Individual {
//    val random = RND
//    var generation = 0
//    //Generates the population
//    var population = generate(populationSize, matrices.size)
//    population.evaluateAll(matrices)
//    population.individuals.replaceAll { localSearch(it, matrices) }
//    while (generation < maxGenerations) {
//        generation++
//        val newPopulation = Population()
//        //Selects the fittest individuals to be parents
//        val selected = baseSelection(population, selectedSize, tournamentSize, selectType)
//        //Combines the parents until the amount of individuals
//        //equals the original population size
//        val offspring = mutableListOf<Individual>()
//        while (offspring.size < childrenSize) {
//            val parents = selected.pick(numberParents)
//            if (random.nextFloat() < crossoverRate) {
//                var child = multiSlide(parents, generation)
//                child.evaluate(matrices)
//                //Only perform ILS if the child is worse than all parents
//                if (parents.all { child.evaluated > it.evaluated })
//                    child = iteratedLocalSearch(matrices, child)
//                offspring.add(child)
//            }
//        }
//        //Each selected individual can generate a potential mutated copy
//        val mutants = mutableListOf<Individual>()
//        for (individual in selected) {
//            if (random.nextFloat() <= mutationRate) {
//                var mutant = baseMutation(individual, generation)
//                mutant.evaluate(matrices)
//                //Only perform ILS if the mutant is worse than the individual
//                if (mutant.evaluated > individual.evaluated)
//                    mutant = iteratedLocalSearch(matrices, mutant)
//                mutants.add(mutant)
//            }
//        }
//        newPopulation.individuals.addAll(selected)
//        newPopulation.individuals.addAll(offspring)
//        newPopulation.individuals.addAll(mutants)
//        cull(newPopulation, populationSize)
//        population = newPopulation
//    }
//    return population.best()
//}