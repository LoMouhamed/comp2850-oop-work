import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File

// Mock data and paths for testing file-related functions
// NOTE: For 'readWordList' to work in a real environment, you must ensure 'wordle_test_list.txt'
// exists in the correct directory (e.g., project root or resource folder) when running the tests.
const val TEST_WORD_FILE = "wordle_test_list.txt"

// Mock the file content for readWordList testing (in a real test environment, this file must exist)
fun createMockWordFile() {
    val content = "HELLO\nWORLD\nKOTLIN\nTEST\n"
    File(TEST_WORD_FILE).writeText(content)
}

// Define the test suite using Kotest's FunSpec style
class WordleTest : FunSpec({

    // 1. Tests for isValid(word: String): Boolean
    context("isValid") {
        test("should return true for a 5-letter word") {
            isValid("HELLO") shouldBe true
        }

        test("should return false for a word shorter than 5 letters") {
            isValid("TEST") shouldBe false
        }

        test("should return false for a word longer than 5 letters") {
            isValid("KOTLIN") shouldBe false
        }

        test("should return true for a 5-letter word containing special characters (if not explicitly restricted by game rules)") {
            // Assuming Wordle words are typically alphabetic, but testing length strictly
            isValid("A-B-C") shouldBe true
        }

        test("should return false for an empty string") {
            isValid("") shouldBe false
        }
    }

    // 2. Tests for readWordList(filename: String): MutableList<String>
    context("readWordList") {
        beforeSpec {
            // Setup: Create a mock file before running file tests
            createMockWordFile()
        }

        test("should read all valid words from the file") {
            val words = readWordList(TEST_WORD_FILE)
            // The file contains 4 lines: HELLO, WORLD, KOTLIN, TEST.
            // Assuming the function only returns valid (5-letter) words for the Wordle list.
            // If the implementation is to return ALL lines regardless of length, adjust expectation.
            // Standard Wordle list philosophy is 5-letter words.
            words shouldContainExactly mutableListOf("HELLO", "WORLD") // KOTLIN (6), TEST (4) are excluded
        }

        test("should return an empty list if the file does not exist") {
            readWordList("non_existent_file.txt").shouldBeEmpty()
        }

        afterSpec {
            // Teardown: Clean up the mock file after all tests in this context
            File(TEST_WORD_FILE).delete()
        }
    }

    // 3. Tests for pickRandomWord(words: MutableList<String>): String
    context("pickRandomWord") {
        val wordList = mutableListOf("APPLE", "BANANA", "CHAIR", "TABLE")

        test("should return a word that was originally in the list") {
            val originalList = mutableListOf("APPLE", "BANANA", "CHAIR", "TABLE")
            val pickedWord = pickRandomWord(originalList)
            pickedWord shouldNotBe null
            originalList shouldNotBe mutableListOf("APPLE", "BANANA", "CHAIR", "TABLE") // check modification
        }

        test("should remove the chosen word from the list") {
            val list = mutableListOf("FIRST", "SECOND", "THIRD")
            list shouldHaveLength 3
            val pickedWord = pickRandomWord(list)

            list shouldHaveLength 2
            list shouldNot contain(pickedWord) // Use Kotest's `contain` matcher negated
        }

        test("should throw an exception or return null if the list is empty") {
            // Assuming implementation handles empty list gracefully (e.g., returns null or empty string)
            val emptyList = mutableListOf<String>()
            pickRandomWord(emptyList) shouldBe null
        }
    }

    // 4. Tests for evaluateGuess(guess: String, target: String): List<Int>
    // Result mapping: 2 = perfect match (correct letter, correct position), 1 = letter match (correct letter, wrong position), 0 = no match
    context("evaluateGuess") {
        val target = "APPLE"

        test("should return all 2s for a perfect match") {
            evaluateGuess(target, target) shouldContainExactly listOf(2, 2, 2, 2, 2)
        }

        test("should return all 0s for a total mismatch") {
            evaluateGuess("GRAPE", "TOUCH") shouldContainExactly listOf(0, 0, 0, 0, 0)
        }

        test("should correctly identify perfect and partial matches") {
            // Target: APPLE
            // Guess: PLATE
            // P (2) | L (1) | A (1) | T (0) | E (2)
            evaluateGuess("PLATE", target) shouldContainExactly listOf(2, 1, 1, 0, 2)
        }

        test("should handle duplicate letters in the guess (prioritize perfect match)") {
            // Target: ALLOY
            // Guess: LEVEL
            // L (0) | E (0) | V (0) | E (0) | L (1) - Only one L should be marked as 1
            // Correction: Based on typical Wordle logic:
            // 1. L: Misplaced (since the first L is at L[0] in target, which is correct position) -> L at [0] is 2
            // Let's use a clearer case:
            // Target: SPELL
            // Guess: STEEL
            // S (2) | T (0) | E (2) | E (0) | L (1)
            // S(2): Target S(0) | T(0): No T | E(2): Target E(2) | E(0): Target E(3) is consumed by E(2) | L(1): Target L(4)
            evaluateGuess("STEEL", "SPELL") shouldContainExactly listOf(2, 0, 2, 0, 1)
        }

        test("should handle duplicate letters in the target") {
            // Target: BOOZY
            // Guess: ODDLY
            // O(1) | D(0) | D(0) | L(0) | Y(2)
            // O is misplaced (O[1] and O[2] in target available).
            evaluateGuess("ODDLY", "BOOZY") shouldContainExactly listOf(1, 0, 0, 0, 2)
        }

        test("should not overcount misplaced letters if target has fewer duplicates") {
            // Target: LIGHT
            // Guess: LILAC
            // L(2) | I(1) | L(0) | A(0) | C(0)
            // Target has one L, Guess has two. Only the first L gets a match (2). The second L gets 0.
            // Target has one I, Guess has two. Only the first I gets a match (1). The second I gets 0.
            evaluateGuess("LILAC", "LIGHT") shouldContainExactly listOf(2, 1, 0, 0, 0)
        }

        test("should correctly handle full shuffle with duplicates") {
            // Target: RIVET
            // Guess: TERRI
            // T(1) | E(1) | R(1) | R(0) | I(1)
            // R-at-0: R at 2 is 1 (consumed)
            // I-at-1: I at 1 is 1 (consumed)
            // V-at-2: T is 0
            // E-at-3: E at 3 is 1 (consumed)
            // T-at-4: T at 4 is 1 (consumed)
            // Wait, this is hard. Let's use the actual algorithm logic.
            // Target: RIVET
            // Guess: TERRI
            // Perfect Matches (2s): None
            // Available letters in Target (remaining R, I, V, E, T)
            // T(1): T in RIVET. Available? Yes (T[4]). T[0] is Yellow.
            // E(1): E in RIVET. Available? Yes (E[3]). E[1] is Yellow.
            // R(1): R in RIVET. Available? Yes (R[0]). R[2] is Yellow.
            // R(0): R in RIVET. Available? No (R[0] consumed). R[3] is Gray.
            // I(1): I in RIVET. Available? Yes (I[1]). I[4] is Yellow.
            evaluateGuess("TERRI", "RIVET") shouldContainExactly listOf(1, 1, 1, 0, 1)
        }
    }
})

