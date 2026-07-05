# NextStep

FormOps Agent prototype for telling users the next step in an enterprise form workflow.

## Current Scope

- MVP flow knowledge config: `config/*.json`
- Java Spring Boot MCP-like server prototype: `mcp-server/`

## Run MCP Server

Requirements:

- Java 21 recommended
- Maven 3.9+

On macOS with Homebrew:

```bash
brew install openjdk@21 maven
```

For zsh, set Java 21 in `~/.zshrc`:

```bash
export JAVA_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

Verify:

```bash
java -version
mvn -version
```

Run the server from the repository root:

```bash
mvn -f mcp-server/pom.xml spring-boot:run
```

See `mcp-server/README.md` for API examples.
