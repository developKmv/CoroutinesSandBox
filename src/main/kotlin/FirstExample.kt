import kotlinx.coroutines.*
import java.net.URL

private const val BASE_URL = "http://kotlin-book.bignerdranch.com/2e"
private const val FLIGHT_ENDPOINT = "$BASE_URL/flight"

/*fun main(args: Array<String>){
    println("Started")
    GlobalScope.launch {
        val flight = fetchFlight()
        println(flight)
    }
    println("Finished")
}*/

fun main(args: Array<String>) {
    println("Started")
    runBlocking {
        launch {
            val flight = fetchFlight()
            println(flight)
        }
        println("Finished")
    }

}

//fun fetchFlight(): String = URL(FLIGHT_ENDPOINT).readText()
suspend fun fetchFlight(): String = withContext(Dispatchers.IO){ URL(FLIGHT_ENDPOINT).readText()}

