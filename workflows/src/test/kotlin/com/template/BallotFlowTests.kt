package com.template

import com.example.contract.BALLOTContract
import com.example.flow.FillBallotFlow
import com.example.flow.IssueBallotFlow
import com.example.state.BALLOTState
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow

import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.MockNetworkParameters
import net.corda.testing.node.TestCordapp
import org.junit.After
import org.junit.Before
import org.junit.Test

class BallotFlowTests {
    private val network = MockNetwork(MockNetworkParameters(cordappsForAllNodes = listOf(
        TestCordapp.findCordapp("com.example.contract"),
        TestCordapp.findCordapp("com.example.flow")
    )))
    private val a = network.createNode()
    private val b = network.createNode()

    init {
        listOf(a, b).forEach {
            it.registerInitiatedFlow(FillBallotFlow::class.java)
        }
    }

    @Before
    fun setup() = network.runNetwork()

    @After
    fun tearDown() = network.stopNodes()

    @Test
    fun flowReturnsCorrectlyFormedPartiallySignedTransaction() {

        val count = mapOf<String, Int>()
        val issuer = a.info.chooseIdentityAndCert().party
        val voter = b.info.chooseIdentityAndCert().party
        val selections = mapOf<String, Boolean>()
        val ballot = BALLOTState(count, issuer, voter, selections = selections)
        val flow = IssueBallotFlow(ballot)
        val future = a.startFlow(flow)
        network.runNetwork()

        val ptx: SignedTransaction = future.getOrThrow()
        println(ptx.tx)

        assert(ptx.tx.inputs.isEmpty())
        assert(ptx.tx.outputs.single().data is BALLOTState)
        val command = ptx.tx.commands.single()
        assert(command.value is BALLOTContract.Commands.IssueBallot)
        assert(command.signers.toSet() == ballot.participants.map { it.owningKey }.toSet())
        ptx.verifySignaturesExcept(voter.owningKey, network.defaultNotaryNode.info.legalIdentitiesAndCerts.first().owningKey)



    }
}