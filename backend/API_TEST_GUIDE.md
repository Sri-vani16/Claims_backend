# Job Monitoring API Test Guide

## Available Endpoints

### 1. Get All Jobs
```
GET http://localhost:8080/api/monitoring/jobs
```
**Purpose:** List all jobs to get actual job IDs for testing

### 2. Get Failed Jobs
```
GET http://localhost:8080/api/monitoring/failed?hours=24
```
**Purpose:** Get failed jobs in last 24 hours with error details from logs

### 3. Get Job Status
```
GET http://localhost:8080/api/monitoring/status/{jobId}
```
**Purpose:** Get detailed status of a specific job
**Example:** `GET http://localhost:8080/api/monitoring/status/7ceaf84c-29f1-42e9-aef8-7357f5121103`

### 4. Rerun Job
```
POST http://localhost:8080/api/monitoring/rerun/{jobId}
```
**Purpose:** Mark a job for rerun
**Example:** `POST http://localhost:8080/api/monitoring/rerun/7ceaf84c-29f1-42e9-aef8-7357f5121103`

### 5. Assign Job
```
POST http://localhost:8080/api/monitoring/assign/{jobId}
Content-Type: application/json

{
  "assignee": "john.doe@company.com"
}
```

### 6. System Health
```
GET http://localhost:8080/api/monitoring/health
```

## Bot Endpoints

### 7. Bot Query
```
POST http://localhost:8080/api/bot/query
Content-Type: application/json

{
  "query": "list all failed jobs in last 24 hours"
}
```

**Other bot queries:**
- `"show me failed jobs"`
- `"get status of job 7ceaf84c-29f1-42e9-aef8-7357f5121103"`
- `"rerun job 7ceaf84c-29f1-42e9-aef8-7357f5121103"`

### 8. Bot Help
```
GET http://localhost:8080/api/bot/help
```

## Testing Steps

1. **Start the application** - This will create sample jobs via JobDataInitializer
2. **Get all jobs** - `GET /api/monitoring/jobs` to see created job IDs
3. **Test failed jobs** - `GET /api/monitoring/failed?hours=24` to see error details
4. **Test bot** - Use actual job IDs from step 2 in bot queries
5. **Test other endpoints** - Use job IDs for status, rerun, assign operations

## Expected Results

- Failed jobs should show detailed error logs with stack traces
- Bot should respond with formatted job information
- All CRUD operations should work with actual job IDs