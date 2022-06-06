# Wendy

## Bob's SO and the reference CLI.

Wendy is an `opinionated client` for Bob and takes care of the following tasks:
- Implement a full CLI to all of Bob's REST API with corresponding and semantic commands

#### TODO
- Read a `build.json` file and translate to REST API calls to Bob
- Plan a build:
    - Bob doesnt know of the order of execution of pipelines
    - Wendy reads the dependencies of a pipeline on other pipelines and schedules them accordingly
    - Trigger all of the upstream pipelines of a pipeline such that Bob can proceed with the builds

Wendy is a reference client for Bob implementing a build with certain opinions but others are
encouraged to build their own CLIs using Bob as an engine in ways they see fit.

## Status
🚧 Experimental 🚧

## Requirements:
- A running instance of [Bob](https://github.com/bob-cd/bob)
- Recent version of [Babashka](https://github.com/babashka/babashka#installation)

### Running
- Clone the repo.
- To see the list of commands, run from the root: `bb -m wendy.main --help`.
