package com.example.state

import com.example.contract.BALLOTContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party



/**
 * The state object recording VOTING BALLOTS between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 *
 * @param ballot a map containing the selections for a vote and a boolean representing yes/no
 */
@BelongsToContract(BALLOTContract::class)
data class BALLOTState(val count: Map<String,Int>,
                       val issuer: Party,
                       val voter: Party,
                       val selections: Map<String, Boolean>,
                       override val linearId: UniqueIdentifier = UniqueIdentifier()):
        LinearState {
    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf(issuer, voter)

}