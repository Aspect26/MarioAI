package cz.cuni.mff.aspect.utils

import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.Serializable

class DeepCopyTests {

    class ShallowClass(val intValue: Int, val stringValue: String) : Serializable

    class DeepClass(val deep: ShallowClass, val intValue: Int) : Serializable

    @Test
    fun `primitive copy`() {
        val intPrimitive = 8

        val copiedPrimitive = DeepCopy.copy(intPrimitive)

        assertEquals(intPrimitive, copiedPrimitive, "The copied primitive should have the same value as original")
    }

    @Test
    fun `shallow copy`() {
        val originalObj = ShallowClass(5, "Winter is coming")

        val copiedObj = DeepCopy.copy(originalObj)

        assertThat("The copied object should be instance of 'ShallowClass'", copiedObj, instanceOf(ShallowClass::class.java))
        assertEquals(originalObj.intValue, copiedObj.intValue, "The 'intValue' should be the same in original and copy")
        assertEquals(originalObj.stringValue, copiedObj.stringValue, "The 'stringValue' should be the same in original and copy")
    }

    @Test
    fun `deep copy`() {
        val originalObj = DeepClass(ShallowClass(42, "Hear me roar"), 666)

        val copiedObj = DeepCopy.copy(originalObj)

        assertThat("The copied object should be instance of 'DeepClass'", copiedObj, instanceOf(DeepClass::class.java))
        assertNotEquals(originalObj.deep,  copiedObj.deep, "The copied deep object should be different instance than the original's")
        assertEquals(originalObj.intValue, copiedObj.intValue, "The 'intValue' should be the same in original and copy")
        assertEquals(originalObj.deep.intValue, copiedObj.deep.intValue, "The 'intValue' of he deep object should be the same in original and copy")
        assertEquals(originalObj.deep.stringValue, copiedObj.deep.stringValue, "The 'stringValue' of he deep object should be the same in original and copy")
    }

}