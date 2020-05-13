package cz.cuni.mff.aspect.storage

import java.io.File

/** Implementation of a [LocalStorage] for storing a loading text files. */
object LocalTextFileStorage : LocalStorage {

    override fun storeData(filePath: String, data: String) {
        this.createDirectories(filePath)
        File(filePath).writeText(data)
    }

    override fun loadData(filePath: String): String {
        val file = File(filePath)
        if (!file.exists() || file.isDirectory)
            throw IllegalArgumentException("Can't load data from '$filePath' because the file either does not exist or is a directory.")

        return file.readText()
    }

    private fun createDirectories(filePath: String) {
        if (filePath.contains("/")) {
            val directoryPath = filePath.replaceAfterLast("/", "")
            val directory = File(directoryPath)
            if (!directory.exists()) directory.mkdirs()
        }
    }

}