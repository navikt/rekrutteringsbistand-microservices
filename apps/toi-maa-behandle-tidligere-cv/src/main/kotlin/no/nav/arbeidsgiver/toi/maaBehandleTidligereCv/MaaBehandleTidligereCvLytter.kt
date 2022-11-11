package no.nav.arbeidsgiver.toi.maaBehandleTidligereCv

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River

class MaaBehandleTidligereCvLytter(private val rapidsConnection: RapidsConnection) : River.PacketListener {
    init {
        River(rapidsConnection).apply {
            validate {
                it.demandKey("aktorId")
                it.rejectKey("@event_name")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val aktørId = packet["aktorId"].asText()

        val melding = mapOf(
            "aktørId" to aktørId,
            "måBehandleTidligereCv" to packet.fjernMetadataOgKonverter(),
            "@event_name" to "må-behandle-tidligere-cv",
        )

        log.info("Skal publisere måBehandleTidligereCv-melding for aktørId $aktørId")
        val nyPacket = JsonMessage.newMessage(melding).toJson()
        rapidsConnection.publish(aktørId, nyPacket)
    }

    private fun JsonMessage.fjernMetadataOgKonverter(): JsonNode {
        val jsonNode = jacksonObjectMapper().readTree(this.toJson()) as ObjectNode
        val metadataFelter = listOf("system_read_count", "system_participating_services", "@event_name", "@id", "@opprettet")
        jsonNode.remove(metadataFelter)
        return jsonNode
    }
}
