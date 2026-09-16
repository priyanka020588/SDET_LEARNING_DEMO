# Jira Create Tickets — Examples

## Example 1: Single ticket

**User input:**
```
Create a Jira ticket:
- Summary: Reverse a String
- Description: Reverse a given string without using built-in reverse methods.
  Test case: Input = "hello". Expected output = "olleh".
- Labels: topic-string, level-beginner, programming-practice
- Epic: prog
```

**Agent actions:**
1. Search for Epic with summary `prog` → finds `SDET-2`
2. Create Task with labels and `epicKey: SDET-2`
3. Return `SDET-11` (or next available key)

---

## Example 2: Batch tickets (15 tasks under one Epic)

**User input:**
```
Epic: prog
Create these tasks with labels and descriptions:

1. Title: Linear Search in an Array
   Labels: topic-array, level-beginner, programming-practice
   Description: Search an array linearly...
   Test case: Input array = [4, 2, 7, 1, 9, 3], target = 9. Expected output = index 4.

2. Title: Binary Search in a Sorted Array
   ...
```

**Agent actions:**
1. Resolve Epic `prog` → `SDET-2`
2. Parse all 15 items
3. Create all Tasks in parallel with shared `epicKey`
4. Group results by topic label in the summary table

---

## Example 3: Epic key provided directly

**User input:**
```
Summary: Two Sum Problem
Description: Using a hashmap, find indices of two numbers...
Test case: Input array = [2, 7, 11, 15], target = 9. Expected output = [0, 1].
Labels: topic-hashmap, level-beginner, programming-practice
Epic: SDET-2
```

**Agent actions:**
1. Skip Epic search (key already known)
2. Create one Task linked to `SDET-2`

---

## Example 4: Create Epic + child tickets

**User input:**
```
Create an Epic named "automation-suite" and 3 tasks under it:
1. ...
2. ...
3. ...
```

**Agent actions:**
1. `jira_create_issue` with `issue_type: Epic`, `summary: automation-suite`
2. Use returned key (e.g. `SDET-18`) as `epicKey` for all child Tasks

---

## Example 5: Minimal prompt (skill infers defaults)

**User input:**
```
Add a ticket to prog:
Title: Count Vowels in a String
Labels: topic-string, level-beginner, programming-practice
Description: Count vowels in a string (a, e, i, o, u).
Test case: Input = "hello". Expected output = 2.
```

**Agent actions:**
1. Infer project `SDET`, issue type `Task`
2. Resolve Epic `prog`
3. Create and confirm
