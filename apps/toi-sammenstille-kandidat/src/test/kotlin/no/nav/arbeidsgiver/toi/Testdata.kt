package no.nav.arbeidsgiver.toi

fun veilederMelding(aktørId: String) = """
        {
            "@event_name": "veileder",
            "aktørId": "$aktørId",
            "veileder": {
                "aktorId":"$aktørId",
                "veilederId":"Z994526",
                "tilordnet":"2020-12-21T10:58:19.023+01:00"
            }
        }
    """.trimIndent()

fun cvMelding(aktørId: String) = """
        {
          "aktørId": "$aktørId",
          "cv": {
            "meldingstype": "SLETT",
            "oppfolgingsinformasjon": null,
            "opprettCv": null,
            "endreCv": null,
            "slettCv": null,
            "opprettJobbprofil": null,
            "endreJobbprofil": null,
            "slettJobbprofil": null,
            "aktoerId": "$aktørId",
            "sistEndret": 1636718935.195
          },
          "@event_name": "cv"
        }
    """.trimIndent()

fun arbeidsmarkedCvMelding(aktørId: String) = """
        {
          "aktørId": "$aktørId",
          "arbeidsmarkedCv": {
            "meldingstype": "SLETT",
            "oppfolgingsinformasjon": null,
            "opprettCv": null,
            "endreCv": null,
            "slettCv": null,
            "opprettJobbprofil": null,
            "endreJobbprofil": null,
            "slettJobbprofil": null,
            "aktoerId": "$aktørId",
            "sistEndret": 1636718935.195
          },
          "@event_name": "arbeidsmarked-cv"
        }
    """.trimIndent()

fun cvMeldingMedSystemParticipatingServices(aktørid: String  = "123") = """
        {
          "aktørId": "$aktørid",
          "cv": {
            "meldingstype": "SLETT",
            "oppfolgingsinformasjon": null,
            "opprettCv": null,
            "endreCv": null,
            "slettCv": null,
            "opprettJobbprofil": null,
            "endreJobbprofil": null,
            "slettJobbprofil": null,
            "aktoerId": "$aktørid",
            "sistEndret": 1636718935.195
          },
          "@event_name": "cv",
          "system_read_count": 1,
          "system_participating_services": [
            {
              "service": "toi-cv",
              "instance": "toi-cv-58849d5f86-7qffs",
              "time": "2021-11-19T10:53:59.163725026"
            }
          ]
        }
    """.trimIndent()

fun oppfølgingsinformasjonMelding(aktørId: String) = """
        {
            "aktørId": "$aktørId",
            "fodselsnummer": "12345678912",
            "@event_name": "oppfølgingsinformasjon",
            "oppfølgingsinformasjon": {
                "fodselsnummer": "12345678912",
                "formidlingsgruppe": "IARBS",
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
                "sperretAnsatt": false,
                "erDoed": false,
                "doedFraDato": null,
                "sistEndretDato": "2020-10-30T14:15:38+01:00"
            }   
        }
    """.trimIndent()

fun siste14avedtakMelding(aktørId: String) = """
            {
              "aktørId": "$aktørId",
              "@event_name": "siste14avedtak",
              "siste14avedtak": {
                "aktorId": "$aktørId",
                "innsatsgruppe": "STANDARD_INNSATS",
                "hovedmal": "SKAFFE_ARBEID",
                "fattetDato": "2021-09-08T09:29:20.398043+02:00",
                "fraArena": false
              }
            }
""".trimIndent()
fun oppfølgingsperiodeMelding(aktørId: String) = """
        {
            "aktørId": "$aktørId",
            "@event_name": "oppfølgingsperiode",
            "oppfølgingsperiode": {
                "uuid": "0b0e2261-343d-488e-a70f-807f4b151a2f",
                "aktorId": "$aktørId",
                "startDato": "2021-12-01T18:18:00.435004+01:00",
                "sluttDato": null 
            }   
        }
    """.trimIndent()

fun fritattKandidatsøkMelding(aktørId: String, fritattKandidatsøk: Boolean) = """
        {
            "aktørId": "$aktørId",
            "fodselsnummer": "123",
            "@event_name": "fritatt-kandidatsøk",
            "fritattKandidatsøk": {
                "fritattKandidatsok": $fritattKandidatsøk
            }
        }
    """.trimIndent()

fun hjemmelMelding(aktørId: String) = """
        {
            "aktørId": "$aktørId",
            "@event_name": "hjemmel",
            "hjemmel": {
                "samtykkeId" : 1,
                "aktoerId" : "AktorId(aktorId=$aktørId)",
                "fnr" : "27075349594",
                "ressurs" : "CV_HJEMMEL",
                "opprettetDato" : "2019-01-09T12:36:06+01:00",
                "slettetDato" : null,
                "versjon" : 1,
                "versjonGjeldendeFra" : null,
                "versjonGjeldendeTil" : "2019-04-08"
            }
        }
    """.trimIndent()

fun måBehandleTidligereCvMelding(aktørId: String) = """
        {
            "aktørId": "$aktørId",
            "@event_name": "må-behandle-tidligere-cv",
            "måBehandleTidligereCv": {
              "aktorId" : "$aktørId",
              "maaBehandleTidligereCv": true
            }
        }
    """.trimIndent()

fun tilretteleggingsbehovEndretMelding(aktørId: String) = """
        {
            "@event_name": "tilretteleggingsbehov",
            "aktørId": "$aktørId",
            "tilretteleggingsbehov": {
                "aktoerId": "$aktørId",
                "harTilretteleggingsbehov": false,
                "behov": ["behov1"]
            }
        }
    """.trimIndent()

val modifiserbareSystemVariabler: MutableMap<String, String>
    get() {
        val unmodifiableEnv = System.getenv()
        val felt = unmodifiableEnv.javaClass.getDeclaredField("m")
        felt.isAccessible = true
        return felt.get(unmodifiableEnv) as MutableMap<String, String>
    }