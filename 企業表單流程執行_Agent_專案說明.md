# 企業表單流程執行 Agent 專案說明

## 1. 專案名稱

**企業表單流程執行 Agent**

也可以命名為：

- **FormOps Agent**
- **下一步 Agent**
- **企業流程導航與表單自動化 Agent**
- **FormOps MCP Server**

---

## 2. 專案一句話

> 使用者只需要用自然語言描述想完成的目標，Agent 就能判斷所需流程、規劃表單順序、檢查缺漏資料，並透過 MCP Server 呼叫 Playwright 工具協助建立草稿或送出公司表單。

---

## 3. 專案背景

在公司內部，許多工作並不是填寫單一表單就能完成。

例如「建置一個新系統」，可能需要依序提出多張不同表單：

1. 系統需求申請表
2. 主機資源申請表
3. 資料庫申請表
4. 防火牆申請表
5. WAF 申請表
6. 資安檢核表
7. 弱點掃描申請表
8. 上線審核表
9. 權限申請表
10. 維運交接表

這些表單通常具有：

- 順序性：某些表單必須先核准，才能提出下一張表單。
- 條件性：例如對外系統才需要 WAF；有個資才需要資安檢核。
- 相依性：前一張表單產生的系統代號、主機 IP、資料庫名稱，可能會成為下一張表單的輸入。
- 狀態性：每張表單可能處於草稿、已送出、審核中、退件、已完成等狀態。
- 權責性：有些表單由 SA 提出，有些由 PM 提出，有些需要主管或資安單位審核。

因此，使用者真正的痛點不是「不會填某一張表」，而是：

> 不知道現在應該做什麼、該提哪張表、缺什麼資料、下一步是誰要處理。

---

## 4. 專案目標

本專案希望打造一套「企業表單流程執行 Agent」，讓使用者能夠：

1. 用自然語言描述目標。
2. 由 Agent 判斷對應的公司流程。
3. 自動規劃需要提出哪些表單。
4. 判斷表單之間的先後順序與條件關係。
5. 檢查目前缺少哪些資料。
6. 呼叫 Playwright 工具協助建立表單草稿。
7. 在人工確認後送出表單。
8. 追蹤每張表單的狀態。
9. 根據目前狀態推薦下一步。

---

## 5. 核心概念

本專案不是單純的 Chatbot，也不是單純的 RPA，而是結合以下能力：

| 能力 | 說明 |
|---|---|
| LLM Agent | 理解使用者自然語言目標，進行對話與規劃 |
| 流程知識庫 | 保存公司 SOP、表單順序、條件規則與欄位定義 |
| MCP Server | 將公司表單能力封裝成 Agent 可呼叫的工具 |
| Playwright Worker | 實際操作公司表單系統，建立草稿或送出表單 |
| Web Dashboard | 讓使用者與評審查看流程、狀態、截圖與確認送出 |
| 任務狀態追蹤 | 記錄每個流程目前進度與每張表單狀態 |
| Audit Log | 保存完整執行紀錄，確保可追蹤與可稽核 |

---

## 6. 與傳統 RPA 的差異

傳統 RPA 的特點是：

- 流程固定。
- 使用者必須知道要執行哪個機器人。
- 適合重複且明確的操作。
- 不擅長判斷「下一步應該做什麼」。

本專案的特點是：

- 使用者只需要描述目標。
- Agent 會先判斷流程。
- Agent 會決定需要哪些表單。
- Agent 會檢查表單條件與前後關係。
- Agent 會呼叫對應的 Playwright 工具。
- Agent 會追蹤狀態並推薦下一步。

簡單來說：

> 傳統 RPA 解決的是「重複操作」；本專案解決的是「不知道該做什麼」以及「做起來很繁瑣」兩個問題。

---

## 7. 整體架構

```text
使用者
  ↓
Chat UI / Codex / ChatGPT
  ↓
FormOps MCP Server
  ↓
Agent Orchestrator
  ├─ Intent Parser
  ├─ Process Planner
  ├─ Dependency Resolver
  ├─ Missing Info Checker
  ├─ Execution Engine
  ├─ Approval Guard
  ├─ Form Tool Registry
  └─ Status Tracker
  ↓
Playwright Workers
  ↓
公司表單系統
  ↓
表單單號 / 系統代號 / 主機 IP / 資料庫資訊
  ↓
任務狀態庫 / Audit Log / Web Dashboard
```

---

## 8. 架構分層說明

### 8.1 使用端

使用端包含：

- 一般使用者
- SA
- PM
- 系統負責人
- 評審或管理者

