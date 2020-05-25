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

    fun exists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    fun<T: Serializable> load(filePath: String): T {
        val file = File(filePath)
        val fis = FileInputStream(file)
        val ois = ObjectInputStream(fis)

        @Suppress("UNCHECKED_CAST")
        return ois.readObject() as T
    }

}
