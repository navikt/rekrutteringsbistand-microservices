package no.nav.arbeidsgiver.toi.arbeidsgiver.notifikasjon

import no.nav.arbeidsgiver.toi.presentertekandidater.notifikasjoner.NotifikasjonKlient
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import java.util.*

class NotifikasjonLytter(rapidsConnection: RapidsConnection, private val notifikasjonKlient: NotifikasjonKlient) : River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "notifikasjon.cv-delt")
                it.requireKey(
                    "notifikasjonsId",
                    "virksomhetsnummer",
                    "stillingsId",
                    "utførtAvVeilederFornavn",
                    "utførtAvVeilederEtternavn",
                    "epostAdresseArbeidsgiver"
                )
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        val notifikasjonsId = packet["notifikasjonsId"].asText()
        val stillingsId = UUID.fromString(packet["stillingsId"].asText())
        val virksomhetsnummer = packet["virksomhetsnummer"].asText()
        val utførtAvVeilederFornavn = packet["utførtAvVeilederFornavn"].asText()
        val utførtAvVeilederEtternavn = packet["utførtAvVeilederEtternavn"].asText()
        val epostAdresseArbeidsgiver = packet["epostAdresseArbeidsgiver"].asText()

        notifikasjonKlient.sendNotifikasjon(
            notifikasjonsId = notifikasjonsId,
            mottakerEpost = epostAdresseArbeidsgiver,
            stillingsId = stillingsId,
            virksomhetsnummer = virksomhetsnummer,
            avsender = "$utførtAvVeilederFornavn $utførtAvVeilederEtternavn"
        )
    }
}
