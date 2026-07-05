# FormOps MCP Server Prototype

Java Spring Boot prototype for the FormOps Agent MCP-like tool API.

## Requirements

- JDK 17+
- Maven 3.9+

## Run

From the repository root:

```bash
mvn -f mcp-server/pom.xml spring-boot:run
```

The server starts on `http://localhost:8080`.

## Tool API

### create_task

```bash
curl -X POST http://localhost:8080/tools/create_task \
  -H 'Content-Type: application/json' \
  -d '{
    "goal": "我要建置一個新的對外系統，系統名稱叫 APLY，會使用 PostgreSQL，也會處理個資。",
    "requester": "user001",
    "context": {
      "systemName": "APLY",
      "externalFacing": true,
      "needDatabase": true,
      "databaseType": "PostgreSQL",
      "hasPersonalData": true
    }
  }'
```

### plan_forms

```bash
curl -X POST http://localhost:8080/tools/plan_forms \
  -H 'Content-Type: application/json' \
  -d '{"taskId":"TASK-20260705-001"}'
```

### check_missing_fields

```bash
curl -X POST http://localhost:8080/tools/check_missing_fields \
  -H 'Content-Type: application/json' \
  -d '{"taskId":"TASK-20260705-001","formCode":"SYS_REQ"}'
```

### create_form_draft

This prototype returns mock draft data. Playwright integration can replace this handler later.

```bash
curl -X POST http://localhost:8080/tools/create_form_draft \
  -H 'Content-Type: application/json' \
  -d '{
    "taskId": "TASK-20260705-001",
    "formCode": "SYS_REQ",
    "input": {
      "expectedGoLiveDate": "2026-09-30",
      "systemOwner": "user001",
      "businessOwner": "業務一部",
      "hasExistingSystemCode": false
    }
  }'
```

### get_task_status

```bash
curl http://localhost:8080/tools/get_task_status/TASK-20260705-001
```
