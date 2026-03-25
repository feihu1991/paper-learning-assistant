#!/bin/bash
# 自动监控 GitHub Actions 构建并获取错误日志
# 用法：./auto-monitor.sh <repo> <token>

set -e

REPO="${1:-feihu1991/paper-learning-assistant}"
TOKEN="${2:-}"
POLL_INTERVAL="${3:-30}"
MAX_ATTEMPTS="${4:-60}"

if [ -z "$TOKEN" ]; then
    echo "⚠️  Warning: No token provided"
    AUTH_HEADER=""
else
    AUTH_HEADER="-H \"Authorization: token $TOKEN\""
fi

echo "🔍 Auto-monitoring: $REPO"
echo "Poll interval: ${POLL_INTERVAL}s | Max attempts: $MAX_ATTEMPTS"
echo "=========================================="

LAST_RUN_ID=""
LAST_STATUS=""

get_latest_run() {
    curl -sL $AUTH_HEADER "https://api.github.com/repos/$REPO/actions/runs?per_page=1" 2>/dev/null
}

get_run_details() {
    local run_id=$1
    curl -sL $AUTH_HEADER "https://api.github.com/repos/$REPO/actions/runs/$run_id" 2>/dev/null
}

download_logs() {
    local run_id=$1
    echo -e "\n📥 Downloading logs for run #$run_id..."
    
    for i in 1 2 3; do
        if curl -sL $AUTH_HEADER \
            "https://api.github.com/repos/$REPO/actions/runs/$run_id/logs" \
            -o "/tmp/build-$run_id.log" 2>/dev/null && [ -s "/tmp/build-$run_id.log" ]; then
            echo "✅ Logs downloaded: /tmp/build-$run_id.log"
            
            # Extract and show errors
            echo -e "\n📋 === ERROR SUMMARY ==="
            grep -i "error\|failed\|exception" "/tmp/build-$run_id.log" 2>/dev/null | tail -30 || echo "No obvious errors found"
            
            echo -e "\n📋 === LAST 50 LINES ==="
            tail -50 "/tmp/build-$run_id.log"
            
            return 0
        else
            echo "   Attempt $i failed, retrying in $((i*10))s..."
            sleep $((i*10))
        fi
    done
    
    echo "❌ Failed to download logs after 3 attempts"
    return 1
}

download_artifacts() {
    local run_id=$1
    echo -e "\n📦 Checking artifacts..."
    
    ARTIFACTS=$(curl -sL $AUTH_HEADER \
        "https://api.github.com/repos/$REPO/actions/runs/$run_id/artifacts" 2>/dev/null || echo '{"artifacts":[]}')
    
    ARTIFACT_COUNT=$(echo "$ARTIFACTS" | jq -r '.artifacts | length')
    
    if [ "$ARTIFACT_COUNT" -gt 0 ]; then
        echo "   Found $ARTIFACT_COUNT artifact(s):"
        echo "$ARTIFACTS" | jq -r '.artifacts[] | "   - \(.name) (\(.size_in_bytes) bytes, expires: \(.expired_at))"'
    else
        echo "   No artifacts found"
    fi
}

ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    ATTEMPT=$((ATTEMPT + 1))
    TIMESTAMP=$(date '+%H:%M:%S')
    
    RESPONSE=$(get_latest_run)
    RUN_ID=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].id // empty')
    RUN_NAME=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].name // empty')
    STATUS=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].status // empty')
    CONCLUSION=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].conclusion // empty')
    HTML_URL=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].html_url // empty')
    
    if [ -z "$RUN_ID" ]; then
        echo "[$TIMESTAMP] No workflow runs found yet..."
        sleep $POLL_INTERVAL
        continue
    fi
    
    # New run detected
    if [ "$RUN_ID" != "$LAST_RUN_ID" ]; then
        LAST_RUN_ID="$RUN_ID"
        echo -e "\n[$TIMESTAMP] 🆕 New run detected: #$RUN_ID - $RUN_NAME"
        echo "   URL: $HTML_URL"
        LAST_STATUS=""
    fi
    
    # Status changed
    if [ "$STATUS" != "$LAST_STATUS" ]; then
        LAST_STATUS="$STATUS"
        echo -e "\n[$TIMESTAMP] 📊 Status: $STATUS ${CONCLUSION:+- $CONCLUSION}"
    fi
    
    # Completed
    if [ "$STATUS" = "completed" ]; then
        echo -e "\n=========================================="
        if [ "$CONCLUSION" = "success" ]; then
            echo "✅ BUILD SUCCESSFUL!"
            download_artifacts "$RUN_ID"
            echo "=========================================="
            exit 0
        else
            echo "❌ BUILD FAILED!"
            echo "   Conclusion: $CONCLUSION"
            echo "=========================================="
            download_logs "$RUN_ID"
            download_artifacts "$RUN_ID"
            exit 1
        fi
    fi
    
    sleep $POLL_INTERVAL
done

echo -e "\n⏱️  Timeout after $MAX_ATTEMPTS attempts"
exit 2
