# cmd

A quarkus based CLI tool to do quick snippet-lookups based on [www.commands.dev](https://www.commands.dev)

## Usage
```bash
./cmd docker
# Desc: Removes all stopped containers. To clean up all unused containers, networks, images and volumes in one command, run `docker system prune`.
# Tags: docker
docker container prune

# Desc: Runs a shell script located within a running Docker container.
# Tags: docker
docker exec {{container_id}} {{filepath_in_container}}

# Desc: Lists all environment variables from a Docker container.
# Tags: docker
docker exec container env

# Desc: Copies files from a Docker container back to the host. Note the container does not need to be running in order to use this command.
# Tags: docker
docker cp {{container_id}}:{{container_filepath}} {{local_filepath}}

# Desc: Runs a Bash subshell within a Docker container.
# Tags: docker
docker exec -it {{container_name}} bash

# Desc: Copies a file (or multiple files) from a host machine to a container.
# Tags: docker
docker cp {{local_file_path}} {{container_id}}:/{{container_filepath}}
```

### Using the tool behind a proxy
The CLI tool tries to discover the proper proxy-settings by reading the `HTTPS_PROXY` environment variable (and also supports proxies that require basic auth).
If you're executing the tool outside of a shell-environment or if the auto-discovery doesn't work properly you can hand-over the required proxy settings explicitly via respective options (see `--help`):

```bash
#  in that case I'd recommend a wrapper-bash-function
./cmd --proxy-host proxy.muc --proxy-port 8080 docker
```

### Output-formats and Alfred integration

The CLI tool also supports a XML output format that can be processed/using by an [Alfred](https://www.alfredapp.com)-Workflow for quick snippet lookup.

## Installation

1. Checkout the repository and make sure you have all pre-requisites for compiling to native code
    1. GraalVM (tested with version `22.1.0.r17`)
    2. make sure you can compile to native images with that installation (i.e. `gu install native-image`)
2. next compile the code to native image and generate the binaries using `./buildAll.sh`
3. in the `target` folder you should see now a `cmd` binary
    1. move that binary to our `$PATH`

## Developement

As already mentioned - this project makes use of Quarkus and PicoCLI to create a native executable.

- used for active development (also use the `e`-key to define and re-execute a specific command)
   ```shell script
   ./mvnw compile quarkus:dev
   ```
    - As picocli applications will often require arguments to be passed on the commandline, this is also possible in dev mode via:
      ```shell script
      ./mvnw compile quarkus:dev -Dquarkus.args='-o alfred docker'
      ```
      Also for picocli applications the dev mode is supported. When running dev mode, the picocli application is executed and on press of the Enter key, is restarted.
- Creating a native executable
   ```shell script
   ./mvnw package -Pnative
   ```
    - Or, if you don't have GraalVM installed, you can run the native executable build in a container using (produces a linux-binary):
       ```shell script
       ./mvnw package -Pnative -Dquarkus.native.container-build=true
       ```

You can then execute your native executable with: `./target/cmd-1.0.0-SNAPSHOT-runner` - it's recommended to rename or define an alias for the final result

### Produce a Autocompletion-Script

There is a bash-script included that does all for you (buliding a native image as well as generating an autocompletion script):
```bash
$>./buildAll.sh
$> # or do it manually
$> cd target/quarkus-app
$> java -cp $(ls lib/main | awk '{print "lib/main/" $1}' | tr "\n" ":")../cmd-1.0.0-SNAPSHOT.jar picocli.AutoComplete de.bender.commandsdev.boundary.Commands
```

### Related Guides

- Picocli ([guide](https://quarkus.io/guides/picocli)): Develop command line applications with Picocli

## Alfred integration
Although there is already a `cmd`-workflow for Alfred I was eager to make my own experiences (for educational purposes mainly).

I've initially provided a `workflow`-definition in this repo as well as an export (as described [in here][workflow-on-github] - out-of-the-box only for Intel-Macs).

![](./docs/alfred.gif)

[workflow-on-github]:https://www.alfredapp.com/blog/guides-and-tutorials/share-workflow-on-github/
