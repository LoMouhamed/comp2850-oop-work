fun greetingFor(target: String): String{
    val greeting = setOf("Hello", "Hi", "G'day").random()
    return "$greeting $target!"
}
