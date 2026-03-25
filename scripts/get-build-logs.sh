#!/bin/bash
# GitHub Actions Build Log Downloader
# 用于获取构建错误日志，带重试机制

set -e

REPO="${1:-feihu1991/paper-learning-assistant}"
RUN_ID="${2:-}"
TOKEN="${3:-}"

if [ -z "$RUN_ID" ]; then
    echo "Usage: $0 <repo> <run_id> [token]"
    echo "Example: $0 feihu1991/paper-learning-assistant 23529321416"
    exit 1
fi

AUTH_HEADER=""
if [ -n "$TOKEN" ]; then
    AUTH_HEADER="-H \"Authorization: token $TOKEN\""
fi

echo "Downloading logs for run $RUN_ID from $REPO..."

# Retry with exponential backoff
for i in 1 2 3; do
    echo "Attempt $i..."
    
    if curl -sL $AUTH_HEADER \
        "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/logs" \
        -o "build-$RUN_ID.log"; then
        echo "✅ Logs downloaded successfully!"
        echo "Saved to: build-$RUN_ID.log"
        
        # Show error summary
        echo -e "\n=== Error Summary ==="
        grep -i "error\|failed\|exception" "build-$RUN_ID.log" | tail -20 || echo "No obvious errors found"
        
        exit 0
    else
        echo "❌ Attempt $i failed"
        if [ $i -lt 3 ]; then
            WAIT=$((i*10))
            echo "Waiting ${WAIT}s before retry..."
            sleep $WAIT
        fi
    fi
done

echo "❌ Failed to download logs after 3 attempts"
exit 1
