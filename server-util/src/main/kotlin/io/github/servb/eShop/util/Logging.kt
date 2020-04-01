package io.github.servb.eShop.util

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.content.TextContent
import io.ktor.request.ApplicationReceivePipeline
import io.ktor.request.httpMethod
import io.ktor.request.uri
import io.ktor.response.ApplicationSendPipeline
import io.ktor.util.pipeline.PipelinePhase

fun Application.logRequests() {
    val logRequestPhase = PipelinePhase("log request")

    receivePipeline.insertPhaseAfter(ApplicationReceivePipeline.Phases.Transform, logRequestPhase)

    receivePipeline.intercept(logRequestPhase) { subject ->
        log.trace(
            buildString {
                append("Request: ")
                append(call.request.httpMethod.value)
                append(" ")
                append(call.request.uri)
                append(", body: ")
                append(subject.value)
            }
        )
    }
}

fun Application.logResponses() {
    fun createSubjectString(subject: Any) = when (subject) {
        is TextContent -> "TextContent[${subject.contentType}] \"${subject.text}\""  // by default this class trims text
        else -> subject.toString()
    }

    val logResponsePhase = PipelinePhase("log response")

    sendPipeline.insertPhaseAfter(ApplicationSendPipeline.Phases.Render, logResponsePhase)

    sendPipeline.intercept(logResponsePhase) { subject ->
        log.trace(
            buildString {
                append("Request: ")
                append(call.request.httpMethod.value)
                append(" ")
                append(call.request.uri)
                append(" - Response: ")
                append(call.response.status())
                append(", body: ")
                append(createSubjectString(subject))
            }
        )
    }
}
