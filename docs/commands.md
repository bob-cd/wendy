## Command reference

### can-we-build-it
Performs a health check on Bob.

$ `wendy health-check`

### pipeline

Group of commands dealing with the lifecycle of a pipeline.

#### create

Create a pipeline.

Params:
- data: String = The path to the `build.json` file. You can use `-` as a placeholder to read from stdin.
- group: String = Group of the pipeline.
- name: String = Name of the pipeline.

$ `wendy pipeline-create --data /docs/build.json --group dev --name build`

A sample [build.json](https://github.com/bob-cd/wendy/blob/main/docs/build.json).

#### start

Starts a pipeline.

Params:
- group: String = Group of the pipeline.
- name: String = Name of the pipeline.

$ `wendy pipeline-start --group dev --name build`

#### stop

Stops a running pipeline.

Params:
- group: String = Group of the pipeline.
- name: String = Name of the pipeline.
- id: String = Run ID of the pipeline.

$ `wendy pipeline-stop --group dev --name build --id r-20e19dcc-9fdb-475d-96b1-63416b0f7b44`

#### delete

Deletes a pipeline.

Params:
- group: String = Group of the pipeline.
- name: String = Name of the pipeline.

$ `wendy pipeline delete --group dev --name build`

#### logs

Returns the logs of a running pipeline.

Params:
- group: String = Group of the pipeline.
- name: String = Name of the pipeline.
- id: String = Run ID of the pipeline.
- offset: Int = Line offset from the beginning of the logs.
- lines: Int = Number of lines from the offset.

$ `wendy pipeline-logs --group dev --name build --id r-20e19dcc-9fdb-475d-96b1-63416b0f7b44 --offset 10 --lines 100`

#### status

Returns the status of a pipeline.

Params:
- id: String = Run ID of the pipeline.

$ `wendy pipeline-status --group dev --name build --id r-20e19dcc-9fdb-475d-96b1-63416b0f7b44`

### resource-provider

Group of commands dealing with resource providers.

#### create

Registers a resource provider.

Params:
- name: File = Unique name of the resource.
- data: String = The path to the definition file. You can use `-` as a placeholder to read the attributes from stdin.

$ `echo '{"url": "http://192.168.33.10"}' | wendy resource-provider-create --name git --data - `

#### delete

Deletes a resource provider.

Params:
- name: String = Unique name of the resource provider.

$ `wendy resource-provider-delete --name git`

#### list

Lists all registered resource-provider by name.

$ `wendy resource-provider-list`

### artifact-store

Group of commands dealing with artifact stores.

#### create

Registers an artifact store.

Params:
- name: String = Unique name of the artifact store.
- data: String = The path to the definition file. You can use `-` as a placeholder to read the attributes from stdin.

$ `echo '{"url": "http://192.168.33.11"}' | wendy artifact-store-create --name s3 --data - `

#### delete

Deletes an artifact store.

Params:
- name: String = Unique name of the artifact store.

$ `wendy artifact-store-delete --name git`

#### list

Lists all registered artifact stores by name.

$ `wendy artifact-store-list`
