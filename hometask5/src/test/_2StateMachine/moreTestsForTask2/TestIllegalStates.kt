package _2StateMachine.moreTestsForTask2

import _2StateMachine.task2.stateMachine
import org.junit.Test
import kotlin.test.assertFailsWith

open class TestIllegalStates {

    @Test
    fun onlyKnownCommand() {
//         uncomment the code below
        stateMachine("START") {
            state("START") {
                assertFailsWith<IllegalStateException> {
                    commands("UNKNOWN")
                }
            }
        }
    }

    @Test
    fun uniqueCommands() {
        stateMachine("START") {
            command("COMMAND")
            assertFailsWith<IllegalStateException> {
                command("COMMAND")
            }
            state("START") {}
        }
    }

    @Test
    fun onlyKnownEvent() {
        stateMachine("START") {
            state("START") {
                assertFailsWith<IllegalStateException> {
                    transition("CYCLE", target = "START")
                }
            }
        }
    }

    @Test
    fun uniqueEvents() {
        stateMachine("START") {
            event("EVENT")
            assertFailsWith<IllegalStateException> {
                event("EVENT")
            }
            state("START") {}
        }
    }


    @Test
    fun onlyKnownResetEvent() {
        stateMachine("START") {
            state("START") {}
            assertFailsWith<IllegalStateException> {
                resetEvents("UNKNOWN")
            }
        }
    }

    @Test
    fun uniqueStates() {
        stateMachine("START") {
            state("START") {}
            assertFailsWith<IllegalStateException> {
                state("START") {}
            }
        }
    }

    @Test
    fun transitionOnlyToKnownStates() {
        assertFailsWith<IllegalStateException> {
            stateMachine("START") {
                event("GO")
                state("START") {
                    transition("GO", target = "LAST")
                }
            }
        }
    }

    @Test
    fun startStateIsDeclared() {
        assertFailsWith<IllegalStateException> {
            stateMachine("START") {
            }
        }
    }
}