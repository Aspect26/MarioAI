package cz.cuni.mff.aspect.utils

import org.apache.commons.math3.exception.OutOfRangeException


fun<T : Comparable<T>> min(elements: List<T>): T = when (elements.size) {
    0 -> throw IllegalArgumentException("'elements' cannot be empty")
    1 -> elements[0]
    2 -> if (elements[0] < elements[1]) elements[0] else elements[1]
    else -> {
        val others = elements.subList(2, elements.size).toMutableList()
        others.add(min(listOf(elements[0], elements[1])))
        min(others)
    }
}

fun<T2, T1> discretize(value: T1, availableValues: Array<T2>): T2
        where T2 : Number,
              T1 : Number,
              T1 : Comparable<T2>{
    for (availableValue in availableValues.reversed()) if (value >= availableValue) return availableValue
    throw OutOfRangeException(value, availableValues.first(), availableValues.last())
}