使用者可以透過自然語言描述需求，例如：

```text
我要建置一個新的對外系統，會用 PostgreSQL，也會處理個資。
```

Agent 需要根據這段話判斷：

- 這是新系統建置流程。
- 是對外系統。
- 需要資料庫。
- 涉及個資。
- 可能需要資安檢核、WAF、弱掃與上線審核。

### 8.2 互動入口

互動入口可以包含兩種：

#### Chat UI / Codex / ChatGPT

負責：

- 接收使用者自然語言輸入。
- 與使用者對話。
- 詢問缺少資料。
- 呼叫 MCP 工具。
- 回覆下一步建議。

#### Web Dashboard

負責：

- 顯示任務流程。
- 顯示每張表單狀態。
- 顯示 Playwright 執行截圖。
- 顯示表單草稿內容。
- 提供人工確認按鈕。
- 顯示 Audit Log。

### 8.3 FormOps MCP Server

MCP Server 是 Agent 與公司內部工具之間的橋樑。

它不是單純的 UI，也不應該讓 Agent 直接操作瀏覽器細節。

MCP Server 的定位是：

> 將公司表單系統封裝成 Agent 可理解、可呼叫、可控管的高階工具。

Agent 不應該呼叫：

```text
clickButton
fillInput
waitForSelector
gotoPage
```

而應該呼叫：

```text
createSystemRequirementDraft
createVmRequestDraft
createDatabaseRequestDraft
createFirewallRequestDraft
getTaskStatus
submitFormAfterApproval
```

這樣可以確保 Agent 呼叫的是業務語意工具，而不是低階 UI 操作。

---

## 9. MCP Server 核心工具設計

### 9.1 create_task

建立一個流程任務。

#### 輸入範例

```json
{
  "goal": "建置新的對外系統",
  "requester": "user001",
  "context": {
    "systemName": "APLY",
    "externalFacing": true,
    "needDatabase": true,
    "databaseType": "PostgreSQL",
    "hasPersonalData": true
  }
}
```

#### 輸出範例

```json
{
  "taskId": "TASK-20260705-001",
  "dashboardUrl": "https://demo.local/tasks/TASK-20260705-001"
}
```

### 9.2 plan_forms

根據使用者目標與條件，產生需要執行的表單流程。

#### 輸入範例

```json
{
  "taskId": "TASK-20260705-001"
}
```

#### 輸出範例

```json
{
  "process": "新系統建置流程",
  "forms": [
    {
      "order": 1,
      "formCode": "SYS_REQ",
      "formName": "系統需求申請表",
      "status": "READY"
    },
    {
      "order": 2,
      "formCode": "VM_REQ",
      "formName": "主機資源申請表",
      "status": "WAITING_FOR_SYS_REQ_APPROVAL"
    },
    {
      "order": 3,
      "formCode": "DB_REQ",
      "formName": "資料庫申請表",
      "status": "WAITING_FOR_SYS_REQ_APPROVAL"
    },
    {
      "order": 4,
      "formCode": "FIREWALL_REQ",
      "formName": "防火牆申請表",
      "status": "WAITING_FOR_HOST_AND_DB_INFO"
    },
    {
      "order": 5,
      "formCode": "WAF_REQ",
      "formName": "WAF 申請表",
      "status": "WAITING_FOR_DOMAIN_INFO"
    },
    {
      "order": 6,
      "formCode": "SECURITY_REVIEW",
      "formName": "資安檢核表",
      "status": "WAITING_FOR_BASIC_INFO"
    },
    {
      "order": 7,
      "formCode": "WEAK_SCAN",
      "formName": "弱點掃描申請表",
      "status": "WAITING_FOR_DEPLOYMENT_INFO"
    },
    {
      "order": 8,
      "formCode": "GO_LIVE",
      "formName": "上線審核表",
      "status": "WAITING_FOR_ALL_PREVIOUS_FORMS"
    }
  ]
}
```

### 9.3 check_missing_fields

檢查某一張表單目前缺少哪些資料。

#### 輸入範例

```json
{
  "taskId": "TASK-20260705-001",
  "formCode": "VM_REQ"
}
```

#### 輸出範例

```json
{
  "formCode": "VM_REQ",
  "formName": "主機資源申請表",
  "missingFields": [
    "environment",
    "cpu",
    "memoryGb",
    "diskGb",
    "os",
    "purpose"
  ]
}
```

### 9.4 create_form_draft

呼叫 Playwright Worker 建立表單草稿。

#### 輸入範例

