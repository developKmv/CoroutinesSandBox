import kotlinx.coroutines.flow.*
import BoardingState.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

val bannedPassengers = setOf("Nogartse")

fun main() {
    runBlocking {
        println("Getting the latest flight info...")
        val flights = fetchFlights(listOf("Barney", "Nogartse", "Botinkin", "Brandon", "Orange"))
        val flightDescriptions = flights.joinToString {
            "${it.passengerName} (${it.flightNumber}) (${it.departureTimeInMinutes})"
        }
        println("Found flights for $flightDescriptions")
        val flightsAtGate = MutableStateFlow(flights.size)
        launch {
            flightsAtGate.takeWhile { it > 0 }
                .collect { flightCount ->
                    println("There are $flightCount flights being tracked")
                }
        }
        launch {
            flights.forEach {
                watchFlight(it)
                flightsAtGate.value -= 1
            }
        }
    }
}

suspend fun watchFlight(initialFlight: FlightStatus) {
    val passengerName = initialFlight.passengerName
    val currentFlight: Flow<FlightStatus> = flow {
        require(passengerName !in bannedPassengers) {
            "Cannot track $passengerName's flight. They are banned from the airport"
        }
        var flight = initialFlight
        //repeat(5) {
        while (flight.departureTimeInMinutes >= 0 &&
            !flight.isFlightCanceled
        ) {
            emit(flight)
            delay(1000)
            flight = flight.copy(
                departureTimeInMinutes = flight.departureTimeInMinutes - 1
            )
        }
    }
    currentFlight.map { flight ->
        when (flight.boardingStatus) {
            FlightCanceled -> "Your flight was canceled"
            BoardingNotStarted -> "Boarding will start soon"
            WaitingToBoard -> "Other passengers are boarding"
            Boarding -> "You can now board the plane"
            BoardingEnded -> "The boarding doors have closed"
        } + " (Flight departs in ${flight.departureTimeInMinutes} minutes)"
    }.onCompletion { println("Finished tracking all flights") }
        .catch { throwable ->
            throwable.printStackTrace()
            //emit("Error")
        }
        .collect {
            /*  val status = when (it.boardingStatus) {
                  BoardingState.FlightCanceled -> "Your flight was canceled"
                  BoardingState.BoardingNotStarted -> "Boarding will start soon"
                  BoardingState.WaitingToBoard -> "Other passengers are boarding"
                  BoardingState.Boarding -> "You can now board the plane"
                  BoardingState.BoardingEnded -> "The boarding doors have closed"
              } + " (Flight departs in ${it.departureTimeInMinutes} minutes)"*/
                status ->
            println("%s : %s".format(passengerName, status))
        }
}

suspend fun fetchFlights_old(
    passengerNames: List<String> = listOf("Madrigal", "Polarcubis")
) = passengerNames.map { fetchFlight(it) }

suspend fun fetchFlights(
    passengerNames: List<String> = listOf("Madrigal", "Polarcubis"),
    numberOfWorker: Int = 2
): List<FlightStatus> =
    coroutineScope {
        val passengerNamesChannel = Channel<String>()
        val fetchedFlightsChannel = Channel<FlightStatus>()

        launch {
            passengerNames.forEach {
                passengerNamesChannel.send(it)
            }
            passengerNamesChannel.close()
        }
        /*    repeat(numberOfWorker) {
                launch {
                    fetchFlightStatuses(passengerNamesChannel, fetchedFlightsChannel)
                    fetchedFlightsChannel.close()
                }
            }*/

        launch {
            (1..numberOfWorker).map {
                launch {
                    fetchFlightStatuses(passengerNamesChannel, fetchedFlightsChannel)
                }
            }.joinAll()
            fetchedFlightsChannel.close()
        }

        fetchedFlightsChannel.toList()
        //emptyList<FlightStatus>()
    }

suspend fun fetchFlightStatuses(
    fetchChannel: ReceiveChannel<String>,
    resultChannel: SendChannel<FlightStatus>
) {
    /*   fetchChannel.consume {
           val flight = fetchFlight(this.receive())
           println("Fetched flight: $flight")
       }*/
    for (passengerName in fetchChannel) {
        val flight = fetchFlight(passengerName)
        println("Fetched flight: $flight")
        resultChannel.send(flight)
    }
    //val passengerName = fetchChannel.receive()

}

enum class LoyaltyTier(
    val tierName: String,
    val boardingWindowStart: Int
) {
    Bronze("Bronze", 25),
    Silver("Silver", 25),
    Gold("Gold", 30),
    Platinum("Platinum", 35),
    Titanium("Titanium", 40),
    Diamond("Diamond", 45),
    DiamondPlus("Diamond+", 50),
    DiamondPlusPlus("Diamond++", 60)
}

enum class BoardingState {
    FlightCanceled,
    BoardingNotStarted,
    WaitingToBoard,
    Boarding,
    BoardingEnded
}

/*
private val _boardingPass: MutableStateFlow<BoardingPass> =
    mutableStateFlow(BoardingPass())
val boardingPass: StateFlow<BoardingPass>
    get() = _boardingPass
fun refreshBoardingPass() {
    _boardingPass.value = BoardingPass()
}*/
