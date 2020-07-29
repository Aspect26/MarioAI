package cz.cuni.mff.aspect.utils

/**
 * Sums the array to a float value using given selector.
 *
 * @param startValue starting value of the sum.
 * @param selector converts the array item to a float which is used in the resulting sum.
 */
inline fun <T> Array<T>.sumByFloat(startValue: Float = 0.0f, selector: (T) -> Float): Float {
    var sum = startValue
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

/** Sums the iterable to a float value. */
fun Iterable<Float>.sumByFloat(): Float {
    var sum = 0f
    for (element in this) {
        sum += element
    }
    return sum
}

/**
 * Sums the iterable to a float value using given selector.
 *
 * @param selector converts the array item to a float which is used in the resulting sum.
 */
inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

/**
 * Sums the array to a float value using given selector.
 *
 * @param selector converts the array item to a float which is used in the resulting sum.
 */
inline fun IntArray.sumByFloat(selector: (Int) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

/** Flattens the array. */
fun Array<out IntArray>.flatten(): List<Int> {
    val result = ArrayList<Int>(sumBy { it.size })
    for (innerArray in this) {
        for (element in innerArray) {
            result.add(element)
        }
    }
    return result
}

/** Returns last index of element, which fulfills the given predicate or -1 if no such element is found. */
fun IntArray.lastIndexOf(predicate: (Int) -> Boolean): Int {
    for (index in indices.reversed()) {
        if (predicate(this[index])) {
            return index
        }
    }

    return -1
}