package cz.cuni.mff.aspect.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object DeepCopy {

    fun <T> copy(obj: T): T {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(obj)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val ois = ObjectInputStream(bais)

        return ois.readObject() as T
    }

}