#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${1:?Usage: ./burst.sh BASE_URL SHOW_ID [COUNT]}"
SHOW_ID="${2:?Usage: ./burst.sh BASE_URL SHOW_ID [COUNT]}"
COUNT="${3:-500}"
TMP="$(mktemp)"
trap 'rm -f "$TMP"' EXIT
seq "$COUNT" | xargs -P "$COUNT" -I{} curl -sS -o /dev/null -w '%{http_code}\n' \
  -X POST "$BASE_URL/api/v1/shows/$SHOW_ID/reserve" \
  -H "Authorization: Bearer user-{}" -H 'Content-Type: application/json' \
  -d '{"seats":["A12"],"idempotency_key":"hot-seat-{}"}' >> "$TMP"
printf 'Outcome distribution:\n'; sort "$TMP" | uniq -c
printf '\nFinal state:\n'; curl -sS "$BASE_URL/api/v1/shows/$SHOW_ID"
