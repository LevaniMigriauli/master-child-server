import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

const val RANGE = 10_000_000
val receivedNumbers = ConcurrentHashMap.newKeySet<Int>()
val receivedCount = AtomicInteger(0)

fun main() {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }

    routing {
        post("/create-child") {
            call.application.launch {
                simulateChildProcess()
            }
            call.respond(HttpStatusCode.Accepted, "Child process started")
        }

        get("/progress") {
            println("[Progress Endpoint] Received count: ${receivedCount.get()}, Unique numbers: ${receivedNumbers.size}")
            val percentage = (receivedCount.get().toDouble() / RANGE) * 100
            println("[Progress Endpoint] Percentage calculated: $percentage")
            call.respond(mapOf("percentage" to percentage))
        }
    }
}

// Simulates a child process
suspend fun simulateChildProcess() {
    try {
        delay(2000)
        val randomNumber = (0 until RANGE).random()
        println("[Child Process] Generated number: $randomNumber")
        if (receivedNumbers.add(randomNumber)) {
            receivedCount.incrementAndGet()
            println("[Child Process] Number added: $randomNumber, Total count: ${receivedCount.get()}")
        } else {
            println("[Child Process] Number already exists: $randomNumber")
        }
    } catch (e: Exception) {
        println("[Child Process] Error: ${e.message}")
    }
}



//
//import io.ktor.http.*
//import io.ktor.server.engine.*
//import io.ktor.server.netty.*
//import io.ktor.server.application.*
//import io.ktor.server.response.*
//import io.ktor.server.routing.*
//import kotlinx.coroutines.*
//import java.util.concurrent.ConcurrentHashMap
//import java.util.concurrent.atomic.AtomicInteger
//
//const val RANGE = 10_000_000
//val receivedNumbers = ConcurrentHashMap.newKeySet<Int>()
//val receivedCount = AtomicInteger(0)
//
//fun main() {
//    embeddedServer(Netty, port = 8080) {
//        routing {
//            post("/create-child") {
//                call.application.launch {
//                    simulateChildProcess()
//                }
//                call.respond(HttpStatusCode.Accepted, "Child process started")
//            }
//
//            get("/progress") {
//                println("[Progress Endpoint] Received count: ${receivedCount.get()}, Unique numbers: ${receivedNumbers.size}")
//                val percentage = (receivedCount.get().toDouble() / RANGE) * 100
//                println("[Progress Endpoint] Percentage calculated: $percentage")
//                call.respond(mapOf("percentage" to percentage))
//            }
//        }
//    }.start(wait = true)
//}
//
//suspend fun simulateChildProcess() {
//    try {
////        delay(2000) // Simulate long-running operation
//        val randomNumber = (0 until RANGE).random()
//        println("[Child Process] Generated number: $randomNumber")
//        if (receivedNumbers.add(randomNumber)) {
//            receivedCount.incrementAndGet()
//            println("[Child Process] Number added: $randomNumber, Total count: ${receivedCount.get()}")
//        } else {
//            println("[Child Process] Number already exists: $randomNumber")
//        }
//    } catch (e: Exception) {
//        println("[Child Process] Error: ${e.message}")
//    }
//}
