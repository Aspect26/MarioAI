package cz.cuni.mff.aspect.storage

import java.io.*

/**
 * Generic storage for all kinds of Serializable Java objects.
 *
 * For storing and loading the objects the storage uses Java's [ObjectInputStream] and [ObjectOutputStream].
 */
object ObjectStorage {

    fun store(filePath: String, data: Serializable) {
        val file = File(filePath)
        file.parentFile?.mkdirs()
        file.createNewFile()
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)

        oos.use { it.writeObject(data)}
        oos.flush()
    }

    fun load(filePath: String): Any {
        val file = File(filePath)
        val fis = FileInputStream(file)
        val ois = ObjectInputStream(fis)

        var result: Any = Object()
        ois.use {
            result = it.readObject()
        }

        return result
    }

}