```json
{
  "taskId": "TASK-20260705-001",
  "formCode": "SYS_REQ",
  "input": {
    "systemName": "APLY",
    "externalFacing": true,
    "needDatabase": true,
    "databaseType": "PostgreSQL",
    "hasPersonalData": true,
    "expectedGoLiveDate": "2026-09-30"
  }
}
```

#### 輸出範例

```json
{
  "status": "DRAFT_CREATED",
  "formCode": "SYS_REQ",
  "formName": "系統需求申請表",
  "draftNo": "SYS-REQ-20260705-001",
  "screenshotUrl": "https://demo.local/screenshots/SYS-REQ-20260705-001.png",
  "dashboardUrl": "https://demo.local/tasks/TASK-20260705-001"
}
```

### 9.5 submit_form_after_approval

使用者確認後送出表單。

#### 輸入範例

```json
{
  "taskId": "TASK-20260705-001",
  "formCode": "SYS_REQ",
  "draftNo": "SYS-REQ-20260705-001"
}
```

#### 輸出範例

```json
{
  "status": "SUBMITTED",
  "formCode": "SYS_REQ",
  "formNo": "SYS-REQ-20260705-001",
  "submittedAt": "2026-07-05T16:00:00+08:00"
}
```

### 9.6 get_task_status

查詢目前任務進度。

#### 輸入範例

```json
{
  "taskId": "TASK-20260705-001"
}
```

#### 輸出範例

```json
{
  "taskId": "TASK-20260705-001",
  "goal": "建置新的對外系統",
  "currentStep": "等待系統需求申請核准",
  "completedForms": [],
  "inProgressForms": [
    {
      "formCode": "SYS_REQ",
      "formName": "系統需求申請表",
      "formNo": "SYS-REQ-20260705-001",
      "status": "SUBMITTED"
    }
  ],
  "nextAction": "等待 SYS_REQ 核准後，建立主機資源申請表草稿"
}
```

---

## 10. Agent Orchestrator 模組設計

### 10.1 Intent Parser

負責理解使用者自然語言目標。

#### 範例

使用者輸入：

```text
我要建置一個新的對外系統，會使用 PostgreSQL，也會處理個資。
```

Intent Parser 輸出：

```json
{
  "intent": "NEW_SYSTEM_SETUP",
  "entities": {
    "externalFacing": true,
    "needDatabase": true,
    "databaseType": "PostgreSQL",
    "hasPersonalData": true
  }
}
```

### 10.2 Process Planner

根據使用者目標，找出對應流程模板。

```json
{
  "processCode": "NEW_EXTERNAL_SYSTEM_SETUP",
  "processName": "新對外系統建置流程"
}
```

### 10.3 Dependency Resolver

判斷表單之間的前後關係。

```text
系統需求申請表
  ↓ 產生 systemCode
主機資源申請表
  ↓ 產生 hostIP
資料庫申請表
  ↓ 產生 dbName / dbIP
防火牆申請表
  ↓ 需要 hostIP + dbIP
WAF 申請表
  ↓ 需要 domainName + hostIP
弱點掃描申請表
  ↓ 需要 URL / IP
上線審核表
  ↓ 需要所有前置單號
```

### 10.4 Missing Info Checker

檢查執行某張表單前，資料是否完整。

例如主機申請表需要：

```json
{
  "requiredFields": [
    "systemName",
    "systemCode",
    "environment",
    "cpu",
    "memoryGb",
    "diskGb",
    "os",
    "purpose"
  ]
}
```

如果缺少資料，Agent 應該先問使用者，而不是直接執行。

### 10.5 Execution Engine

負責協調工具執行。

它會：

1. 確認表單是否可執行。
2. 檢查缺漏欄位。
3. 呼叫對應 Playwright Worker。
4. 接收執行結果。
5. 更新任務狀態。
6. 寫入 Audit Log。
7. 回覆 Agent 下一步。

### 10.6 Approval Guard

負責人工確認與風險控管。

| 風險等級 | 執行策略 |
|---|---|
| Low | 可自動建立草稿 |
| Medium | 建立草稿後需要人工確認才能送出 |
| High | 只允許建立草稿，不允許自動送出 |
| Critical | 禁止 Agent 執行，只能提供填寫建議 |

例如：

- 系統需求申請表：可建立草稿，確認後送出。
- 主機資源申請表：可建立草稿，確認後送出。
- 防火牆申請表：需要人工確認。
- 上線審核表：高風險，只允許草稿。
- 權限申請表：高風險，只允許草稿或禁止自動送出。

