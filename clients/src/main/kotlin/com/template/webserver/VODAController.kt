package com.template.webserver

import net.corda.core.contracts.ContractState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.messaging.vaultQueryBy
import net.corda.core.node.services.vault.QueryCriteria
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.example.state.BALLOTState
import net.corda.core.node.services.VaultService
import nonapi.io.github.classgraph.json.JSONSerializer



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

    @GetMapping(value = "/ballots/{id:.+}", produces = arrayOf("text/plain"))
    private fun ballots(@PathVariable linearId: String?): String {


        val ballotId = UniqueIdentifier(linearId)
        val listOfBALLOTStates = proxy.vaultQueryByCriteria(QueryCriteria.LinearStateQueryCriteria(linearId = listOf(ballotId)), BALLOTState::class.java)

        val ballotState = listOfBALLOTStates.states.single().state.data

        return JSONSerializer.serializeObject(ballotState)

    }

    @GetMapping(value = "/ballots", produces = arrayOf("text/plain"))
    private fun states() = proxy.vaultQueryBy<BALLOTState>().states.toString()





}