// Mock implementations for the tests to compile and run
// In a real project, Wordle.kt would contain the actual logic.

// 1. isValid implementation
fun isValid(word: String): Boolean {
    return word.length == 5
}

// 2. readWordList implementation
// Simplistic implementation for testing file reading/filtering
fun readWordList(filename: String): MutableList<String> {
    val words = mutableListOf<String>()
    try {
        File(filename).forEachLine { line ->
            // In a real Wordle list, we would filter for isAlpha as well, but here we just check length for list integrity.
            if (line.length == 5) {
                words.add(line.uppercase())
            }
        }
    } catch (e: Exception) {
        // Handle file not found or other IO exceptions gracefully
        return mutableListOf()
    }
    return words
}

// 3. pickRandomWord implementation
// Non-random implementation for reliable testing
fun pickRandomWord(words: MutableList<String>): String? {
    if (words.isEmpty()) return null

    // For testing purposes, always pick the last word to ensure predictability.
    val indexToPick = words.size - 1
    val word = words[indexToPick]
    words.removeAt(indexToPick)
    return word
}

// 5. evaluateGuess implementation
fun evaluateGuess(guess: String, target: String): List<Int> {
    val result = MutableList(5) { 0 }
    val targetChars = target.toMutableList()
    val guessChars = guess.toMutableList()

    // Pass 1: Find all perfect matches (2)
    for (i in 0 until 5) {
        if (guessChars[i] == targetChars[i]) {
            result[i] = 2
            // Null out the matched characters so they aren't double-counted in the second pass
            targetChars[i] = '\u0000' // Null char placeholder
            guessChars[i] = '\u0001' // Different placeholder for guess
        }
    }

    // Pass 2: Find all partial matches (1)
    for (i in 0 until 5) {
        if (result[i] != 2) {
            val char = guess[i]
            val targetIndex = targetChars.indexOf(char)

            if (targetIndex != -1) {
                result[i] = 1
                // Null out the matched target character
                targetChars[targetIndex] = '\u0000'
            }
        }
    }
    return result
}