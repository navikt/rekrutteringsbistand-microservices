package no.nav.arbeidsgiver.toi.kandidatfeed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.rapids_rivers.*
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord

const val topicName = "toi.kandidat-3"

class KandidatfeedLytter(
    rapidsConnection: RapidsConnection,
    private val producer: Producer<String, String>
) :
    River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandKey("aktørId")
                it.demandKey("synlighet")
                it.interestedIn("cv")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val synlighetErFerdigBeregnet = packet["synlighet"]["ferdigBeregnet"].asBoolean()

        if (!synlighetErFerdigBeregnet) {
            log.info("Ignorerer kandidat fordi synlighet ikke er ferdig beregnet" + packet["aktørId"])
            return
        } else {
            val erSynlig = packet["synlighet"]["erSynlig"].asBoolean()

            val cvPacket = (
                packet["cv"]["opprettCv"] ?:
                packet["cv"]["endreCv"] ?:
                packet["cv"]["slettCv"]
            ).get("cv")

            val synlighetFraArbeidsplassen = cvPacket?.get("synligForVeilederSok") ?: false
            if (synlighetFraArbeidsplassen != erSynlig) {
               log.warn("Synlighet for ${packet["aktørId"].asText()} er beregnet annerledes ($erSynlig) enn hos Arbeidsplassen ($synlighetFraArbeidsplassen)")
            }
        }

        val aktørId = packet["aktørId"].asText()
        val packetUtenMetadata = packet.fjernMetadataOgKonverter()
        val melding = ProducerRecord(topicName, aktørId, packetUtenMetadata.toString())

        producer.send(melding) { _, exception ->
            if (exception == null) {
                log.info("Sendte kandidat med aktørId $aktørId")
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
