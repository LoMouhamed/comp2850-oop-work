fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        println(greetingFor(args[0]))
    }
    else{
        println(greetingFor("World"))
    }
}