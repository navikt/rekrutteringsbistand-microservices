package no.nav.arbeidsgiver.toi

import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SynlighetsmotorTest {

    @Test
    fun `legg på synlighet som sann om all data i hendelse tilsier det`() = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon(), cv = cv()),
        enHendelseErPublisertMedSynlighetsverdi(true)
    )

    @Test
    fun `legg på synlighet ved oppfølgingsinformasjon`() = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon()), enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `legg på synlighet ved cv`() = testProgramMedHendelse(
        hendelse(cv = cv()), enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `om Person er død skal synlighet være false`() = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon(erDoed = true)),
        enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `om Person er sperret ansatt skal synlighet være false`() = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon(sperretAnsatt = true)),
        enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `om Person er familie skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person er under oppfølging skal synlighet være false`(): Unit = TODO()

    @Test
    fun `formidlingsgruppe ARBS skal også anses som gyldig formidlingsgruppe`(): Unit = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon(formidlingsgruppe = "ARBS"), cv = cv()),
        enHendelseErPublisertMedSynlighetsverdi(true)
    )

    @Test
    fun `om Person har feil formidlingsgruppe skal synlighet være false`(): Unit = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon(formidlingsgruppe = "IKKEARBSELLERIARBS")),
        enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `om Person ikke er manuell skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person er fritatt fra kandidatsøk skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person er kode 6 skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person er kode 7 skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person ikke har CV skal synlighet være false`() = testProgramMedHendelse(
        hendelse(cv = manglendeCV()),
        enHendelseErPublisertMedSynlighetsverdi(false)
    )

    @Test
    fun `om Person ikke har jobbønsker skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person ikke har sett hjemmel skal synlighet være false`(): Unit = TODO()

    @Test
    fun `om Person må behandle tidligere CV skal synlighet være false`(): Unit = TODO()

    @Test
    fun `ignorer uinteressante hendelser`() = testProgramMedHendelse(
        """
        {
            "@event_name":"uinteressant_hendelse"
        }
        """.trimIndent()
    ) {
        assertThat(size).isEqualTo(0)
    }

    private fun enHendelseErPublisertMedSynlighetsverdi(synlighet: Boolean): TestRapid.RapidInspector.() -> Unit =
        {
            assertThat(size).isEqualTo(1)
            assertThat(field(0, "@event_name").asText()).isEqualTo("hendelse")
            field(0, "synlighet").apply {
                assertThat(get("erSynlig").asBoolean()).apply { if (synlighet) isTrue else isFalse }
                assertThat(get("ferdigBeregnet").asBoolean()).isFalse
            }
        }

    private fun testProgramMedHendelse(
        oppfølgingsinformasjonHendelse: String,
        assertion: TestRapid.RapidInspector.() -> Unit
    ) {
        val rapid = TestRapid()
            .also(::SynlighetsLytter)

        rapid.sendTestMessage(oppfølgingsinformasjonHendelse)

        rapid.inspektør.apply(assertion)
    }

    private fun hendelse(
        oppfølgingsinformasjon: String? = null,
        cv: String? = null
    ) = """
            {
                ${listOfNotNull(""""@event_name":"hendelse"""", oppfølgingsinformasjon, cv).joinToString()}
                
            }
            """.trimIndent()

    private fun manglendeOppfølgingsinformasjon() =
        """
            "oppfølgingsinformasjon": null
        """.trimIndent()

    private fun oppfølgingsinformasjon(
        erDoed: Boolean = false,
        sperretAnsatt: Boolean = false,
        formidlingsgruppe: String = "IARBS"
    ) =
        """
            "oppfølgingsinformasjon": {
                    "fodselsnummer": "12345678912",
                    "formidlingsgruppe": "$formidlingsgruppe",
                    "iservFraDato": null,
                    "fornavn": "TULLETE",
                    "etternavn": "TABBE",
                    "oppfolgingsenhet": "0318",
                    "kvalifiseringsgruppe": "BATT",
                    "rettighetsgruppe": "AAP",
                    "hovedmaal": "BEHOLDEA",
                    "sikkerhetstiltakType": null,
                    "diskresjonskode": null,
                    "harOppfolgingssak": true,
                    "sperretAnsatt": $sperretAnsatt,
                    "erDoed": $erDoed,
                    "doedFraDato": null,
                    "sistEndretDato": "2020-10-30T14:15:38+01:00"
                }
        """.trimIndent()

    private fun cv(
    ) =
        """
            "cv": {
                }
        """.trimIndent()

    private fun manglendeCV() =
        """
            "cv": null
        """.trimIndent()
}