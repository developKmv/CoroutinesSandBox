import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        //flowExample()
        //sharedFlowExample()
        mutableStateFlowExample()
    }
}

suspend fun flowExample() = coroutineScope {
    val numbersFlow = flow {
        (1..5).forEach {
            delay(1000)
            emit(it)
        }
    }

    launch {
        numbersFlow.collect { println("Collector 1: Got $it") }
    }
    launch {
        delay(2200)
        numbersFlow.collect { println("Collector 2: Got $it") }
    }
}

suspend fun sharedFlowExample() = coroutineScope {
    val numbersFlow = MutableSharedFlow<Int>()
    launch {
        numbersFlow.collect { println("Collector 1: Got $it") }
    }
    launch {
        delay(2200)
        numbersFlow.collect { println("Collector 2: Got $it") }
    }

    (1..5).forEach {
        delay(1000)
        numbersFlow.emit(it)
    }
}

suspend fun mutableStateFlowExample() = coroutineScope {
    val msf = MutableStateFlow<EmitObj>(EmitObj(0))

    launch {
        msf.collect { println("Collector 1: Got $it") }
    }
    launch {
        delay(2200)
        msf.collect { println("Collector 2: Got $it") }
    }

    (1..25).forEach {
        //msf.value += 1
        msf.emit(EmitObj(it))
        delay(1)
    }
}

data class EmitObj(val i: Int)