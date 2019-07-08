package net.corda.training.state

import net.corda.core.contracts.*
import net.corda.core.identity.Party
import net.corda.finance.*
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import com.example.state.BALLOTState


class BALLOTStateTests {


    @Test
    fun hasBALLOTIssuerFieldOfCorrectType() {
        // Does the issuer field exist?
        BALLOTState::class.java.getDeclaredField("issuer")
        // Is the issuer field of the correct type?
        assertEquals(BALLOTState::class.java.getDeclaredField("issuer").type, Party::class.java)
    }



    @Test
    fun hasBALLOTVoterFieldOfCorrectType() {
        // Does the voter field exist?
        BALLOTState::class.java.getDeclaredField("voter")
        // Is the voter field of the correct type?
        assertEquals(IOUState::class.java.getDeclaredField("voter").type, Party::class.java)
    }



    @Test
    fun hasBALLOTSelectionsFieldOfCorrectType() {
        // Does the selections field exist?
        BALLOTState::class.java.getDeclaredField("selections")
        // Is the borrower field of the correct type?
        assertEquals(BALLOTState::class.java.getDeclaredField("selections").type, Party::class.java)
    }


    @Test
    fun isLinearState() {
        assert(LinearState::class.java.isAssignableFrom(BALLOTState::class.java))
    }



    @Test
    fun hasLinearIdFieldOfCorrectType() {
        // Does the linearId field exist?
        BALLOTState::class.java.getDeclaredField("linearId")
        // Is the linearId field of the correct type?
        assertEquals(BALLOTState::class.java.getDeclaredField("linearId").type, UniqueIdentifier::class.java)
    }


    @Test
    fun checkBALLOTStateParameterOrdering() {
        val fields = BALLOTState::class.java.declaredFields
        val countIdx = fields.indexOf(BALLOTState::class.java.getDeclaredField("count"))
        val issuerIdx = fields.indexOf(BALLOTState::class.java.getDeclaredField("issuer"))
        val voterIdx = fields.indexOf(BALLOTState::class.java.getDeclaredField("voter"))
        val selectionsIdx = fields.indexOf(BALLOTState::class.java.getDeclaredField("selections"))
        val linearIdIdx = fields.indexOf(BALLOTState::class.java.getDeclaredField("linearId"))

        assert(countIdx < issuerIdx)
        assert(issuerIdx < voterIdx)
        assert(voterIdx < selectionsIdx)
        assert(selectionsIdx < linearIdIdx)
    }






}