package no.nav.arbeidsgiver.toi.kandidatfeed

import no.nav.helse.rapids_rivers.RapidApplication
import org.apache.kafka.clients.producer.KafkaProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() = RapidApplication.create(System.getenv()).also { rapidsConnection ->
    KandidatfeedLytter(rapidsConnection, KafkaProducer(producerConfig))
}.start()

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)
