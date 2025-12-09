import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveLength
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.io.File

// Define a temporary file name to use during testing
const val TEST_WORD_FILE = "wordle_test_list.txt"

class WordleTest : FunSpec({

    // Creates a file with sample words to test file reading functions
    fun createMockWordFile() {
        val content = "HELLO\nWORLD\nKOTLIN\nTEST\n"
        File(TEST_WORD_FILE).writeText(content)
    }

    // Tests for the isValid function
    context("isValid") {
        test("should return true if the word is exactly 5 letters") {
            isValid("HELLO") shouldBe true
        }

        test("should return false if the word is too short") {
            isValid("BYE") shouldBe false
        }

        test("should return false if the word is too long") {
            isValid("MORNING") shouldBe false
        }
        
        test("should return false if the string is empty") {
            isValid("") shouldBe false
        }
    }

    // Tests for reading the word list from a file
    context("readWordList") {
        // Sets up the sample file before running the tests
        beforeSpec {
            createMockWordFile()
        }

        test("should only read the words that are 5 letters long") {
            val words = readWordList(TEST_WORD_FILE)
            // It should ignore MORNING (7 letters) and BYE (3 letters)
            words shouldContainExactly mutableListOf("HELLO", "WORLD")
        }

        test("should return an empty list if the file does not exist") {
            readWordList("non_existent_file.txt").shouldBeEmpty()
        }

        // Deletes the sample file after tests are finished
        afterSpec {
            File(TEST_WORD_FILE).delete()
        }
    }

    // Tests for picking a random word
    context("pickRandomWord") {
        test("should pick a word and remove it from the list") {
            val list = mutableListOf("APPLE", "BANANA")
            val startSize = list.size
            
            val pickedWord = pickRandomWord(list)
            
            // Verifies the returned word was actually in the list
            (pickedWord == "APPLE" || pickedWord == "BANANA") shouldBe true
            
            // Checks that the list size decreased
            list.size shouldBe startSize - 1
            
            // Ensures the word isn't in the list anymore
            list shouldNotContain pickedWord
        }

        test("should throw an error when picking from an empty list") {
            val emptyList = mutableListOf<String>()
            // Expects the code to throw NoSuchElementException
            shouldThrow<NoSuchElementException> {
                pickRandomWord(emptyList)
            }
        }
    }

    // Tests for evaluating the guess against the target
    // Logic: 1 indicates correct letter at correct position, 0 indicates otherwise
    context("evaluateGuess") {
        val target = "APPLE"

        test("should get all 1s if the guess is perfect") {
            evaluateGuess("APPLE", target) shouldContainExactly listOf(1, 1, 1, 1, 1)
        }

        test("should get all 0s if nothing matches") {
            evaluateGuess("GHOST", target) shouldContainExactly listOf(0, 0, 0, 0, 0)
        }

        test("should return 1 only for the letters in the right spots") {
            // Target is APPLE. Guess is APPLY.
            // The first 4 letters are right, the last one is wrong.
            evaluateGuess("APPLY", target) shouldContainExactly listOf(1, 1, 1, 1, 0)
        }

        test("should return 0 even if the letter is right but in the wrong spot") {
            // Checks exact positions only, so these should all be 0
            evaluateGuess("EALPP", target) shouldContainExactly listOf(0, 0, 0, 0, 0)
        }
        
        test("should work for mixed correct and incorrect letters") {
            // Target: RIVER, Guess: RACER
            // R matches R -> 1
            // A doesn't match I -> 0
            // C doesn't match V -> 0
            // E matches E -> 1
            // R matches R -> 1
            evaluateGuess("RACER", "RIVER") shouldContainExactly listOf(1, 0, 0, 1, 1)
        }
    }
})