# NextStep

FormOps Agent prototype for telling users the next step in an enterprise form workflow.

## Current Scope

- MVP flow knowledge config: `config/*.json`
- Java Spring Boot MCP-like server prototype: `mcp-server/`

## Run MCP Server

Requirements:

- JDK 17+
- Maven 3.9+

```bash
mvn -f mcp-server/pom.xml spring-boot:run
```

See `mcp-server/README.md` for API examples.
