import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import no.nav.arbeidsgiver.toi.identmapper.IdentMapping
import java.sql.Timestamp
import javax.sql.DataSource

class TestDatabase {
    val dataSource: DataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
            validate()
        })

    fun slettAlt() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                "DELETE FROM identmapping"
            ).execute()
        }
    }

    fun lagreIdentMapping(identMapping: IdentMapping) {
        dataSource.connection.use {
            it.prepareStatement("INSERT INTO identmapping(aktor_id, fnr, cachet_tidspunkt) VALUES (?, ?, ?)")
                .apply {
                    setString(1, identMapping.aktørId)
                    setString(2, identMapping.fødselsnummer)
                    setTimestamp(3, Timestamp.valueOf(identMapping.cachetTidspunkt))
                }.executeUpdate()
        }
    }
}