### 10.7 Form Tool Registry

管理所有表單工具。

```json
[
  {
    "formCode": "SYS_REQ",
    "formName": "系統需求申請表",
    "toolName": "createSystemRequirementDraft",
    "riskLevel": "MEDIUM",
    "executionMode": "CONFIRM_BEFORE_SUBMIT"
  },
  {
    "formCode": "VM_REQ",
    "formName": "主機資源申請表",
    "toolName": "createVmRequestDraft",
    "riskLevel": "MEDIUM",
    "executionMode": "CONFIRM_BEFORE_SUBMIT"
  },
  {
    "formCode": "FIREWALL_REQ",
    "formName": "防火牆申請表",
    "toolName": "createFirewallRequestDraft",
    "riskLevel": "HIGH",
    "executionMode": "CREATE_DRAFT_ONLY"
  },
  {
    "formCode": "GO_LIVE",
    "formName": "上線審核表",
    "toolName": "createGoLiveReviewDraft",
    "riskLevel": "HIGH",
    "executionMode": "CREATE_DRAFT_ONLY"
  }
]
```

### 10.8 Status Tracker

負責追蹤每張表單目前狀態。

常見狀態：

```text
NOT_STARTED
READY
WAITING_FOR_INPUT
DRAFT_CREATED
WAITING_FOR_USER_CONFIRMATION
SUBMITTED
IN_REVIEW
APPROVED
REJECTED
FAILED
SKIPPED
```

---

## 11. Playwright Worker 設計

### 11.1 設計原則

Playwright Worker 不應該讓 Agent 直接操作 UI 細節。

正確設計方式是：

- 每張表單一支工具。
- 每支工具有明確 input schema。
- 執行前做欄位驗證。
- 執行中截圖。
- 執行後回傳標準化結果。
- 錯誤時回傳錯誤原因與截圖。

### 11.2 每張表單一個 Tool

```text
createSystemRequirementDraft.ts
createVmRequestDraft.ts
createDatabaseRequestDraft.ts
createFirewallRequestDraft.ts
createWafRequestDraft.ts
createSecurityReviewDraft.ts
createWeakScanDraft.ts
createGoLiveReviewDraft.ts
```

### 11.3 Tool Input Schema 範例

#### 主機資源申請表

```ts
type VmRequestInput = {
  systemName: string;
  systemCode: string;
  environment: "DEV" | "TEST" | "STAG" | "PROD";
  cpu: number;
  memoryGb: number;
  diskGb: number;
  os: string;
  purpose: string;
};
```

### 11.4 Tool Output Schema 範例

```ts
type FormSubmitResult = {
  success: boolean;
  formCode: string;
  formName: string;
  status: "DRAFT_CREATED" | "SUBMITTED" | "FAILED";
  draftNo?: string;
  formNo?: string;
  screenshotPath?: string;
  screenshotUrl?: string;
  outputData?: Record<string, unknown>;
  errorReason?: string;
};
```

### 11.5 Playwright 執行流程

```text
1. 接收標準化 input
2. 檢查必填欄位
3. 開啟公司表單系統
4. 登入或沿用 session
5. 進入指定表單頁面
6. 填寫欄位
7. 上傳附件，若需要
8. 建立草稿或送出
9. 截圖
10. 取得表單單號
11. 回傳結果
12. 寫入 Audit Log
```

---

## 12. 流程知識庫設計

流程知識庫用來保存：

- SOP
- FAQ
- 流程模板
- 表單條件規則
- 表單前後關係
- 表單欄位定義
- 表單風險等級
- 角色與權責

### 12.1 流程模板範例

