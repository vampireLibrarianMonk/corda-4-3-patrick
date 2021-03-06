package com.example.state

import net.corda.core.contracts.Amount
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.Party
import com.example.contract.IOUContract
import java.util.*

/**
 * The IOU State object, with the following properties
 * - [amount] The amount owed by the [borrower] to the [lender].
 * - [lender] The lending party.
 * - [borrower] The borrowing party.
 * - [contract] Holds a reference to the [IOUContract]
 * - [paid] Records how much of the [amount] has been paid.
 * - [linearId] A unique id shared by all LinearState states representing the same agreement throughout history within
 *  the vaults of all parties. Verify methods should check that one input and one output share the id in a transaction,
 *  except at issuance/termination.
 */
@BelongsToContract(IOUContract::class)
data class IOUState(val amount: Amount<Currency>,
                    val lender: Party,
                    val borrower: Party,
                    val paid: Amount<Currency> = Amount(0, amount.token),
                    override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState {

    /**
     * This property holds a list of the nodes which can "use" this state in a valid transaction. In this case, the
     * lender or the borrower.
     */
    override val participants: List<Party> get() = listOf(lender, borrower)

    /**
     * Helper 1: [pay] Adds an amount to the paid property.
     */
    fun pay(amountToPay: Amount<Currency>) = copy(paid = paid.plus(amountToPay))

    /**
     * Helper 2: [withNewLender] Creates a copy of the current state with a newly specified lender. For use when
     * transferring.
     */
    fun changeLender(newLender: Party) = copy(lender = newLender)
}