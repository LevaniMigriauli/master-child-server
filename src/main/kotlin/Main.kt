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
val receivedNumbers = ConcurrentHashMap.newKeySet<Int>() // Track unique numbers
val receivedCount = AtomicInteger(0) // Count total unique numbers

fun main() {
    try {
        // Check if Docker is available
        val dockerCheck = ProcessBuilder("docker", "--version").start()
        val exitCode = dockerCheck.waitFor()
        if (exitCode == 0) {
            val dockerVersion = dockerCheck.inputStream.bufferedReader().use { it.readText() }
            println("[Docker] Docker Version: $dockerVersion")
        } else {
            println("[Docker] Error: Docker is not installed or accessible.")
        }
    } catch (e: Exception) {
        println("[Docker] Error: ${e.message}")
    }

    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(CORS) {
        allowHost("example.com") // Adjust for production
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
    }

    install(ContentNegotiation) {
        gson { setPrettyPrinting() }
    }

    routing {
        post("/create-child") {
            withContext(Dispatchers.IO) {
                createDockerChildContainer()
            }
            call.respond(HttpStatusCode.Accepted, "Child process started")
        }

        get("/progress") {
            val percentage = (receivedNumbers.size.toDouble() / RANGE) * 100
            println("[Progress] Unique Numbers: ${receivedNumbers.size}, Progress: $percentage%")
            call.respond(mapOf("uniqueNumbers" to receivedNumbers.size, "percentage" to percentage))
        }
    }
}

fun createDockerChildContainer(debug: Boolean = false) {
    println("[Debug] Creating Docker container for child process")
    try {
        val dockerPath = System.getenv("DOCKER_PATH") ?: "docker"
        val processArgs = mutableListOf(dockerPath, "run", "--rm", "child-app")

        if (debug) {
            processArgs.add("--debug")
        }

        val process = ProcessBuilder(processArgs).start()

        process.inputStream.bufferedReader().use { reader ->
            val randomNumber = reader.readLine()?.toIntOrNull() ?: run {
                println("[Child] No valid number received from the child container")
                return
            }
            if (randomNumber in 0 until RANGE && receivedNumbers.add(randomNumber)) {
                receivedCount.incrementAndGet()
                println("[Child] Received unique number: $randomNumber")
            } else {
                println("[Child] Received duplicate or invalid number: $randomNumber")
            }
        }

        println("[Debug] Docker container for child process completed successfully")
    } catch (e: Exception) {
        println("[Error] Failed to start Docker container: ${e.message}")
    }
}
