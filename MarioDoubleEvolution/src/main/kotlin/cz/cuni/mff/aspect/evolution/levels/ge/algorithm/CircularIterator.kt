package cz.cuni.mff.aspect.evolution.levels.ge.algorithm


class CircularIterator<T>(private val data: Array<T>): Iterator<T> {

    private var currentIndex = 0
    private var _wrapsCounter = 0

    override fun next(): T {
        if (this.currentIndex >= data.size) {
            this.currentIndex = 0
            this._wrapsCounter++
        }

        return this.data[this.currentIndex++]
    }

    override fun hasNext(): Boolean = true

    val wrapsCount: Int
        get() = this._wrapsCounter
}