```json
{
  "processCode": "NEW_EXTERNAL_SYSTEM_SETUP",
  "processName": "新對外系統建置流程",
  "description": "用於建置新的對外服務系統",
  "steps": [
    {
      "order": 1,
      "formCode": "SYS_REQ",
      "formName": "系統需求申請表",
      "preconditions": [],
      "requiredInputs": [
        "systemName",
        "externalFacing",
        "needDatabase",
        "hasPersonalData"
      ],
      "outputFields": [
        "systemCode",
        "sysReqFormNo"
      ]
    },
    {
      "order": 2,
      "formCode": "VM_REQ",
      "formName": "主機資源申請表",
      "preconditions": [
        "SYS_REQ.APPROVED"
      ],
      "requiredInputs": [
        "systemCode",
        "environment",
        "cpu",
        "memoryGb",
        "diskGb",
        "os"
      ],
      "outputFields": [
        "hostIP",
        "vmFormNo"
      ]
    },
    {
      "order": 3,
      "formCode": "DB_REQ",
      "formName": "資料庫申請表",
      "conditions": [
        "needDatabase == true"
      ],
      "preconditions": [
        "SYS_REQ.APPROVED"
      ],
      "requiredInputs": [
        "systemCode",
        "databaseType",
        "databaseName"
      ],
      "outputFields": [
        "dbName",
        "dbIP",
        "dbFormNo"
      ]
    },
    {
      "order": 4,
      "formCode": "FIREWALL_REQ",
      "formName": "防火牆申請表",
      "conditions": [
        "externalFacing == true || needCrossNetwork == true"
      ],
      "preconditions": [
        "VM_REQ.APPROVED"
      ],
      "requiredInputs": [
        "sourceIP",
        "targetIP",
        "port",
        "protocol"
      ],
      "outputFields": [
        "firewallFormNo"
      ]
    },
    {
      "order": 5,
      "formCode": "WAF_REQ",
      "formName": "WAF 申請表",
      "conditions": [
        "externalFacing == true"
      ],
      "preconditions": [
        "VM_REQ.APPROVED"
      ],
      "requiredInputs": [
        "domainName",
        "hostIP"
      ],
      "outputFields": [
        "wafFormNo"
      ]
    }
  ]
}
```

---

## 13. 任務狀態資料設計

### 13.1 TASK_INSTANCE

保存任務主檔。

