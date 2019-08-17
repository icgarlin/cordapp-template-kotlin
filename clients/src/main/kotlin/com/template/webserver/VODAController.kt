package com.template.webserver

import com.example.flow.IssueBallotFlow
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.vault.QueryCriteria
import org.slf4j.LoggerFactory
import com.example.state.BALLOTState
import net.corda.core.identity.Party
import net.corda.core.messaging.startTrackedFlow
import net.corda.core.node.services.Vault
import net.corda.core.utilities.getOrThrow
import nonapi.io.github.classgraph.json.JSONSerializer
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletRequest



import java.io.InputStream
import java.nio.charset.Charset
import javax.servlet.http.Part


/**
 * Define your API endpoints here.
 */

@CrossOrigin(origins = arrayOf("*"), maxAge = 3600, allowedHeaders = arrayOf("*"), methods = arrayOf(RequestMethod.POST, RequestMethod.GET))
@RestController
@RequestMapping("/voda") // The paths for HTTP requests are relative to this base path.
class Controller(rpc: NodeRPCConnection) {

    companion object {
        private val logger = LoggerFactory.getLogger(RestController::class.java)
    }

    private val proxy = rpc.proxy

    @GetMapping(value = "/status", produces = arrayOf("text/plain"))
    private fun status() = "200"

    @GetMapping(value = "/ballots/{id}", produces = arrayOf("text/plain"))
    private fun ballots(@PathVariable("id") linearId: String?): String {


        val ballotId = UniqueIdentifier(id = UUID.fromString(linearId))

        val listOfBALLOTStates = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(ballotId), status = Vault.StateStatus.UNCONSUMED)


        val results = proxy.vaultQueryByCriteria(listOfBALLOTStates, BALLOTState::class.java)


        val ballotState = results.states.single().state.data


        return JSONSerializer.serializeObject(ballotState)

    }


    @GetMapping(value = "/ballots", produces = arrayOf("text/plain"))
    private fun allBallots() = proxy.vaultQueryBy<BALLOTState>().states.toString()


    @PostMapping(value = ["issue-ballot"], headers = ["Content-Type=application/json"])
    fun issueBallot(@RequestBody selections: List<String>, request: HttpServletRequest): ResponseEntity<String>{




        val ballotSelections = mutableMapOf<String,Boolean>()
        for (s in selections) {

            ballotSelections.put(s,false)

        }



        // create variables for the issuer node and
        // list of voters
        val issuer: Party = proxy.nodeInfo().legalIdentities.first()

        val voters = mutableListOf<Party>()


        // accesses all nodes on the network
        // retrieves nodes that do not serve as notary or issuer
        // adds them to list of voters
        val nodeNetworkInfo = proxy.networkMapSnapshot()

        for (info in nodeNetworkInfo) {

            for (id in info.legalIdentities) {

                println(id.name.organisation + " and " + issuer.name.organisation)
                if ((id != issuer) && (id.name.organisation != "Notary" )) {
                    voters.add(id)
                    println(voters)
                }
            }

        }


        val ballot: BALLOTState = BALLOTState(issuer = issuer, voters = voters, selections = ballotSelections)

        println(ballot)

//        val stx = proxy.startTrackedFlow(::IssueBallotFlow, ballot)
//        val vaultQueryBallot =



        return try {


            val stx = proxy.startTrackedFlow(::IssueBallotFlow, ballot).returnValue.getOrThrow()
            ResponseEntity.status(HttpStatus.CREATED).body("Ballot id ${stx.id} committed to ledger")

        } catch (ex: Throwable) {


            logger.error(ex.message, ex)
            ResponseEntity.badRequest().body(ex.message)
        }


    }


    private fun InputStream.readTextAndClose(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }


}