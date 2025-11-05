package com.example.citiway.data.domain

import android.util.Log
import com.example.citiway.data.remote.Step

class MetrorailService(): ITransportService<MetrorailService> {

    private var _fare: Double = 0.0
    private var highestZone: MetrorailZone = MetrorailZone.ZONE_0

    private val stationZoneMap: Map<String, Int> = mapOf(
        // --- ZONE 1 (1 - 15) ---

        // TODO: Double check all stations are here
        "athlone" to 1,
        "bonteheuwel" to 1,
        "century city" to 1,
        "claremont" to 1,
        "crawford" to 1,
        "elsies river" to 1,
        "esplanade" to 1,
        "goodwood" to 1,
        "harfield road" to 1,
        "hazendal" to 1,
        "kenilworth" to 1,
        "koeberg road" to 1,
        "langa" to 1,
        "maitland" to 1,
        "monte vista" to 1,
        "mowbray" to 1,
        "mutual" to 1,
        "ndabeni" to 1,
        "newlands" to 1,
        "observatory" to 1,
        "pinelands" to 1,
        "plumstead" to 1,
        "rondebosch" to 1,
        "rosebank" to 1,
        "salt river" to 1,
        "thornton" to 1,
        "vasco" to 1,
        "wittebome" to 1,
        "woodstock" to 1,
        "wynberg" to 1,
        "ysterplaat" to 1,

        // --- ZONE 2 (16 - 40) ---
        "avondale" to 2,
        "belhar" to 2,
        "bellville" to 2,
        "blackheath" to 2,
        "brackenfell" to 2,
        "chris hani" to 2,
        "de grendal" to 2,
        "dieprivier" to 2,
        "eikenfontein" to 2,
        "eerste river" to 2,
        "faure" to 2,
        "false bay" to 2,
        "fisantekraal" to 2,
        "fish hoek" to 2,
        "glencairn" to 2,
        "heathfield" to 2,
        "kalk bay" to 2,
        "khayelitsha" to 2,
        "kraaifontein" to 2,
        "kuils river" to 2,
        "kuyasa" to 2,
        "lakeside" to 2,
        "lansdowne" to 2,
        "lavistown" to 2,
        "mandalay" to 2,
        "meltonrose" to 2,
        "muizenberg" to 2,
        "nolungile" to 2,
        "nonkqubela" to 2,
        "nyanga" to 2,
        "oosterzee" to 2,
        "ottery" to 2,
        "parow" to 2,
        "pentech" to 2,
        "philippi" to 2,
        "retreat" to 2,
        "saint james" to 2,
        "sarepta" to 2,
        "simonstown" to 2,
        "southfield" to 2,
        "steenberg" to 2,
        "steurhof" to 2,
        "stikland" to 2,
        "stock road" to 2,
        "sunny cove" to 2,
        "tygerberg" to 2,
        "unibell" to 2,
        "wetton" to 2,

        // --- ZONE 3 (41 - 60) ---
        "abbotsdale" to 3,
        "artois" to 3,
        "du toit" to 3,
        "firgrove" to 3,
        "gouda" to 3,
        "hermon" to 3,
        "kalbaskraal" to 3,
        "klapmuts" to 3,
        "klipheuwel" to 3,
        "koelenhof" to 3,
        "malan" to 3,
        "malmesbury" to 3,
        "mellish" to 3,
        "mikpunt" to 3,
        "muldersvlei" to 3,
        "paarl" to 3,
        "soetendal" to 3,
        "somerset west" to 3,
        "stellenbosch" to 3,
        "strand" to 3,
        "tulbachweg" to 3,
        "van der stel" to 3,
        "vlottenburg" to 3,
        "voelvlei" to 3,
        "wintervogel" to 3,
        "wolseley" to 3,

        // --- ZONE 4 (Over 60) ---
        "huguenot" to 4,
        "dal josafat" to 4,
        "mbekweni" to 4,
        "wellington" to 4,
        "worcester" to 4
    )

    override val agencyName: String
        get() = "Prasa"

    override val travelMode: String
        get() = "HEAVY_RAIL"

    override suspend fun adjustFare(step: Step): MetrorailService {
        val stopDetails = step.transitDetails?.stopDetails
        val arrivalZone = stationZoneMap[normalizeStationName(stopDetails?.arrivalStop?.name)] ?: 0
        val departureZone = stationZoneMap[normalizeStationName(stopDetails?.departureStop?.name)] ?: 0

        val highestZoneOfStep = MetrorailZone.fromNumber(maxOf(arrivalZone, departureZone))

        // If the step contains a station in a higher distance band, the fare must increase
        if (highestZoneOfStep.zoneNumber > highestZone.zoneNumber){
            highestZone = highestZoneOfStep
            _fare = highestZoneOfStep.singleRate
        }

        return this
    }

    override fun getFare(): Double {
        return _fare
    }

    override fun resetFare() {
        _fare = 0.0
    }

    private fun normalizeStationName(stationName: String?): String? {
        return stationName
            ?.lowercase()
            ?.replace(" station", "")
            ?.trim()
    }
}

enum class MetrorailZone(val zoneNumber: Int, val singleRate: Double) {
    ZONE_0(0, 0.0), // <- for initialization
    ZONE_1(1, 10.0),
    ZONE_2(2, 12.0),
    ZONE_3(3, 14.0),
    ZONE_4(4, 16.0);

    companion object {
        /** Helper function to safely convert a number (e.g., from a data source) to a zone. */
        fun fromNumber(number: Int): MetrorailZone {
            return entries.firstOrNull { it.zoneNumber == number } ?: ZONE_1
        }
    }
}