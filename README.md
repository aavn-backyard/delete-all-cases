# delete-all-cases

## Introduction

An simple RESTful API deployed into an Axon.ivy application for destroying or deleting `ITask` and `ICase`.

## Usage

```
$> curl -u username:password http://localhost:8081/ivy/api/designer/root/cases
There are 18'159 existing cases

$> curl -u username:password http://localhost:8081/ivy/api/designer/root/cases?running=true
There are 594 RUNNING cases

$> curl -u username:password -X DELETE http://localhost:8081/ivy/api/designer/root/cases
{"errorId":"157420B156ABC4A0","errorMessage":"User must have role DeleteAllCasesExecutor","statusCode":401}

$> curl -u username:password -X POST http://localhost:8081/ivy/api/designer/root/cases/234/destroy
Case 234 has been destroyed

$> curl -u username:password -X POST http://localhost:8081/ivy/api/designer/root/cases/destroy
594 running cases has been destroyed
```
