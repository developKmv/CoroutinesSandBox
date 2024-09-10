import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        observeTemperature(flowOf(1,2,3,4))
        }
    }

suspend fun observeTemperature(
    temperatureInCelsius: Flow<Int>
) {
    temperatureInCelsius.convertToKelvin()
        .collect { temperatureInKelvin ->
            println("The current temperature is $temperatureInKelvin")
        }
}

fun Flow<Int>.convertToKelvin(): Flow<Double> =
    transform<Int, Double> { temperatureInCelsius ->
        emit(temperatureInCelsius + 273.15)
    }