package _2StateMachine.task2

import _2StateMachine.model.StateMachine

fun createStateMachine1(): StateMachine = stateMachine("start") {
    event("whistle")
    event("finishLine")

    command("run")
    command("stop")

    state("start") {
        transition("whistle", "running")
    }
    state("running") {
        transition("finishLine", "finish")
        commands("run")
    }
    state("finish") {
        commands("stop")
    }
}

fun createStateMachine2(): StateMachine = stateMachine("switch_off") {
    event("on")
    event("off")

    state("switch_off") {
        transition("on", "switch_on")
    }
    state("switch_on") {
        transition("off", "switch_off")
    }
}

fun createStateMachine3(): StateMachine = stateMachine("switch_off") {
    event("on")
    event("off")

    command("on")
    command("off")

    state("switch_off") {
        transition("on", "switch_on")
        commands("off")
    }
    state("switch_on") {
        transition("off", "switch_off")
        commands("on")
    }
}