package cz.cuni.mff.aspect.storage

import java.io.*


object ObjectStorage {

    fun store(filePath: String, data: Any) {
        val file = File(filePath)
        if (file.parentFile != null) file.parentFile.mkdirs()
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