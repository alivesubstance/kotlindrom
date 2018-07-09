package app.git

fun main(args: Array<String>) {
    LastCommitterFinder.start()
}

object LastCommitterFinder {

    fun start() {
        println("Looking for an last committer of the project")

        val config = ConfigProvider.readConfig()
        val gitClient = GitClient(config)

        config.listProjects()
                .flatMap { gitClient.findLastCommitter(it) }
                .sortedBy { it.lastCommitter.toLowerCase() }
                .forEach { println("${it.lastCommitter} https://bitbucket.org/pleeco/${it.project}/branch/${it.branch}?dest=develop") }
    }

}