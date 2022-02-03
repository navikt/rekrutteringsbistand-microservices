package no.nav.arbeidsgiver.toi

import no.nav.arbeidsgiver.toi.Testdata.Companion.avsluttetOppfølgingsperiode
import no.nav.arbeidsgiver.toi.Testdata.Companion.cv
import no.nav.arbeidsgiver.toi.Testdata.Companion.fritattKandidatsøk
import no.nav.arbeidsgiver.toi.Testdata.Companion.harCvManglerJobbprofil
import no.nav.arbeidsgiver.toi.Testdata.Companion.harEndreJobbrofil
import no.nav.arbeidsgiver.toi.Testdata.Companion.harOpprettJobbrofil
import no.nav.arbeidsgiver.toi.Testdata.Companion.hendelse
import no.nav.arbeidsgiver.toi.Testdata.Companion.hjemmel
import no.nav.arbeidsgiver.toi.Testdata.Companion.komplettHendelseSomFørerTilSynlighetTrue
import no.nav.arbeidsgiver.toi.Testdata.Companion.manglendeCV
import no.nav.arbeidsgiver.toi.Testdata.Companion.manglendeHjemmel
import no.nav.arbeidsgiver.toi.Testdata.Companion.måBehandleTidligereCv
import no.nav.arbeidsgiver.toi.Testdata.Companion.oppfølgingsinformasjon
import no.nav.arbeidsgiver.toi.Testdata.Companion.oppfølgingsinformasjonHendelseMedParticipatingService
import no.nav.arbeidsgiver.toi.Testdata.Companion.participatingService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.ZonedDateTime

class SynlighetsmotorTest {
    @Test
    fun `legg på synlighet som sann om all data i hendelse tilsier det`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(true, true)
    )

    @Test
    fun `kandidat med kun oppfølgingsinformasjon skal ikke være synlig`() = testProgramMedHendelse(
        hendelse(oppfølgingsinformasjon = oppfølgingsinformasjon()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(
            synlighet = false, ferdigBeregnet = false
        )
    )

    @Test
    fun `kandidat med kun cv skal ikke være synlig`() = testProgramMedHendelse(
        hendelse(cv = cv()), enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, false)
    )

    @Test
    fun `om CV har meldingstype "SLETT" skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = cv(CvMeldingstype.SLETT)),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om CV har meldingstype "ENDRE" skal synlighet være true`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = cv(meldingstype = CvMeldingstype.ENDRE)),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(true, true)
    )

    @Test
    fun `om Person er død skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(erDoed = true)),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person er sperret ansatt skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(sperretAnsatt = true)),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person ikke har aktiv oppfølgingsperiode skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(
            oppfølgingsperiode = avsluttetOppfølgingsperiode()
        ),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person ikke har oppfølgingsinformasjon skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = null),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, false)
    )

    @Test
    fun `formidlingsgruppe ARBS skal også anses som gyldig formidlingsgruppe`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(formidlingsgruppe = "ARBS")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(true, true)
    )

    @Test
    fun `om Person har feil formidlingsgruppe skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(formidlingsgruppe = "IKKEARBSELLERIARBS")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person har kode 6 skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(diskresjonskode = "6")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person har kode 7 skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(oppfølgingsinformasjon = oppfølgingsinformasjon(diskresjonskode = "7")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person er fritatt fra kandidatsøk skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(fritattKandidatsøk = fritattKandidatsøk(true)),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, true)
    )

    @Test
    fun `om Person ikke har CV skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = manglendeCV()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, false)
    )

    @Test
    fun `om Person ikke har jobbprofil skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = harCvManglerJobbprofil()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, true)
    )

    @Test
    fun `om Person har endrejobbprofil skal synlighet kunne være true`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = harEndreJobbrofil()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = true, true)
    )

    @Test
    fun `om Person har opprettjobbprofil skal synlighet kunne være true`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(cv = harOpprettJobbrofil()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = true, ferdigBeregnet = true)
    )

    @Test
    fun `om Person ikke har sett hjemmel skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(hjemmel = manglendeHjemmel()),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, ferdigBeregnet = true)
    )

    @Test
    fun `om Person har hjemmel for feil ressurs skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(hjemmel = hjemmel(ressurs = "CV_GENERELL")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, ferdigBeregnet = true)
    )

    @Test
    fun `om Person har hjemmel som er avsluttet skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(
            hjemmel = hjemmel(
                opprettetDato = ZonedDateTime.now().minusYears(2),
                slettetDato = ZonedDateTime.now().minusYears(1)
            )
        ),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, ferdigBeregnet = true)
    )

    @Test
    fun `om Person må behandle tidligere CV skal synlighet være false`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(
            måBehandleTidligereCv = måBehandleTidligereCv(
                maaBehandleTidligereCv = true
            )
        ),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = false, true)
    )

    @Test
    fun `om Person spesifikt ikke må behandle tidligere CV skal synlighet være true`() = testProgramMedHendelse(
        komplettHendelseSomFørerTilSynlighetTrue(
            måBehandleTidligereCv = måBehandleTidligereCv(
                maaBehandleTidligereCv = false
            )
        ),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = true, true)
    )

    @Test
    fun `om det er ukjent om en Person ikke må behandle tidligere CV skal synlighet være true`() =
        testProgramMedHendelse(
            komplettHendelseSomFørerTilSynlighetTrue(
                måBehandleTidligereCv = null
            ),
            enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(synlighet = true, true)
        )

    @Test
    fun `ignorer uinteressante hendelser`() {
        testProgramMedHendelse(
            oppfølgingsinformasjonHendelse = """{ "@event_name":"uinteressant_hendelse" }""",
            assertion = {
                assertThat(size).isZero()
            }
        )
    }

    @Test
    fun `produserer ny melding dersom sammenstiller er kjørt`() = testProgramMedHendelse(
        oppfølgingsinformasjonHendelseMedParticipatingService(participatingService = participatingService("toi-sammenstille-kandidat")),
        enHendelseErPublisertMedSynlighetsverdiOgFerdigBeregnet(false, false),
    )

    @Test
    fun `Ingen ny melding dersom sammenstiller ikke er kjørt`() = testProgramMedHendelse(
        oppfølgingsinformasjonHendelseMedParticipatingService(
            participatingService = participatingService("toi-cv")
        ),
        enHendelseErIkkePublisert()
    )
}
