package no.nav.arbeidsgiver.toi

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.ResultSet
import javax.sql.DataSource

class TestDatabase {

    val dataSource: DataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1"
            username = "sa"
            password = ""
            validate()
        })

    init {
        Repository(dataSource)
        slettAlt()
    }

    fun slettAlt() {
        dataSource.connection.use { connection ->
            connection.prepareStatement(
                "DELETE FROM sammenstiltkandidat"
            ).execute()
        }
    }

    fun hentAlleKandidater() =
        dataSource.connection.prepareStatement("select * from sammenstiltkandidat")
            .executeQuery()
            .map { databaseRad ->
                databaseRad.getString("kandidat")
            }.map { it.somJsonNode() }
            .map { Kandidat.fraJson(it) }

    fun lagreKandidat(kandidat: Kandidat) = dataSource.connection
        .prepareStatement("insert into sammenstiltkandidat (aktor_id, kandidat) values (?, ?)")
        .apply {
            setString(1, kandidat.aktørId)
            setString(2, kandidat.toJson())
        }.executeUpdate()


    fun <T> ResultSet.map(mapper: (ResultSet) -> T): List<T> {
        return generateSequence {
            if (this.next()) {
                mapper(this)
            } else {
                null
            }
        }.toList()
    }

    fun hentAntallKandidater() =
        dataSource.connection.use {
            val statement = it.prepareStatement("SELECT count(*) FROM sammenstiltkandidat")
            val resultSet = statement.executeQuery()
            if (resultSet.next()) resultSet.getInt(1)
            else 0
        }
}
