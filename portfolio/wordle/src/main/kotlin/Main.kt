import java.io.File
import kotlin.random.Random

fun main() {
    // Reads the list of words from the specific file path
    val words = readWordList("data/words.txt")

    if (words.isEmpty()) {
        println("Error: Could not read words from data/words.txt. Please check the file exists.")
        return
    }

    // Picks a random word to be the answer
    val target = pickRandomWord(words) ?: return 

    // Sets the maximum number of attempts allowed
    val maxAttempts = 10
    var hasWon = false

    println("Welcome to Wordle! You have $maxAttempts attempts.")

    // Loops through the allowed number of attempts
    for (attempt in 1..maxAttempts) {
        var guess = ""
        
        // Keeps asking for input until the user provides a valid 5 letter word
        while (true) {
            guess = obtainGuess(attempt)
            if (isValid(guess)) {
                break
            } else {
                println("Invalid guess. Please enter exactly 5 letters.")
            }
        }

        // Checks the guess against the target word
        val results = evaluateGuess(guess, target)
        
        // Shows the user how they did
        displayGuess(guess, results)

        // Checks if the user won by seeing if all results are 1 so a match
        if (results.all { it == 1 }) {
            println("Congratulations! You guessed the word correctly.")
            hasWon = true
            break
        }
    }

    // If the loop finishes and they haven't won, let them know the word
    if (!hasWon) {
        println("Game over! You ran out of guesses.")
        println("The word was: $target")
    }
}

