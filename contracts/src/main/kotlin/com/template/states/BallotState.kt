package com.example.state

import com.example.contract.BALLOTContract
import net.corda.core.contracts.*
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party
import java.time.Duration


/**
 * The state object recording VOTING BALLOTS between two parties.
 *
 * A state must implement [ContractState] or one of its descendants.
 *
 * @param ballot a map containing the selections for a vote and a boolean representing yes/no
 */
@BelongsToContract(BALLOTContract::class)
data class BALLOTState(
                       /*var count: MutableMap<String,Int>,*/
                       val issuer: Party,
                       val voters: MutableList<Party>,
                       var selections: Map<String,Boolean>,
                       /*val maxChoices: Int,
                       val daysOpen: Int,*/
                       override val linearId: UniqueIdentifier = UniqueIdentifier()):


        LinearState {
    /** The public keys of the involved parties. */

    // creates a copy of voters list
    val votersCopy = voters.filter{true}
    val newVotersCopy = votersCopy.toMutableList()


    // creates a participant list
    val createdParticipantsList: Boolean = newVotersCopy.add(issuer)
    override val participants: List<Party> get() = newVotersCopy



}