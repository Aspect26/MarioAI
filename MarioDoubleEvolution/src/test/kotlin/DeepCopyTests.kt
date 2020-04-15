import cz.cuni.mff.aspect.utils.DeepCopy
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Test
import java.io.Serializable


class DeepCopyTests {

    class ShallowClass(val intValue: Int, val stringValue: String) : Serializable

    class DeepClass(val deep: ShallowClass, val intValue: Int) : Serializable

    @Test
    fun `primitive copy`() {
        val intPrimitive = 8

        val copiedPrimitive = DeepCopy.copy(intPrimitive)

        Assert.assertEquals("The copied primitive should have the same value as original", intPrimitive, copiedPrimitive)
    }

    @Test
    fun `shallow copy`() {
        val originalObj = ShallowClass(5, "Winter is coming")

        val copiedObj = DeepCopy.copy(originalObj)

        Assert.assertThat("The copied object should be instance of 'ShallowClass'", copiedObj, instanceOf(ShallowClass::class.java))
        Assert.assertEquals("The 'intValue' should be the same in original and copy", originalObj.intValue, copiedObj.intValue)
        Assert.assertEquals("The 'stringValue' should be the same in original and copy", originalObj.stringValue, copiedObj.stringValue)
    }

    @Test
    fun `deep copy`() {
        val originalObj = DeepClass(ShallowClass(42, "Hear me roar"), 666)

        val copiedObj = DeepCopy.copy(originalObj)

        Assert.assertThat("The copied object should be instance of 'DeepClass'", copiedObj, instanceOf(DeepClass::class.java))
        Assert.assertNotEquals("The copied deep object should be different instance than the original's", originalObj.deep,  copiedObj.deep)
        Assert.assertEquals("The 'intValue' should be the same in original and copy", originalObj.intValue, copiedObj.intValue)
        Assert.assertEquals("The 'intValue' of he deep object should be the same in original and copy", originalObj.deep.intValue, copiedObj.deep.intValue)
        Assert.assertEquals("The 'stringValue' of he deep object should be the same in original and copy", originalObj.deep.stringValue, copiedObj.deep.stringValue)
    }

}