package operators

import java.util.*
import Individual
import RND

/* MUTATION */

/*
    The base mutation works by swapping the position
    of two values in the Individual.
 */
fun baseMutation(individual: Individual, generation: Int): Individual {
    val mutated = Individual(individual.size, generation).apply {
        this.solution = individual.solution.copyOf()
    }
    val r = RND
    val i1 = r.nextInt(mutated.size)
    var i2: Int
    do {
        i2 = r.nextInt(mutated.size)
    } while (i1 == i2)
    val temp = mutated.solution[i1]
    mutated.solution[i1] = mutated.solution[i2]
    mutated.solution[i2] = temp
    return mutated
}