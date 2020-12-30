## Command reference

### can-we-build-it
Performs a health check on Bob.

$ `wendy can-we-build-it`

### pipeline

Group of commands dealing with the lifecycle of a pipeline.

#### create

Create a pipeline.

Params:
- f: String = The path to the `build.toml` file. Will use the `build.toml` in the PWD if unspecified.

$ `wendy pipeline create -c /tmp/build.toml`

$ `wendy pipeline create`

A sample [build.toml](https://github.com/bob-cd/wendy/blob/main/docs/build_wendy.toml).

#### start

Starts a pipeline.

Params:
- g: String = Group of the pipeline.
- n: String = Name of the pipeline.

$ `wendy pipeline start -g dev -n build`

#### stop

Stops a running pipeline.

Params:
- g: String = Group of the pipeline.
- n: String = Name of the pipeline.
- num: Int = The run number of the pipeline.

$ `wendy pipeline stop -g dev -n build -num 2`

#### delete

Deletes a pipeline.

Params:
- g: String = Group of the pipeline.
- n: String = Name of the pipeline.

$ `wendy pipeline delete -g dev -n build`

#### logs

Returns the logs of a running pipeline.

Params:
- g: String = Group of the pipeline.
- n: String = Name of the pipeline.
- num: Int = The run number of the pipeline.
- o: Int = The line offset from the beginning of the logs.
- l: Int = The number of lines from the offset.

$ `wendy pipeline logs -g dev -n build -num 2 -o 10 -l 100`

#### status

Returns the status of a pipeline.

Params:
- g: String = Group of the pipeline.
- n: String = Name of the pipeline.
- num: Int = The run number of the pipeline.

$ `wendy pipeline status -g dev -n build -num 2`

### external-resource

Group of commands dealing with external resources.

#### register

Registers an external resource.

Params:
- n: String = Unique name of the resource.
- u: String = Url of the resource.

$ `wendy external-resource register -n git -u "http://192.168.33.10"`

#### delete

Deletes an external resource.

Params:
- n: String = Unique name of the resource.

$ `wendy external-resource delete -n git`

#### list

Lists all registered external resources by name.

$ `wendy external-resource list`

### artifact-store

Group of commands dealing with artifact store.

#### register

Registers an artifact store.

Params:
- n: String = Unique name of the store.
- u: String = Url of the store.

$ `wendy artifact-store register -n s3 -u "http://192.168.33.11"`

#### delete

Deletes an artifact store.

Params:
- n: String = Unique name of the store.

$ `wendy artifact-store delete -n git`

#### list

Lists all registered artifact stores by name.

$ `wendy artifact-store list`
