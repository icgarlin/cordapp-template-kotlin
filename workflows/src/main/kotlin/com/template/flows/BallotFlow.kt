package com.example.flow

import co.paralleluniverse.fibers.Suspendable
import com.example.contract.BALLOTContract
import com.example.state.BALLOTState
import net.corda.core.contracts.Command
import net.corda.core.contracts.Requirements.using
import net.corda.core.contracts.TimeWindow
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.days
import java.lang.IllegalArgumentException
import java.time.Duration
import java.time.Instant


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



        // verifies that the node initiating the flow
        // is the issuing party
        if (ourIdentity != ballot.issuer) {
            throw IllegalArgumentException("IssueBallotFlow must be initiated by the issuer node")
        }




        // retrieves the first notary from the network
        val notary = serviceHub.networkMapCache.notaryIdentities.first()




        // creates a command for the transaction construction
        val issueCommand = Command(BALLOTContract.Commands.IssueBallot(), ballot.participants.map {it.owningKey} )

        val builder = TransactionBuilder(notary=notary)

        builder.addOutputState(ballot, BALLOTContract.BALLOT_CONTRACT_ID)
        builder.addCommand(issueCommand)


        val daysOpen : Long = ballot.daysOpen.toLong()
        val duration = Duration.ofDays(daysOpen)


        val timeWindow: TimeWindow = TimeWindow.withTolerance(serviceHub.clock.instant(), duration)

        builder.setTimeWindow(timeWindow)


        // initial signing of the transaction before being sent to
        // participating nodes
        val ptx = serviceHub.signInitialTransaction(builder)


        val sessions = (ballot.participants - ourIdentity).map { initiateFlow(it) }.toSet()


        // val session = initiateFlow(ballot.voter)


        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))



        return subFlow(FinalityFlow(stx, sessions))

    }
}






@InitiatedBy(IssueBallotFlow::class)
class IssueBallotResponderFlow(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {


    @Suspendable
    override fun call(): SignedTransaction {

        // why does this expression work?
        val stf = object : SignTransactionFlow(flowSession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {

                val output = stx.tx.outputs.single().data
                "This must be a BALLOT transaction" using (output is BALLOTState)



            }
        }

        val txId = subFlow(stf).id

        return subFlow(ReceiveFinalityFlow(flowSession, expectedTxId = txId))
    }

}





@InitiatingFlow
@StartableByRPC
class FillBallotFlow(val ballot: BALLOTState, val selected: Map<String,Boolean>) : FlowLogic<SignedTransaction>() {



    override fun call(): SignedTransaction {

        // verifies that the node initiating the flow
        // is the voting party
        if (ourIdentity != ballot.voter) {

            throw IllegalArgumentException("FillBallotFlow must be initiated by voter node")


        }

        // iterates through selected map
        // and increases the values in count map by 1
        // for each selection that has a value of true
        for ((key, value) in selected) {

            if (value) {

                var initialVal = (ballot.count.get(key) as Int)
                ballot.count.replace(key,initialVal,initialVal+1)
            }

        }

        // creates a new transaction with the selections
        // of the voting party now being used to construct
        // a new ballot
        val notary = serviceHub.networkMapCache.notaryIdentities.first()
        val newBallot = ballot.copy(selections = selected)
        val command = Command(BALLOTContract.Commands.FillOut(), ballot.participants.map {it.owningKey})

        val builder = TransactionBuilder(notary=notary)

        builder.addCommand(command)
        builder.addOutputState(newBallot,BALLOTContract.BALLOT_CONTRACT_ID)

        val ptx = serviceHub.signInitialTransaction(builder)

        val sessions = (ballot.participants - ourIdentity).map {initiateFlow(it)}.toSet()
        val stx = subFlow(CollectSignaturesFlow(ptx, sessions))

        return subFlow(FinalityFlow(stx, sessions))



    }


}



@InitiatedBy(FillBallotFlow::class)
class FillBallotFlowResponder(val flowSession: FlowSession) : FlowLogic<SignedTransaction>() {


    override fun call(): SignedTransaction {

        val stf = object : SignTransactionFlow(flowSession) {

            override fun checkTransaction(stx: SignedTransaction) {

                val output = stx.tx.outputs.single().data
                "This must be a BALLOT transaction" using (output is BALLOTState)


                val ballot = output as BALLOTState
                var count = 0

                for ((key, value) in ballot.selections) {

                    if (value) {
                        count++
                    }
                }

                "Voting party has not chosen more selections than is allowed by issuer." using (count <= ballot.maxChoices)
            }
        }
        val txId = subFlow(stf).id

        return subFlow(ReceiveFinalityFlow(flowSession,expectedTxId = txId))


    }



}


