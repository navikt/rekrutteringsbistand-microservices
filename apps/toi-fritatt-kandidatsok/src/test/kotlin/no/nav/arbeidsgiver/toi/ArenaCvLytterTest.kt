package no.nav.arbeidsgiver.toi

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import no.nav.arbeid.cv.events.CvEvent
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.MockConsumer
import org.apache.kafka.clients.consumer.OffsetResetStrategy
import org.apache.kafka.common.TopicPartition
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

private val topicName = "arena-cv-topic"

class ArenaCvLytterTest {

    @Test
    fun  `Lesing av melding på Arena CV-topic skal føre til at en fritatt kandidatsøk-melding blir publisert på rapid`() {
        val consumer = mockConsumer()
        val arenaCvLytter = ArenaCvLytter(topicName, consumer)
        val rapid = TestRapid()
        val fødselsnummer = "123"
        val fritattKandidatsøk = true

        val melding = melding(fødselsnummer, fritattKandidatsøk)

        mottaArenaCvMelding(consumer, melding)
        arenaCvLytter.onReady(rapid)

        Thread.sleep(300)
        val inspektør = rapid.inspektør
        assertThat(inspektør.size).isEqualTo(1)

        val meldingJson = inspektør.message(0)

        assertThat(meldingJson.fieldNames().asSequence().toList()).containsExactlyInAnyOrder(
            "@event_name",
            "fodselsnummer",
            "fritattKandidatsok",
            "system_read_count"
        )

        assertThat(meldingJson.get("@event_name").asText()).isEqualTo("fritatt-kandidatsøk")
        assertThat(meldingJson.get("fodselsnummer").asText()).isEqualTo(fødselsnummer)
        assertThat(meldingJson.get("fritattKandidatsok").asBoolean()).isEqualTo(fritattKandidatsøk)
    }

    @Test
    fun `Skal ikke publisere fritatt kandidatsøk for personer med kode 6 eller 7`() {
        val consumer = mockConsumer()
        val arenaCvLytter = ArenaCvLytter(topicName, consumer)
        val rapid = TestRapid()
        val fødselsnummer = "123"
        val fritattKandidatsøk = true

        val meldingMedKode6 = melding(fødselsnummer, fritattKandidatsøk, "6")
        val meldingMedKode7 = melding(fødselsnummer, fritattKandidatsøk, "7")

        mottaArenaCvMelding(consumer, meldingMedKode6)
        mottaArenaCvMelding(consumer, meldingMedKode7)
        arenaCvLytter.onReady(rapid)

        Thread.sleep(300)
        val inspektør = rapid.inspektør
        assertThat(inspektør.size).isEqualTo(0)
    }
}

private fun mockConsumer() = MockConsumer<String, CvEvent>(OffsetResetStrategy.EARLIEST).apply {
    schedulePollTask {
        rebalance(listOf(topic))
        updateBeginningOffsets(mapOf(Pair(topic, 0)))
    }
}

private fun mottaArenaCvMelding(consumer: MockConsumer<String, CvEvent>, melding: CvEvent, offset: Long = 0) {
    val record = ConsumerRecord(
        topic.topic(),
        topic.partition(),
        offset,
        melding.fodselsnummer,
        melding,
    )

    consumer.schedulePollTask {
        consumer.addRecord(record)
    }
}

private val topic = TopicPartition(topicName, 0)

private fun melding(fødselsnummer: String, fritattKandidatsøk: Boolean, frkode: String = "1") = CvEvent().apply {
    fodselsnummer = fødselsnummer
    fornavn = ""
    etternavn = ""
    fodselsdato = ""
    fodselsdatoErDnr = false
    formidlingsgruppekode = ""
    epostadresse = ""
    telefon = ""
    mobiltelefon = ""
    statsborgerskap = ""
    arenaPersonId = 1L
    arenaKandidatnr = ""
    beskrivelse = ""
    samtykkeStatus = ""
    samtykkeDato = ""
    adresselinje1 = ""
    adresselinje2 = ""
    adresselinje3 = ""
    postnr = ""
    poststed = ""
    landkode = ""
    kommunenr = 1
    disponererBil = true
    tidsstempel = ""
    orgenhet = ""
    kvalifiseringsgruppekode = ""
    hovedmaalkode = ""
    fritattKandidatsok = fritattKandidatsøk
    fritattAgKandidatsok = true
    sperretAnsattEllerFamilie = false
    frKode = frkode
    erDoed = false
    utdanning = emptyList()
    yrkeserfaring = emptyList()
    kompetanse = emptyList()
    sertifikat = emptyList()
    forerkort = emptyList()
    sprak = emptyList()
    kurs = emptyList()
    verv = emptyList()
    geografiJobbonsker = emptyList()
    yrkeJobbonsker = emptyList()
    heltidDeltidJobbonsker = emptyList()
    ansettelsesforholdJobbonsker = emptyList()
    arbeidstidsordningJobbonsker = emptyList()
}

private val objectMapper = ObjectMapper()
    .registerModule(JavaTimeModule())
