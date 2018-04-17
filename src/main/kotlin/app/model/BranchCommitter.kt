package app.model;

class BranchCommitter(val project: String, private val ref: String) {
    var branch: String
    var lastCommitter: String

    init {
        val parts = ref.split("\t").map { it.trim() }
        branch = parts[0].replace("refs/remotes/origin/", "")
        lastCommitter = parts.subList(1, parts.size).joinToString(separator = " ")
    }
}
