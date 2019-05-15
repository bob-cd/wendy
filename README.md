# Wendy

## Bob's SO and the reference CLI.

[![License: GPL v3](https://img.shields.io/badge/license-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

She is an `opinionated client` for Bob and takes care of the following tasks:

- Implement a full CLI to all of Bob's REST API with corresponding and semantic commands
- Read a `build.toml` file and translate to REST API calls to Bob
- Plan a build:
    - Bob doesnt know of the order of execution of pipelines
    - Wendy reads the dependencies of a pipeline on other pipelines and schedules them accordingly
    - Trigger all of the upstream pipelines of a pipeline such that Bob can proceed with the builds
    
Wendy is a reference client for Bob implementing a build with certain opinions but others are
encouraged to build their own CLIs using Bob as an engine in ways they see fit.

**This is a work in progress in conjunction with Bob and will be evolving.**

## Installation

**TBD**

## Building and running from source

### Requirements:
- [Graal VM](https://www.graalvm.org/downloads/)
- [Clojure CLI](https://clojure.org/guides/getting_started)
- A running instance of [Bob](https://github.com/bob-cd/bob)

### Running

Clone the repo and in the repo and:
- Run `clojure -A:test -m kaocha.runner` to run unit tests.
- Set the `GRAALVM_HOME` env var to the path: `<graal_download_path>/Home`.
- Run `<graal_donwload_path>/Home/bin/gu install native-image` to get the Graal native compiler.
- Run `clojure -A:native-image` to compile it to a native executable. (Warning: Quite resource heavy step)  
- `./wendy can-we-build-it` should output `"Yes we can! 🔨 🔨"`

#### The full command reference can be found [here](https://github.com/bob-cd/wendy/blob/master/docs/commands.md)
