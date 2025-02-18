package no.nav.arbeidsgiver.toi.livshendelser

import AdressebeskyttelseLytter
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.person.pdl.leesah.Personhendelse
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val env = System.getenv()

private val secureLog = LoggerFactory.getLogger("secureLog")


fun main() {
    try {
        startApp(
            rapidsConnection(),
            PdlKlient(env["PDL_URL"]!!, AccessTokenClient(env))
        )
    }
    catch (e: Exception) {
        secureLog.error("Uhåndtert exception, stanser applikasjonen", e)
        LoggerFactory.getLogger("main").error("Uhåndtert exception, stanser applikasjonen(se securelog)")
        exitProcess(1)
    }
}

fun startApp(
    rapidsConnection: RapidsConnection,
    pdlKlient: PdlKlient
) {
    val log = LoggerFactory.getLogger("Application.kt")
    try {
        rapidsConnection.also {
            val consumer = { KafkaConsumer<String, Personhendelse>(consumerConfig) }

            PDLLytter(rapidsConnection, consumer, pdlKlient)
            AdressebeskyttelseLytter(pdlKlient, rapidsConnection)
        }.start()
    } catch (e: Exception) {
        log.error("Applikasjonen mottok exception(se secure log)")
        secureLog.error("Applikasjonen mottok exception", e)
        throw e
    }
    finally {
        log.info("Applikasjonen stenges ned")
    }
}

fun rapidsConnection() = RapidApplication.create(System.getenv())

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

val erDev: Boolean = System.getenv()["NAIS_CLUSTER_NAME"]?.equals("dev-gcp") ?: false