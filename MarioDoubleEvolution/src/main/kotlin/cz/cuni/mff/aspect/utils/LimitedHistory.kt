package cz.cuni.mff.aspect.utils

/**
 * Represents an ordered limited history of entries. It keeps its elements in the order in which they were added and
 * never exceeds its limit. The entries which would exceed the limit are removed.
 *
 * The implementation is not thread safe!
 */
class LimitedHistory<T>(private val limit: Int) {

    private val entries: MutableList<T> = mutableListOf()

    /**
     * Pushes new element to the top of the history, possibly removing the oldest entry if it exceed the limit.
     */
    fun push(element: T) {
        if (this.entries.size == this.limit) {
            this.entries.removeAt(this.limit - 1)
        }

        this.entries.add(0, element)
    }

    /**
     * Retrieves the elements pushed to the history, ordered by the time they were put in. The latest entry will be the
     * first entry.
     */
    fun getAll(): List<T> {
        return this.entries
    }

}