package cz.cuni.mff.aspect.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

object DeepCopy {

    fun <T : Serializable> copy(obj: T): T {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(obj)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val ois = ObjectInputStream(bais)

        return ois.readObject() as T
    }

}