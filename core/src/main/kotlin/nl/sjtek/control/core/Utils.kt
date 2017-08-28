package nl.sjtek.control.core

import nl.sjtek.control.core.response.Transformer
import spark.Route

fun get(path: String, function: (req: spark.Request, res: spark.Response) -> Any) {
    spark.Spark.get(path, Transformer.contentType, Route { req, res ->
        res.header("Content-Type", Transformer.contentType)
        function(req, res)
    }, Transformer)
}
