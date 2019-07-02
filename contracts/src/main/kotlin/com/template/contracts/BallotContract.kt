package com.example.contract



import com.example.state.BALLOTState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

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


    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class IssueBallot: Commands
        class FillOut: Commands
    }
}