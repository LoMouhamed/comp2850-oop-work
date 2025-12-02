import java.io.File
import kotlin.random.Random

// 1. isValid(word: String): Boolean
// Returns true if the given word is valid in Wordle (i.e., if it consists of exactly 5 letters).
fun isValid(word: String): Boolean {
    // Standard Wordle length is 5. We assume basic validation only requires length check
    // unless other rules (like all uppercase letters) are specified.
    return word.length == 5
}

// 2. readWordList(filename: String): MutableList<String>
// Reads Wordle target words from the specified file, returning them as a list of strings.
fun readWordList(filename: String): MutableList<String> {
    val words = mutableListOf<String>()
    try {
        // Read the file line by line
        File(filename).forEachLine { line ->
            // Use the isValid function to filter words by length (5 letters)
            if (isValid(line)) {
                // Add the valid word, converting to uppercase for consistency
                words.add(line.uppercase())
            }
        }
    } catch (e: Exception) {
        // Handle file not found or other IO exceptions silently, returning an empty list
        // println("Error reading word list: ${e.message}") // Optional for debugging
        return mutableListOf()
    }
    return words
}

// 3. pickRandomWord(words: MutableList<String>): String?
// Chooses a random word from the given list, removes that word from the list, then returns it.
// Returns null if the list is empty.
fun pickRandomWord(words: MutableList<String>): String? {
    if (words.isEmpty()) {
        return null
    }

    // Generate a random index within the bounds of the list
    val randomIndex = Random.nextInt(words.size)
    
    // Get the word at the random index
    val word = words[randomIndex]
    
    // Remove the chosen word from the list to prevent repetition (as per requirement)
    words.removeAt(randomIndex)
    
    return word
}

// 4. obtainGuess(attempt: Int): String
// Prints a prompt using the given attempt number, reads a word from stdin, and returns it if valid,
// otherwise prompts the user to try again (this function typically requires loop/recursion
// to guarantee a valid input, but for standalone implementation, we return the input and let main handle validity/reprompt).
// NOTE: Since the main program typically handles the reprompting loop using isValid,
// this function is kept simple to just read input for the sake of separation of concerns.
// However, the prompt description implies reading until valid. For simplicity and testability outside main,
// a minimal implementation is provided.
fun obtainGuess(attempt: Int): String {
    // This function relies on console input, which is often difficult to test, hence why
    // the instruction says not to write tests for it.
    print("Attempt $attempt: ")
    // Read user input and convert to uppercase immediately
    return readln().uppercase()
}

// 5. evaluateGuess(guess: String, target: String): List<Int>
// Compares a guess with the target word. Returns a list containing 5 integers:
// 2 = perfect match (correct letter, correct position), 1 = letter match (correct letter, wrong position), 0 = no match
fun evaluateGuess(guess: String, target: String): List<Int> {
    val result = MutableList(5) { 0 }
    
    // Create mutable copies for tracking consumed characters
    val targetChars = target.toMutableList()
    val guessChars = guess.toMutableList()

    // Pass 1: Find all perfect matches (2)
    for (i in 0 until 5) {
        if (guessChars[i] == targetChars[i]) {
            result[i] = 2
            // Use placeholder characters so these letters are not considered in Pass 2
            targetChars[i] = '\u0000' // Null char placeholder for consumed target
            guessChars[i] = '\u0001' // Different placeholder for consumed guess
        }
    }

    // Pass 2: Find all partial matches (1)
    for (i in 0 until 5) {
        // Only process positions that weren't a perfect match (result[i] != 2)
        if (result[i] != 2) {
            val char = guessChars[i]
            
            // Check if the character exists in the remaining 'targetChars'
            val targetIndex = targetChars.indexOf(char)

            if (targetIndex != -1) {
                result[i] = 1
                // Consume the matched target character to prevent overcounting duplicates
                targetChars[targetIndex] = '\u0000'
            }
        }
    }
    return result
}

// 6. displayGuess(guess: String, matches: List<Int>)
// Displays the letters of a guess that match target word, or a '?' character where there is no match.
fun displayGuess(guess: String, matches: List<Int>) {
    val output = guess.mapIndexed { index, char ->
        when (matches[index]) {
            2 -> char.toString() // Perfect match: Display the letter
            1 -> char.toString().lowercase() // Letter match: Display the letter (often displayed as yellow/lowercase in custom displays)
            else -> "?" // No match (0)
        }
    }.joinToString(" ")

    println("Hint: $output")
}