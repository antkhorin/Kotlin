package _2StateMachine.task2

import _2StateMachine.model.*

class StateMachineBuilder {

    val events = mutableListOf<Event>()
    val commands = mutableListOf<Command>()
    val states = mutableListOf<State>()
    val maybeStates = mutableListOf<State>()
    val resetEvents = mutableListOf<Event>()

    fun event(code: String) = if (events.any { it.code == code }) thr() else events.add(Event(code))

    fun command(code: String) = if (commands.any { it.code == code }) thr() else commands.add(Command(code))

    fun state(code: String, init: StateBuilder.() -> Unit) {
        if (states.any { it.code == code }) thr()
        val state = maybeStates.find { it.code == code } ?: State(code)
        states.add(state)
        maybeStates.remove(state)
        val stateBuilder = StateBuilder()
        stateBuilder.init()
        stateBuilder.commands.forEach { c -> state.addCommand(commands.find { it.code == c } ?: thr()) }
        stateBuilder.transitions.forEach { tr ->
            val st = states.find { it.code == tr.value } ?: maybeStates.find { it.code == tr.value } ?: State(tr.value)
            if (!states.any { it.code == st.code } && !maybeStates.any { it.code == st.code }) maybeStates.add(st)
            state.addTransition(events.find { it.code == tr.key } ?: thr(), st)
        }
    }

    fun resetEvents(vararg events: String) =
            events.forEach { c -> if (resetEvents.any { it.code == c }) thr() else resetEvents.add(this.events.find { it.code == c } ?: thr()) }
}

class StateBuilder {
    val commands = mutableListOf<String>()
    val transitions = mutableMapOf<String, String>()

    fun commands(vararg commands: String) = commands.forEach { if (this.commands.contains(it)) thr() else this.commands.add(it) }

    fun transition(event: String, target: String) = if (transitions.containsKey(event)) thr() else transitions.put(event, target)

}

fun stateMachine(start: String, init: StateMachineBuilder.() -> Unit): StateMachine {
    val stateMachineBuilder = StateMachineBuilder()
    stateMachineBuilder.init()
    if (!stateMachineBuilder.maybeStates.isEmpty()) thr()
    val stateMachine = StateMachine(stateMachineBuilder.states.find { it.code == start } ?: thr())
    stateMachineBuilder.resetEvents.forEach { stateMachine.addResetEvent(it) }
    return stateMachine
}

fun thr(): Nothing = throw IllegalStateException()