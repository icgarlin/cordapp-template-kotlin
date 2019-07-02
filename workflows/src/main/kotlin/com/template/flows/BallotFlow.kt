package com.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.example.contract.BALLOTContract
import com.example.state.BALLOTState
import net.corda.core.contracts.Command
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder


/**
 * This flow allows two parties (the [Ballot Issuer] and the [Voter]) to come to an agreement about the state of a ballot encapsulated
 * within an [BALLOTState].
 *
 * In our simple example, the [Voter] always accepts a valid ballot.
 *
 * These flows have deliberately been implemented by using only the call() method for ease of understanding. In
 * practice we would recommend splitting up the various stages of the flow into sub-routines.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */

@InitiatingFlow
@StartableByRPC
class IssueBallotFlow(val ballot: BALLOTState) : FlowLogic<SignedTransaction>() {


    /**
     * The flow logic is encapsulated within the call() method.
     */
    @Suspendable
    override fun call(): SignedTransaction {


        // retrieves the first notary from the network
        val notary = serviceHub.networkMapCache.notaryIdentities.first()


        // creates a command for the transaction construction
        val issueCommand = Command(BALLOTContract.Commands.IssueBallot(), ballot.participants.map {it.owningKey} )

        val builder = TransactionBuilder(notary=notary)

        builder.addOutputState(ballot, BALLOTContract.BALLOT_CONTRACT_ID)
        builder.addCommand(issueCommand)


        // initial signing of the transaction before being sent to
        // participating nodes
        val ptx = serviceHub.signInitialTransaction(builder)



        //val sessions = (ballot.participants - ourIdentity).map { initiateFlow(it) }.toSet()

        val session = initiateFlow(ballot.voter)


        val stx = subFlow(CollectSignaturesFlow(ptx, setOf(session)))


        return subFlow(FinalityFlow(stx, setOf(session)))



    }
}



@InitiatedBy(IssueBallotFlow::class)
class FillBallotFlow(val flowSession: FlowSession/*, val selections: Map<String, Boolean>*/) : FlowLogic<SignedTransaction>() {


    @Suspendable
    override fun call(): SignedTransaction {

        // why does this expression work?
        val stf = object: SignTransactionFlow(flowSession)


        val txId = subFlow(stf).id

        return subFlow(ReceiveFinalityFlow(flowSession, expectedTxId = txId))


    }
}
