package com.template.webserver

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.vault.QueryCriteria
import org.slf4j.LoggerFactory
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RestController
import com.example.state.BALLOTState
import net.corda.core.node.services.Vault
import nonapi.io.github.classgraph.json.JSONSerializer
import org.springframework.web.bind.annotation.*
import java.util.*


/**
 * Define your API endpoints here.
 */
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

        println(linearId)
        val ballotId = UniqueIdentifier(id = UUID.fromString(linearId))
        println(ballotId)
        val listOfBALLOTStates = QueryCriteria.LinearStateQueryCriteria(linearId = listOf(ballotId), status = Vault.StateStatus.UNCONSUMED)
        println(listOfBALLOTStates)

        val results = proxy.vaultQueryByCriteria(listOfBALLOTStates, BALLOTState::class.java)
        println(results)

        val ballotState = results.states.single().state.data
        println(ballotState.participants)

        return JSONSerializer.serializeObject(ballotState)

    }

    @GetMapping(value = "/ballots", produces = arrayOf("text/plain"))
    private fun allBallots() = proxy.vaultQueryBy<BALLOTState>().states.toString()





}