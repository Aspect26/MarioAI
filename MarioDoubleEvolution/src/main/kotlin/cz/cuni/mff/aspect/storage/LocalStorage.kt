package cz.cuni.mff.aspect.storage

/** Represents an interface to a local storage. */
interface LocalStorage {

    /** Stores [data] to a local file at [filePath]. */
    fun storeData(filePath: String, data: String)

    /** Loads data from a local file at [filePath]. */
    fun loadData(filePath: String): String

}