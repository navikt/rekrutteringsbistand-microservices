package no.nav.arbeidsgiver.toi.kandidatfeed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.rapids_rivers.*
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

class UsynligKandidatfeedLytter(
    rapidsConnection: RapidsConnection,
    private val producer: Producer<String, String>
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandKey("aktørId")
                it.demandValue("synlighet.erSynlig", false)
                it.demandValue("synlighet.ferdigBeregnet", true)
                it.interestedIn("cv")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val cvPacket = packet["cv"]["opprettCv"]["cv"] ?: packet["cv"]["endreCv"]["cv"] ?: packet["cv"]["slettCv"]["cv"]
        val synlighetFraArbeidsplassen = cvPacket?.get("synligForVeilederSok")?.asBoolean() ?: false

        if (synlighetFraArbeidsplassen) {
            log.warn("Synlig i følge Arbeidsplassen, men usynlig i følge oss: ${packet["aktørId"].asText()}")
        }

        val aktørId = packet["aktørId"].asText()
        val packetUtenMetadata = packet.fjernMetadataOgKonverter()
        val melding = ProducerRecord(topicName, aktørId, packetUtenMetadata.toString())

        producer.send(melding) { _, exception ->
            if (exception == null) {
                log.info("Sendte kandidat med aktørId $aktørId, synlighet er false")
            } else {
                log.error("Klarte ikke å sende kandidat med aktørId $aktørId", exception)
            }
        }
    }

    private fun JsonMessage.fjernMetadataOgKonverter(): JsonNode {
        val jsonNode = jacksonObjectMapper().readTree(this.toJson()) as ObjectNode
        val metadataFelter = listOf("system_read_count", "system_participating_services", "@event_name")
        return jsonNode.remove(metadataFelter)
    }
}