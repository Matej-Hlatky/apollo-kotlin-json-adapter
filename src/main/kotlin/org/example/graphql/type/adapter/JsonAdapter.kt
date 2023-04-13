package org.example.graphql.type.adapter

import com.apollographql.apollo3.api.Adapter
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.JsonReader
import com.apollographql.apollo3.api.json.JsonWriter
import org.example.graphql.type.GraphQLJson
import org.example.graphql.type.Json
import org.example.readAsUtf8String
import org.example.writeJson

/**
 * [Adapter] for [Json] mapped to [GraphQLJson].
 *
 * Note, [CustomScalarAdapters] is ignored.
 */
class JsonAdapter : Adapter<GraphQLJson> {

    override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): GraphQLJson =
        reader.readAsUtf8String()

    override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: GraphQLJson) =
        writer.writeJson(value)
}
