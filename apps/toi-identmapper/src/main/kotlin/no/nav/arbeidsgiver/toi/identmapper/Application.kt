package no.nav.arbeidsgiver.toi.identmapper

import no.nav.helse.rapids_rivers.RapidApplication
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main() {
    val env = System.getenv()

    RapidApplication.create(env).also { rapidsConnection ->
        val accessTokenClient = AccessTokenClient(env)

        val pdlUrl = env["PDL_URL"]!!
        val cluster = env["NAIS_CLUSTER_NAME"]!!
        val pdlKlient = PdlKlient(pdlUrl, accessTokenClient)
        val repository = Repository(hentDatabasekonfigurasjon(env))
        val aktørIdCache = AktorIdCache(repository, cluster, pdlKlient::hentAktørId)

        listOf("fnr", "fodselsnr", "fodselsnummer").forEach { fnrKey ->
            Lytter(fnrKey, rapidsConnection, cluster, aktørIdCache::hentAktørId)
        }

        PdlLytter(rapidsConnection, repository::lagreAktørId)
    }.start()
}

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)
