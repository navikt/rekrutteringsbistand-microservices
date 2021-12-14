package no.nav.arbeidsgiver.toi

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.arbeid.cv.events.CvEvent

class FritattKandidatsok(melding: CvEvent) {
    @JsonProperty("@event_name")
    private val event_name = "fritatt-kandidatsøk"

    val fodselsnummer = melding.fodselsnummer
    val fritattKandidatsok = melding.fritattKandidatsok

    fun somString() = objectMapper.writeValueAsString(this)
}

private val objectMapper = ObjectMapper()
    .addMixIn(Object::class.java, AvroMixIn::class.java)

abstract class AvroMixIn {
    @JsonIgnore
    abstract fun getSchema(): org.apache.avro.Schema
    @JsonIgnore
    abstract fun getSpecificData() : org.apache.avro.specific.SpecificData
}
