package cz.cuni.mff.aspect.utils


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
