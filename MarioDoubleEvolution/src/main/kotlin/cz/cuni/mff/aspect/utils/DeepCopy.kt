package cz.cuni.mff.aspect.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

/** Object containing utility function for deep copying Java objects. */
object DeepCopy {

    /** Deeply copies the given object, creating a new object. */
    fun <T : Serializable> copy(obj: T): T {
        val baos = ByteArrayOutputStream()
        val oos = ObjectOutputStream(baos)
        oos.writeObject(obj)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val ois = ObjectInputStream(bais)

        @Suppress("UNCHECKED_CAST")
        return ois.readObject() as T
    }

}