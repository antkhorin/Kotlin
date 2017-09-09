package _2StateMachine.moreTestsForTask2

import _2StateMachine.model.Controller
import _2StateMachine.model.StateMachine

data class StateMachineTestResult(val commands: List<String>, val finalState: String)

fun startMachine(stateMachine: StateMachine, vararg events: String) =
    startMachine(stateMachine, events.toList())

fun startMachine(stateMachine: StateMachine, events: List<String>): StateMachineTestResult {
    val actualCommands = mutableListOf<String>()
    val controller = Controller(stateMachine, { actualCommands.add(it) })
    for (code in events) {
        controller.handle(code)
    }
    return StateMachineTestResult(actualCommands, controller.state.code)
}