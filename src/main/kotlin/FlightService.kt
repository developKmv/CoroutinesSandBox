import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.*

private const val BASE_URL = "http://kotlin-book.bignerdranch.com/2e"
private const val FLIGHT_ENDPOINT = "$BASE_URL/flight"
private const val LOYALTY_ENDPOINT = "$BASE_URL/loyalty"

/*fun main(args: Array<String>) {
    runBlocking {
        println("Started")
        launch {
            val flight = fetchFlight("Max")
            println(flight)
        }
        println("Finished")
    }

}*/

suspend fun fetchFlight(passengerName: String): FlightStatus = coroutineScope<FlightStatus> {
    val client = HttpClient(CIO)

    val flightResp = async { client.get(FLIGHT_ENDPOINT).body<String>() }
    val loyaltyResp = async { client.get(LOYALTY_ENDPOINT).body<String>() }

    FlightStatus.parse(
        passengerName = passengerName,
        flightResponse = flightResp.await(),
        loyaltyResponse = loyaltyResp.await()
    )
}