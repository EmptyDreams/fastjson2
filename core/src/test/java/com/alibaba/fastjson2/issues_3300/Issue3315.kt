package com.alibaba.fastjson2.issues_3300

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONReader
import com.alibaba.fastjson2.reader.ObjectReader
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.Type

class Issue3315 {

    @Test
    fun test() {
        JSON.register(TestInterface::class.java, TestReader())
        val wrapper = TestWrapper(
            listOf(
                TestImp("test1"),
                TestImp("test2")
            )
        )
        val json = JSON.toJSONString(wrapper)
        val wrapper2 = JSON.parseObject(json, TestWrapper::class.java)
        wrapper2.list.forEach {
            Assertions.assertEquals(TestImp::class.qualifiedName, it::class.qualifiedName)
        }
        Assertions.assertEquals(wrapper, wrapper2)
    }

}

private data class TestWrapper(
    val list: Collection<TestInterface>
)

private interface TestInterface {

    val name: String

    fun read()

}

private data class TestImp(
    override val name: String
) : TestInterface {

    override fun read() {
        println(name)
    }

}

private class TestReader : ObjectReader<TestImp> {

    override fun readObject(
        jsonReader: JSONReader,
        fieldType: Type?,
        fieldName: Any?,
        features: Long
    ): TestImp? {
        if (jsonReader.nextIfNull()) return null
        jsonReader.nextIfObjectStart()
        jsonReader.readFieldName()
        val name = jsonReader.readString()
        jsonReader.nextIfObjectEnd()
        return TestImp(name)
    }

}
