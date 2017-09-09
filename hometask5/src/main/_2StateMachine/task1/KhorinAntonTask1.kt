package _2StateMachine.task1

import _2StateMachine.model.*


interface StateBuilder {

    fun commands(vararg commands: Command)

    fun transition(event: Event, state: State)

    infix fun Event.leadsTo(state: State) = transition(this, state)

}

class StateBuilderImpl(val state: State) : StateBuilder {

    override fun commands(vararg commands: Command) = commands.forEach { state.addCommand(it) }

    override fun transition(event: Event, state: State) = this.state.addTransition(event, state)

}

fun State.configure(init: StateBuilder.() -> Unit) {
    val stateBuilder = StateBuilderImpl(this)
    stateBuilder.init();
}