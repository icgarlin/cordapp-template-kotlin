package com.template.contracts


import com.example.contract.BALLOTContract
import net.corda.core.identity.Party
import com.example.state.BALLOTState
import net.corda.core.contracts.TypeOnlyCommandData
import net.corda.testing.node.MockServices
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.MockNetworkParameters
import org.junit.Test
import net.corda.testing.node.*
import net.corda.core.identity.CordaX500Name
import net.corda.testing.node.TestCordapp.Companion.findCordapp
import net.corda.testing.internal.chooseIdentityAndCert
import net.corda.core.contracts.*
import net.corda.finance.*


class BALLOTContractTests {

    class DummyCommand : TypeOnlyCommandData()
    private var ledgerServices = MockServices(listOf("com.example.contract"))



    private var mockNetwork: MockNetwork = MockNetwork(listOf("net.corda.training"),
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB"))))

cd

    var a: StartedMockNode = mockNetwork.createNode(MockNodeParameters())
    var b: StartedMockNode = mockNetwork.createNode(MockNodeParameters())

    private val issuer: Party = a.info.chooseIdentityAndCert().party
    private val voter: Party = b.info.chooseIdentityAndCert().party

    // private val selections = mapOf<String, Boolean>()
    // private val count = mapOf<String, Int>()

    @Test
    fun mustIncludeIssueBallotCommand() {

        val selections = mapOf<String, Boolean>()
        val count = mapOf<String, Int>()

        val ballot = BALLOTState(count, issuer, voter, selections)
        ledgerServices.ledger {
            transaction {
                output(BALLOTContract.BALLOT_CONTRACT_ID, ballot)
                command(listOf(issuer.owningKey, voter.owningKey), DummyCommand()) // Wrong type.
                this.fails()
            }
            transaction {
                output(BALLOTContract.BALLOT_CONTRACT_ID, ballot)
                command(listOf(issuer.owningKey, voter.owningKey), BALLOTContract.Commands.IssueBallot()) // Correct type.
                this.verifies()
            }
        }

    }


    @Test
    fun mustIncludeFillOutCommand() {

        val selections = mapOf<String, Boolean>()
        val count = mapOf<String, Int>()

        val ballot = BALLOTState(count, issuer, voter, selections)
        ledgerServices.ledger {
            transaction {
                output(BALLOTContract.BALLOT_CONTRACT_ID, ballot)
                command(listOf(issuer.owningKey, voter.owningKey), DummyCommand()) // Wrong type.
                this.fails()
            }
            transaction {
                output(BALLOTContract.BALLOT_CONTRACT_ID, ballot)
                command(listOf(issuer.owningKey, voter.owningKey), BALLOTContract.Commands.FillOut()) // Correct type.
                this.verifies()
            }
        }

    }
}