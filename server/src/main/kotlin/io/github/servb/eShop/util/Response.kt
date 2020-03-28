package io.github.servb.eShop.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer


interface SuccessResult {

    val ok: Boolean

    object FAIL : SuccessResult {

        override val ok = false
    }
}

interface OptionalResult<out ResultType : Any> {

    val data: ResultType?

    @JsonSerialize(using = FAIL.FailSerializer::class)
    object FAIL : OptionalResult<Nothing> {

        override val data = null

        class FailSerializer @JvmOverloads constructor(t: Class<FAIL>? = null) : StdSerializer<FAIL>(t) {
            override fun serialize(value: FAIL, generator: JsonGenerator, provider: SerializerProvider) {
                generator.writeStartObject()
                generator.writeObjectField("data", null)
                generator.writeEndObject()
            }
        }
    }
}
