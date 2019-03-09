# Wendy

## Bob's reference CLI and his best friend.

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
- [Python](https://www.python.org/downloads/) 3.6+
- [Poetry](https://poetry.eustace.io/)
- A running instance of [Bob](https://github.com/bob-cd/bob)

### Running

Clone the repo and in the repo and in preferably a [virtualenv](https://www.pythonforbeginners.com/basics/how-to-use-python-virtualenv/):
- `poetry install`
- It reads its config from `defaults.toml` and can be overridden by CLI options  
- `python3 wendy/main.py can-we-build-it` should output `"Yes we can! 🔨 🔨"`

#### The full command reference can be found [here](https://github.com/bob-cd/wendy/blob/master/docs/commands.md)
