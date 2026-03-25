#!/bin/bash
# GitHub Actions Workflow Monitor
# 自动监控构建状态，每 30 秒检查一次

set -e

REPO="${1:-feihu1991/paper-learning-assistant}"
TOKEN="${2:-}"
MAX_ATTEMPTS="${3:-20}"  # 最多检查 20 次（10 分钟）

if [ -z "$TOKEN" ]; then
    echo "⚠️  Warning: No token provided, may hit rate limits"
    AUTH_HEADER=""
else
    AUTH_HEADER="-H \"Authorization: token $TOKEN\""
fi

echo "🔍 Monitoring workflow runs for $REPO"
echo "Max attempts: $MAX_ATTEMPTS (every 30s)"
echo "=========================================="

ATTEMPT=0
LAST_STATUS=""
LAST_CONCLUSION=""

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    ATTEMPT=$((ATTEMPT + 1))
    TIMESTAMP=$(date '+%H:%M:%S')
    
    echo -e "\n[$TIMESTAMP] Attempt $ATTEMPT/$MAX_ATTEMPTS"
    
    # Get latest workflow run
    RESPONSE=$(curl -sL $AUTH_HEADER \
        "https://api.github.com/repos/$REPO/actions/runs?per_page=1" 2>/dev/null || echo '{"workflow_runs":[]}')
    
    RUN_ID=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].id // empty')
    RUN_NAME=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].name // empty')
    STATUS=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].status // empty')
    CONCLUSION=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].conclusion // empty')
    HTML_URL=$(echo "$RESPONSE" | jq -r '.workflow_runs[0].html_url // empty')
    
    if [ -z "$RUN_ID" ]; then
        echo "❌ No workflow runs found"
        sleep 30
        continue
    fi
    
    # Check if status changed
    if [ "$STATUS" != "$LAST_STATUS" ] || [ "$CONCLUSION" != "$LAST_CONCLUSION" ]; then
        LAST_STATUS="$STATUS"
        LAST_CONCLUSION="$CONCLUSION"
        
        echo "📊 Status Update:"
        echo "   Run: $RUN_NAME #$RUN_ID"
        echo "   Status: $STATUS"
        echo "   Conclusion: ${CONCLUSION:-N/A}"
        echo "   URL: $HTML_URL"
        
        # Check if completed
        if [ "$STATUS" = "completed" ]; then
            echo -e "\n=========================================="
            if [ "$CONCLUSION" = "success" ]; then
                echo "✅ BUILD SUCCESSFUL!"
                echo "=========================================="
                
                # Download artifacts
                echo -e "\n📦 Checking for artifacts..."
                ARTIFACTS=$(curl -sL $AUTH_HEADER \
                    "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/artifacts" 2>/dev/null || echo '{"artifacts":[]}')
                
                ARTIFACT_COUNT=$(echo "$ARTIFACTS" | jq -r '.artifacts | length')
                if [ "$ARTIFACT_COUNT" -gt 0 ]; then
                    echo "   Found $ARTIFACT_COUNT artifact(s):"
                    echo "$ARTIFACTS" | jq -r '.artifacts[] | "   - \(.name) (\(.size_in_bytes) bytes)"'
                fi
                
                exit 0
            else
                echo "❌ BUILD FAILED!"
                echo "   Conclusion: $CONCLUSION"
                echo "=========================================="
                
                # Try to download error logs
                echo -e "\n📥 Downloading error logs..."
                for i in 1 2 3; do
                    if curl -sL $AUTH_HEADER \
                        "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/logs" \
                        -o "error-run-$RUN_ID.log" 2>/dev/null; then
                        echo "✅ Error logs saved to: error-run-$RUN_ID.log"
                        echo -e "\n📋 Last 20 error lines:"
                        grep -i "error\|failed\|exception" "error-run-$RUN_ID.log" 2>/dev/null | tail -20 || echo "No obvious errors found"
                        break
                    else
                        echo "   Attempt $i failed, retrying in $((i*10))s..."
                        sleep $((i*10))
                    fi
                done
                
                exit 1
            fi
        fi
    else
        echo "   Status: $STATUS (${CONCLUSION:-N/A}) - No change"
    fi
    
    # Wait 30 seconds before next check
    if [ $ATTEMPT -lt $MAX_ATTEMPTS ]; then
        echo "   Waiting 30s before next check..."
        sleep 30
    fi
done

echo -e "\n⏱️  Max attempts reached ($MAX_ATTEMPTS)"
echo "Build did not complete within expected time"
exit 2