```sql
CREATE TABLE task_instance (
    task_id VARCHAR(64) PRIMARY KEY,
    requester VARCHAR(64),
    goal TEXT,
    process_code VARCHAR(64),
    current_step VARCHAR(64),
    status VARCHAR(32),
    dashboard_url TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 13.2 TASK_STEP_STATUS

保存每張表單狀態。

```sql
CREATE TABLE task_step_status (
    id BIGSERIAL PRIMARY KEY,
    task_id VARCHAR(64),
    step_order INT,
    form_code VARCHAR(64),
    form_name VARCHAR(128),
    status VARCHAR(32),
    draft_no VARCHAR(128),
    form_no VARCHAR(128),
    submitted_at TIMESTAMP,
    approved_at TIMESTAMP,
    output_data JSONB,
    error_reason TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### 13.3 AUDIT_LOG

保存執行紀錄。

```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    task_id VARCHAR(64),
    form_code VARCHAR(64),
    actor VARCHAR(64),
    action VARCHAR(64),
    input_data JSONB,
    output_data JSONB,
    screenshot_url TEXT,
    success BOOLEAN,
    error_reason TEXT,
    created_at TIMESTAMP
);
```

---

## 14. Web Dashboard 功能設計

Dashboard 主要給人看，讓整個 Agent 執行過程可視化、可確認、可追蹤。

### 14.1 Dashboard 頁面內容

#### 任務摘要

顯示：

- 任務 ID
- 使用者目標
- 流程類型
- 目前狀態
- 下一步建議

#### 流程節點圖

顯示每張表單：

```text
系統需求申請表 → 主機資源申請表 → 資料庫申請表 → 防火牆申請表 → WAF 申請表 → 弱點掃描申請表 → 上線審核表
```

每個節點顯示狀態：

- 未開始
- 可執行
- 缺少資料
- 草稿已建立
- 等待確認
- 已送出
- 審核中
- 已核准
- 失敗

#### 表單執行紀錄

顯示：

- 表單名稱
- 執行時間
- 執行結果
- 表單單號
- 截圖
- 錯誤訊息

#### 人工確認區

顯示：

- 即將送出的表單摘要
- 輸入資料
- 風險提示
- 確認送出按鈕
- 取消按鈕

---

## 15. 安全與治理設計

因為本專案會接觸公司表單系統，因此必須強調安全與可控性。

### 15.1 工具白名單

Agent 只能呼叫 MCP Server 暴露的工具。

禁止 Agent 任意操作瀏覽器或執行任意腳本。

### 15.2 Schema 驗證

每張表單 Tool 都必須定義 input schema。

資料不完整或格式錯誤時，不允許執行。

### 15.3 人工確認

送出表單前必須由使用者確認。

高風險表單只允許建立草稿。

### 15.4 高風險表單限制

例如：

- 上線審核表
- 權限申請表
- 資安例外申請表
- 防火牆特殊開通申請
- 涉及生產環境異動的表單

這些表單不應由 Agent 自動送出，只能建立草稿或提供填寫建議。

### 15.5 Audit Log

所有操作都需要記錄：

- 操作者
- 任務 ID
- 表單代碼
- 執行時間
- 輸入參數
- 執行結果
- 截圖
- 表單單號
- 是否人工確認
- 錯誤原因

---

## 16. MVP 範圍

黑客松建議不要一開始就做十張表單，應該先選三張代表性表單，展示完整流程。

### MVP 表單範圍

建議選：

1. 系統需求申請表
2. 主機資源申請表
3. 防火牆申請表

原因：

- 有明確順序。
- 有前後資料依賴。
- 可以展示流程規劃。
- 可以展示 Playwright 自動填表。
- 可以展示狀態追蹤。
- 可以展示人工確認。
- Demo 成功率高。

---

## 17. MVP Demo 劇本

### Step 1：使用者提出目標

```text
我要建置一個新的對外系統，系統名稱叫 APLY，會使用 PostgreSQL，也會處理個資。
```

### Step 2：Agent 判斷流程

Agent 回覆：

```text
我判斷你要進行「新對外系統建置流程」。

根據目前資訊，你可能需要以下表單：

1. 系統需求申請表
2. 主機資源申請表
3. 資料庫申請表
4. 防火牆申請表
5. WAF 申請表
6. 資安檢核表
7. 弱點掃描申請表
8. 上線審核表

目前第一步是「系統需求申請表」。
```

### Step 3：Agent 檢查缺漏資料

Agent 回覆：

```text
建立系統需求申請表前，還需要以下資料：

- 預計上線日期
- 系統負責人
- 業務窗口
- 是否已有系統代號
```

### Step 4：使用者補充資料

```text
預計 9 月底上線，系統負責人是我，業務窗口是業務一部，目前還沒有系統代號。
```

### Step 5：Agent 建立任務與表單計畫

Agent 呼叫 MCP：

```text
create_task
plan_forms
check_missing_fields
```

Dashboard 顯示流程圖與任務狀態。

### Step 6：Agent 呼叫 Playwright 建立草稿

Agent 呼叫 MCP：

```text
create_form_draft(formCode = SYS_REQ)
```

Playwright Worker 執行：

```text
1. 開啟公司表單系統
2. 登入
3. 選擇系統需求申請表
4. 填寫系統名稱、是否對外、是否有 DB、是否有個資
5. 儲存草稿
6. 截圖
7. 回傳草稿單號
```

### Step 7：Agent 回覆結果

```text
已建立「系統需求申請表」草稿。

草稿單號：SYS-REQ-20260705-001  
狀態：等待人工確認  
Dashboard：請至任務頁面確認表單內容與截圖。

確認送出後，下一步會等待系統需求申請核准。
核准後，系統會建議建立「主機資源申請表」。
```

### Step 8：Dashboard 人工確認

使用者在 Dashboard 查看：

- 表單內容
- Playwright 截圖
- 輸入資料
- 風險提示

確認後點擊「送出」。

### Step 9：更新任務狀態

任務狀態更新為：

```text
系統需求申請表：已送出
主機資源申請表：等待系統需求核准
防火牆申請表：等待主機資訊
```

Agent 回覆：

```text
目前系統需求申請表已送出，正在等待審核。

下一步：
待系統需求申請核准並取得 systemCode 後，可以建立主機資源申請表草稿。
```

---

## 18. 開發計畫

### Phase 1：流程知識建模

目標：

- 定義新系統建置流程。
- 定義表單清單。
- 定義表單順序。
- 定義條件規則。
- 定義每張表單 required inputs 與 outputs。

產出：

- process-template.json
- form-registry.json
- form-field-schema.json

### Phase 2：MCP Server Prototype

目標：

建立 MCP Server，提供 Agent 可呼叫的工具。

工具包含：

- create_task
- plan_forms
- check_missing_fields
- create_form_draft
- get_task_status

產出：

- MCP Server
- Tool Schema
- Tool Handler
- Mock Response

### Phase 3：Playwright Worker

目標：

針對 MVP 表單建立 Playwright 腳本。

優先實作：

1. 系統需求申請表
2. 主機資源申請表
3. 防火牆申請表

功能包含：

- 開啟表單系統
- 填寫表單欄位
- 建立草稿
- 截圖
- 回傳草稿單號
- 錯誤處理

### Phase 4：任務狀態與 Audit Log

目標：

建立任務狀態追蹤。

功能包含：

- 建立任務
- 更新表單狀態
- 保存表單單號
- 保存前一步輸出
- 保存執行紀錄
- 保存錯誤訊息與截圖

### Phase 5：Web Dashboard

目標：

提供人可查看與確認的介面。

功能包含：

- 任務列表
- 任務詳細頁
- 流程節點圖
- 表單狀態
- 截圖檢視
- 表單送出確認
- Audit Log 檢視

### Phase 6：Demo 整合

目標：

完成一條完整 Demo 流程。

```text
使用者描述目標
  ↓
Agent 判斷流程
  ↓
Agent 產生表單計畫
  ↓
Agent 檢查缺漏資料
  ↓
Agent 呼叫 MCP Tool
  ↓
Playwright 建立表單草稿
  ↓
Dashboard 顯示結果
  ↓
使用者確認送出
  ↓
Agent 更新狀態並推薦下一步
```

---

## 19. 建議技術選型

### 黑客松快速版

| 模組 | 技術 |
|---|---|
| MCP Server | Node.js / TypeScript |
| Playwright Worker | Playwright + TypeScript |
| API Server | Express / Fastify |
| Dashboard | React / Next.js |
| 狀態儲存 | SQLite / JSON File |
| 流程知識庫 | JSON / YAML |
| 截圖儲存 | Local File System |

### 企業正式版

| 模組 | 技術 |
|---|---|
| Agent Gateway | MCP Server |
| 後端服務 | Spring Boot / Node.js |
| Playwright Worker | Node.js / TypeScript |
| 任務狀態庫 | PostgreSQL |
| 流程知識庫 | PostgreSQL + Vector DB |
| Dashboard | React / Vue / Angular |
| Audit Log | PostgreSQL / ELK |
| 權限控管 | SSO / AD / OAuth |
| 部署 | Kubernetes / VM / Container |

---

## 20. Repository 建議結構

```text
formops-agent/
├── README.md
├── docs/
│   ├── architecture.md
│   ├── demo-script.md
│   ├── process-model.md
│   └── security-governance.md
├── mcp-server/
│   ├── src/
│   │   ├── tools/
│   │   │   ├── createTask.ts
│   │   │   ├── planForms.ts
│   │   │   ├── checkMissingFields.ts
│   │   │   ├── createFormDraft.ts
│   │   │   └── getTaskStatus.ts
│   │   ├── services/
│   │   │   ├── ProcessKnowledgeService.ts
│   │   │   ├── TaskStateService.ts
│   │   │   ├── FormToolRegistry.ts
│   │   │   ├── ExecutionEngine.ts
│   │   │   └── ApprovalGuard.ts
│   │   └── index.ts
│   └── package.json
├── playwright-workers/
│   ├── src/
│   │   ├── forms/
│   │   │   ├── createSystemRequirementDraft.ts
│   │   │   ├── createVmRequestDraft.ts
│   │   │   ├── createDatabaseRequestDraft.ts
│   │   │   └── createFirewallRequestDraft.ts
│   │   ├── common/
│   │   │   ├── login.ts
│   │   │   ├── screenshot.ts
│   │   │   └── validation.ts
│   │   └── index.ts
│   └── package.json
├── dashboard/
│   ├── src/
│   │   ├── pages/
│   │   ├── components/
│   │   └── api/
│   └── package.json
├── config/
│   ├── process-template.json
│   ├── form-registry.json
│   └── form-schema.json
└── data/
    ├── tasks.json
    ├── audit-log.json
    └── screenshots/
```

---

## 21. 專案亮點

### 21.1 目標導向

使用者不需要知道表單名稱，只要說出想完成的事情。

### 21.2 流程推理

Agent 根據流程知識庫判斷表單順序、條件與前置關係。

### 21.3 表單工具化

每張表單都封裝成 Playwright Tool，讓 Agent 可以受控呼叫。

### 21.4 資料串接

前一張表單產出的資料，例如系統代號、主機 IP、資料庫名稱、表單單號，可以帶入下一張表單。

### 21.5 人工確認

高風險操作需要人工確認，避免 AI 自動誤送。

### 21.6 狀態追蹤

使用者可以隨時詢問：

```text
我現在到哪一步了？
下一步該做什麼？
哪張表單還沒完成？
哪張表單卡住了？
```

### 21.7 可擴充

未來只要新增：

- 流程模板
- 表單欄位 Schema
- Playwright Tool
- 風險規則

就可以支援更多公司流程。

---

## 22. 評審可能會問的問題與回答

### Q1：這跟一般 Chatbot 有什麼不同？

一般 Chatbot 只能回答問題。

本專案可以：

- 判斷流程。
- 規劃表單。
- 檢查缺漏資料。
- 呼叫工具建立草稿。
- 追蹤表單狀態。
- 推薦下一步。

所以它不是單純問答，而是具備行動能力的流程執行 Agent。

### Q2：這跟 RPA 有什麼不同？

RPA 是固定流程自動化。

本專案是：

- 先理解使用者目標。
- 再判斷需要什麼流程。
- 再決定要呼叫哪些表單工具。
- 最後才由 Playwright 執行。

也就是：

> RPA 解決操作自動化；本專案解決流程決策加操作自動化。

### Q3：AI 會不會亂送表單？

不會。

因為系統設計了以下保護：

1. 工具白名單。
2. Schema 驗證。
3. 人工確認。
4. 高風險表單僅允許草稿。
5. 完整 Audit Log。
6. Agent 不直接操作 UI，只能呼叫受控 MCP Tool。

### Q4：為什麼需要 MCP Server？

MCP Server 是 Agent 與公司內部工具之間的標準化工具層。

它可以：

- 把公司表單能力包裝成 Agent 可呼叫工具。
- 限制 Agent 只能呼叫被允許的功能。
- 定義清楚的 input/output schema。
- 讓 Codex 或其他 Agent 能用一致方式調用表單能力。
- 將表單系統、Playwright、狀態追蹤與 Audit Log 串在一起。

### Q5：為什麼不要讓 Codex 直接跑 Playwright？

因為直接讓 Codex 操作 Playwright 風險較高。

可能問題包含：

- UI 操作失控。
- 欄位填錯。
- 缺少權限控管。
- 難以稽核。
- 無法統一驗證輸入。
- 表單送出責任不清楚。

因此本專案採用：

```text
Codex / Agent
  ↓
MCP Server 高階業務工具
  ↓
Playwright Worker
  ↓
公司表單系統
```

讓 Codex 只負責判斷與規劃，Playwright 只負責表單操作。

---

## 23. 成功指標

### Demo 成功指標

- Agent 能正確理解「新系統建置」目標。
- Agent 能列出正確表單清單。
- Agent 能判斷表單順序。
- Agent 能檢查缺漏資料。
- Agent 能呼叫 Playwright 建立至少一張表單草稿。
- Dashboard 能顯示任務狀態與截圖。
- 使用者能確認送出。
- Agent 能更新狀態並推薦下一步。

### 長期價值指標

- 減少新人詢問流程的時間。
- 減少表單漏提或順序錯誤。
- 減少表單填寫錯誤。
- 提高跨部門流程透明度。
- 提升內部流程自動化成熟度。
- 建立可擴充的企業 Agent 工具平台。

---

## 24. 最終簡報結論

本專案的價值不只是讓 AI 幫忙填表，而是把公司內部隱性的流程經驗顯性化、工具化、可執行化。

過去只有資深同仁知道：

- 下一步要找誰。
- 該提哪張表。
- 哪張表要先核准。
- 哪些資料會被下一張表使用。
- 哪些表單不能自動送出。

現在透過 FormOps Agent，使用者只要描述目標，Agent 就能：

1. 判斷流程。
2. 規劃表單。
3. 檢查資料。
4. 呼叫工具。
5. 建立草稿。
6. 追蹤狀態。
7. 推薦下一步。

最終目標是：

> 讓複雜企業流程從「問人才知道」變成「問 Agent 就知道」，並進一步從「知道下一步」升級為「協助完成下一步」。

---

## 25. 簡報版摘要

```text
企業表單流程執行 Agent

痛點：
公司內部流程複雜，使用者不知道該提哪張表、何時提、缺什麼資料、下一步是什麼。

解法：
使用者以自然語言描述目標，Agent 透過流程知識庫判斷表單流程，並透過 MCP Server 呼叫 Playwright Tool 協助建立表單草稿或送出。

架構：
Chat UI / Codex / ChatGPT
→ FormOps MCP Server
→ Agent Orchestrator
→ Playwright Workers
→ 公司表單系統
→ 任務狀態庫 / Dashboard / Audit Log

亮點：
1. 目標導向
2. 流程推理
3. 表單工具化
4. 資料串接
5. 人工確認
6. 狀態追蹤
7. 完整稽核
8. 可擴充到更多公司流程

MVP：
先支援新系統建置流程中的三張代表性表單：
1. 系統需求申請表
2. 主機資源申請表
3. 防火牆申請表

一句話：
讓使用者只要說出目標，Agent 就能規劃流程、判斷表單、檢查資料，並呼叫 Playwright 工具協助完成公司表單申請。
```
