import app.Branch
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BranchTest {

    @Test
    fun subtract() {
        val localBranches = mutableListOf(
                Branch("develop"),
                Branch("CORE-3333_test")
        )

        val remoteBranches = mutableListOf(
                Branch("remotes/origin/develop"),
                Branch("remotes/origin/CORE-2222_do_something")
        )

        val subtract = localBranches.map { it.name }.subtract(remoteBranches.map { it.name })
        assertEquals(setOf("CORE-3333_test"), subtract)
    }

}