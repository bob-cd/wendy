# Wendy

## Bob's SO and the reference CLI.

Wendy is an `opinionated client` for Bob and takes care of the following tasks:
- Implement a full CLI to all of Bob's REST API with corresponding and semantic commands

#### TODO
- Read declarative file(s) and translate to REST API calls to Bob
- Plan and orchestrate builds:
    - Bob doesnt know of the order of execution of pipelines
    - Wendy reads the dependencies of a pipeline on other pipelines and schedules them accordingly
    - Trigger all of the upstream pipelines of a pipeline such that Bob can proceed with the builds
- More?

Wendy is a reference client for Bob implementing a build with certain opinions but others are encouraged to build their own CLIs using Bob as an engine in ways they see fit.

Wendy follows the same spec-first approach like Bob, bootstrapping from the OpenAPI spec.

## Status
🚧 Under construction 🚧

## Requirements:
- A running instance of [Bob](https://github.com/bob-cd/bob)
- Go 1.23+ is [installed](https://go.dev/doc/install)

### Running
- Clone the repo
- Build and install `go install`
- Configure `wendy configure`
- Bootstrap `wendy bootstrap`
- See whats possible `wendy --help`

## License
Wendy like Bob is [Free](https://www.gnu.org/philosophy/free-sw.en.html) and Open Source and always will be. Licensed fully under [MIT](https://opensource.org/licenses/MIT)
