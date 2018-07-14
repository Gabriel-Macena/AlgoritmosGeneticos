const val maxIterations = 5
const val maxIterationsWithoutUpdate = 3
const val limitIterations = 5
const val fixedPerturbationStrength = 4

fun perturbation(individual: Individual, strength: Int): Individual {
    val candidate = Individual(individual.size, individual.generation).apply {
        this.solution = individual.solution.copyOf()
    }
    val r = RND
    val chosenPositions = (0 until individual.size).shuffled(r).take(strength)
    val swapPositions = chosenPositions.shuffled(r)
    // chosen position (individual) ---> swap position (candidate)
    for ((index, pos) in chosenPositions.withIndex()) {
        val swap = swapPositions[index]
        candidate[swap] = individual[pos]
    }
    return candidate
}

fun swapIndividualFacilities(individual: Individual): Individual {
    //2-OPT
    val swapped = Individual(individual.size, individual.generation).apply {
        this.solution = individual.solution.copyOf()
    }
    val r = RND
    val i1 = r.nextInt(swapped.size)
    var i2: Int
    do {
        i2 = r.nextInt(swapped.size)
    } while (i1 == i2)
    val temp = swapped.solution[i1]
    swapped.solution[i1] = swapped.solution[i2]
    swapped.solution[i2] = temp
    return swapped
}

fun localSearch(source: Individual, matrices: QAP): Individual {
    var best = source
    var count = 0
    var iteration = 0
    do {
        iteration++
        val swapped = swapIndividualFacilities(best)
        if (swapped.evaluate(matrices) < best.evaluate(matrices)) {
            best = swapped
            count = 0
        } else {
            ++count
        }
    } while (count < maxIterationsWithoutUpdate && iteration < limitIterations)

    return best
}

fun iteratedLocalSearch(matrices: QAP,
                        initialSolution: Individual): Individual {
    var solution = initialSolution
    repeat(maxIterations) {
        //Strength values: fixed [4], size/4, size/3, size/2, 3*size/4, size [reset]
        val modified = perturbation(solution, fixedPerturbationStrength)
        val candidate = localSearch(modified, matrices)
        solution = if (candidate.evaluate(matrices) < solution.evaluate(matrices)) candidate else solution
    }
    return solution
}