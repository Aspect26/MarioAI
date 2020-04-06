package cz.cuni.mff.aspect.utils

inline fun <T> Array<T>.sumByFloat(startValue: Float = 0.0f, selector: (T) -> Float): Float {
    var sum = startValue
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Float = 0.0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

inline fun IntArray.sumByFloat(selector: (Int) -> Float): Float {
    var sum: Float = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}