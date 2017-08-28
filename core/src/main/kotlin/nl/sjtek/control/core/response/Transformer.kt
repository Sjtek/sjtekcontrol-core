package nl.sjtek.control.core.response

import spark.ResponseTransformer

object Transformer : ResponseTransformer {
    const val contentType = "application/json"
    override fun render(model: Any?): String = ResponseCache.json
}