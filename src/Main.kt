import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

open class Task(
    val id: Int,
    private var title: String,
    private var description: String,
    private var category: String,
    private var status: String = "Pending",
    private val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
) {
    fun update(title: String, description: String, category: String, status: String) {
        this.title = title
        this.description = description
        this.category = category
        this.status = status
    }

    override fun toString(): String {
        return "[$id] $title | $category | $status | Created: $createdAt\nDescription: $description"
    }
}

class TaskManager {
    private val tasks = mutableListOf<Task>()
    private var nextId = 1
    private val categories = arrayOf("Work", "Personal", "Study", "Fitness")

    fun addTask() {
        print("Enter Title:")
        val title = readln()
        print("Enter Description:")
        val description = readln()
        print("Choose Category:")
        categories.forEachIndexed { index, cat -> println("$index. $cat") }
        val catIndex = readlnOrNull()?.toIntOrNull() ?: -1

        if (catIndex !in categories.indices) {
            println("Invalid category.")
            return
        }

        tasks.add(Task(nextId++, title, description, categories[catIndex]))
        println("Task added.")
    }

    fun viewTasks() {
        if (tasks.isEmpty()) {
            println("No tasks available.")
        } else {
            tasks.forEach { println(it) }
        }
    }

    fun updateTask() {
        print("Enter Task ID to update:")
        val id = readln().toIntOrNull() ?: return println("Invalid ID")
        val task = tasks.find { it.id == id } ?: return println("Task not found.")

        print("Enter new Title:")
        val title = readln()
        print("Enter new Description:")
        val description = readln()
        println("Choose new Category:")
        categories.forEachIndexed { index, cat -> println("$index. $cat") }
        val catIndex = readln().toIntOrNull() ?: -1
        if (catIndex !in categories.indices) return println("Invalid category.")

        print("Status (Pending/Completed):")
        val status = readlnOrNull() ?: "Pending"

        task.update(title, description, categories[catIndex], status)
        println("Task updated.")
    }

    fun deleteTask() {
        print("Enter Task ID to delete:")
        val id = readln().toIntOrNull() ?: return println("Invalid ID")
        val removed = tasks.removeIf { it.id == id }
        if (removed) println("Task deleted.") else println("Task not found.")
    }
}

fun startAutoSave() = GlobalScope.launch {
    while (true) {
        delay(30000)
        val current = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        println("Auto-saved at $current")
    }
}

fun main() = runBlocking {
    val taskManager = TaskManager()
    val autoSaveJob = startAutoSave()

    while (true) {
        println("===== Smart Task Planner =====")
        println("1. Add Task")
        println("2. View Tasks")
        println("3. Update Task")
        println("4. Delete Task")
        println("5. Exit")
        println("===============================")

print("Enter Your Choice : ")
        when (readln()) {
            "1" -> taskManager.addTask()
            "2" -> taskManager.viewTasks()
            "3" -> taskManager.updateTask()
            "4" -> taskManager.deleteTask()
            "5" -> {
                autoSaveJob.cancel()
                println("Goodbye!")
                break
            }

            else -> println("Invalid option. Try again.")
        }
    }
}
