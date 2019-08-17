package com.example.contract



import com.example.state.BALLOTState
import net.corda.core.contracts.*
import net.corda.core.transactions.LedgerTransaction
import java.time.Instant
import java.time.temporal.ChronoUnit
import net.corda.core.flows.*
import net.corda.core.node.NodeInfo

/**
 * A implementation of a basic smart contract in Corda.
 *
 * This contract enforces rules regarding the creation of a valid [BALLOTState], which in turn encapsulates an [VOTERState].
 *
 * For a new [BALLOTState] to be issued onto the ledger, a transaction is required which takes:
 * - Zero input states
 * - One output state: the new [BALLOTState].
 *
 * All contracts must sub-class the [Contract] interface.
 */
class BALLOTContract : Contract {
    companion object {
        @JvmStatic
        val BALLOT_CONTRACT_ID = "com.example.contract.BALLOTContract"

    }



    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: LedgerTransaction) {
        val command = tx.commands.requireSingleCommand<BALLOTContract.Commands>()
        when (command.value) {




            is Commands.IssueBallot -> requireThat {
                "No inputs should be consumed when issuing a BALLOT." using (tx.inputs.isEmpty())
                "Only one output state should be created when issuing an BALLOT." using (tx.outputs.size == 1)
                val ballot = tx.outputStates.single() as BALLOTState
                "The list of selections is not empty" using (ballot.selections.isNotEmpty())




            }

            is Commands.FillOut -> requireThat {

                val ballot = tx.outputStates.single() as BALLOTState
                var count = 0

                // for each selection the voter chooses the
                // count will increase by 1 to
                // get the total amount of selections in a single ballot
//                for ((key, value ) in ballot.selections) {
//                    if (value) {
//                        count++
//                    }
//                }
//
//                "Voting party has not chosen more selections than is allowed by issuer." using (count <= ballot.maxChoices)






            }





        }



    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class IssueBallot: Commands
        class FillOut: Commands
    }
}