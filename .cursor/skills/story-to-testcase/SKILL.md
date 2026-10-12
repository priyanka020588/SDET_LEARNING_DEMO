---
name: story-to-testcase
description: >-
input Jira issue key(MCP)
positive/negative/boundary test cases
smoke vs regression, manual test
No automation/do not write any code until aproval on testlist
---

## Planning step of SDLC 
Create a list of test cases in accordance to the input, include test cases of positive/negative/boundary analysis
**No automation until the test list is approved.**

## Prerequisites
- jira ticket


## Input the user provides
-- User will give Jira issue key(MCP) 
-- will paste Acceptance Criteria

## Workflow
STEP 1 [] Read the user provided Jira ticket and understand the scenario/user requirement
STEP 2 [] List test cases in accordance to the expected behaviour of the application, test cases to be categorized as Positive, Negative and Edge Cases
STEP 3 [] Mark each test case - Smoke vs Regression
STEP 4 [] Check which all test cases to be automated
STEP 5 [] STOP and WAIT for the human approval and do not create any Java code or test classes (in the framework) without human approval


## Rules:

-- STOP and WAIT for the human approval before committing any changes in the actual framework
-- Do not create/add any test file in the framework. Only produce the list of possible test cases categorised as positive, negative and Edge cases
-- Do not call any code generation skill or tool without human approval such as jira-create-tickets
-- Do not need any Java code, only the scenarios under mentioned categories
-- Do not commit anything as part of this ticket

### Step 5: Summarize results
Create a summary table of the output which includes the test scenario along with its categorization, suite details and automate/manual detail

## Output Table 
Test Scenario | Category | Suite | Automate/Manual tag


Include a Jira browse link when possible: `https://etltester.atlassian.net/browse/<KEY>`

## Verification
- Table has positive, negative and boundary rows. 
- Every row has suite + automate/manual
- User has approved the list
- No Java files or new Jira ticket has been created

## Error handling
-- Any error, wait and report to human along with details and wait for the approval before proceeding








