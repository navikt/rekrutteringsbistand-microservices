package no.nav.arbeidsgiver.toi.identmapper

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import javax.sql.DataSource

class Repository(private val dataSource: DataSource) {
    init {
        kjørFlywayMigreringer()
    }

    private val tabell = "identmapping"
    private val aktørIdKolonne = "aktor_id"
    private val fødselsnummerKolonne = "fnr"
    private val cachetTidspunktKolonne = "cachet_tidspunkt"

    fun lagreAktørId(aktørId: String, fødselsnummer: String) {
        val identMappingerBasertPåFødselsnummer = hentIdentMappinger(fødselsnummer)

        val harSammeMapping = identMappingerBasertPåFødselsnummer.any { it.aktørId == aktørId }

        dataSource.connection.use {
            if (harSammeMapping) {
                it.prepareStatement("UPDATE $tabell SET $cachetTidspunktKolonne = ? WHERE $aktørIdKolonne = ? $fødselsnummerKolonne = ?").apply {
                    setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()))
                    setString(2, aktørId)
                    setString(3, fødselsnummer)
                }
            } else {
                it.prepareStatement("INSERT INTO $tabell($aktørIdKolonne, $fødselsnummerKolonne, $cachetTidspunktKolonne) VALUES (?, ?, ?)")
                    .apply {
                        setString(1, aktørId)
                        setString(2, fødselsnummer)
                        setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()))
                    }
            }.executeUpdate()
        }
    }

    fun hentIdentMappinger(fødselsnummer: String): List<IdentMapping> {
        dataSource.connection.use {
            val resultSet = it.prepareStatement("SELECT * FROM $tabell WHERE $fødselsnummerKolonne = ?").apply {
                setString(1, fødselsnummer)
            }.executeQuery()

            return generateSequence {
                if (resultSet.next()) tilIdentMapping(resultSet) else null
            }.toList()
        }
    }

    private fun tilIdentMapping(resultSet: ResultSet) = IdentMapping(
        aktørId = resultSet.getString(aktørIdKolonne),
        fødselsnummer = resultSet.getString(fødselsnummerKolonne),
        cachetTidspunkt = resultSet.getTimestamp(cachetTidspunktKolonne).toLocalDateTime()
    )

    private fun kjørFlywayMigreringer() {
        Flyway.configure()
            .dataSource(dataSource)
            .load()
            .migrate()
    }
}

data class IdentMapping(
    val aktørId: String,
    val fødselsnummer: String,
    val cachetTidspunkt: LocalDateTime
)

fun hentDatabasekonfigurasjon(env: Map<String, String>): HikariDataSource {
    val host = env.variable("NAIS_DATABASE_TOI_IDENTMAPPER_DB_HOST")
    val port = env.variable("NAIS_DATABASE_TOI_IDENTMAPPER_DB_PORT")
    val database = env.variable("NAIS_DATABASE_TOI_IDENTMAPPER_DB_DATABASE")
    val user = env.variable("NAIS_DATABASE_TOI_IDENTMAPPER_DB_USERNAME")
    val pw = env.variable("NAIS_DATABASE_TOI_IDENTMAPPER_DB_PASSWORD")

    return HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://$host:$port/$database"
        minimumIdle = 1
        maximumPoolSize = 2
        driverClassName = "org.postgresql.Driver"
        initializationFailTimeout = 5000
        username = user
        password = pw
        validate()
    }.let(::HikariDataSource)
}

private fun Map<String, String>.variable(felt: String) = this[felt] ?: throw Exception("$felt er ikke angitt")
