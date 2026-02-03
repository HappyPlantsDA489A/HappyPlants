#!/bin/bash

################################################################################
# Script: delete_all_issues.sh
# Purpose: Delete all GitHub issues from the HappyPlantsDA489A/HappyPlants repository
# 
# IMPORTANT: This script will PERMANENTLY delete all issues. This action cannot
# be undone. Make sure you have backups or exports of important issues before
# running this script.
################################################################################

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Repository details
OWNER="HappyPlantsDA489A"
REPO="HappyPlants"

echo "================================================"
echo "GitHub Issue Deletion Script"
echo "================================================"
echo ""
echo "Repository: ${OWNER}/${REPO}"
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo -e "${RED}Error: GitHub CLI (gh) is not installed.${NC}"
    echo "Please install it from: https://cli.github.com/"
    exit 1
fi

# Check if user is authenticated
if ! gh auth status &> /dev/null; then
    echo -e "${RED}Error: Not authenticated with GitHub CLI.${NC}"
    echo "Please run: gh auth login"
    exit 1
fi

echo -e "${YELLOW}WARNING: This will delete ALL issues in ${OWNER}/${REPO}${NC}"
echo ""

# Count total issues (using a high limit to get all issues)
# Note: GitHub CLI has a limit parameter, we use 10000 which should cover most repositories
ISSUE_LIMIT=10000
TOTAL_ISSUES=$(gh issue list --repo "${OWNER}/${REPO}" --limit ${ISSUE_LIMIT} --state all --json number --jq 'length')
echo "Total issues found: ${TOTAL_ISSUES}"

# Warn if we hit the limit (unlikely but possible for very large repos)
if [ "$TOTAL_ISSUES" -eq "$ISSUE_LIMIT" ]; then
    echo -e "${YELLOW}Warning: Hit the limit of ${ISSUE_LIMIT} issues. There may be more issues not shown.${NC}"
    echo -e "${YELLOW}You may need to run this script multiple times to delete all issues.${NC}"
fi
echo ""

if [ "$TOTAL_ISSUES" -eq 0 ]; then
    echo -e "${GREEN}No issues to delete.${NC}"
    exit 0
fi

# Confirmation prompt
read -p "Are you sure you want to delete ALL ${TOTAL_ISSUES} issues? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
    echo "Deletion cancelled."
    exit 0
fi

echo ""
echo "Starting deletion process..."
echo ""

# Get all issue numbers (both open and closed)
ISSUE_NUMBERS=$(gh issue list --repo "${OWNER}/${REPO}" --limit ${ISSUE_LIMIT} --state all --json number --jq '.[].number')

# Counter for tracking progress
SUCCESS_COUNT=0
FAIL_COUNT=0

# Delete each issue
for ISSUE_NUM in $ISSUE_NUMBERS; do
    echo -n "Deleting issue #${ISSUE_NUM}... "
    
    if gh issue delete "${ISSUE_NUM}" --repo "${OWNER}/${REPO}" --yes 2>/dev/null; then
        echo -e "${GREEN}✓${NC}"
        ((SUCCESS_COUNT++))
    else
        echo -e "${RED}✗ (failed)${NC}"
        ((FAIL_COUNT++))
    fi
done

echo ""
echo "================================================"
echo "Deletion Summary"
echo "================================================"
echo -e "${GREEN}Successfully deleted: ${SUCCESS_COUNT}${NC}"
if [ "$FAIL_COUNT" -gt 0 ]; then
    echo -e "${RED}Failed to delete: ${FAIL_COUNT}${NC}"
fi
echo ""

if [ "$FAIL_COUNT" -gt 0 ]; then
    echo -e "${YELLOW}Note: Some issues may have failed to delete. This could be due to:${NC}"
    echo "  - Insufficient permissions"
    echo "  - Network issues"
    echo "  - Issues that were already deleted"
    exit 1
fi

echo -e "${GREEN}All issues have been successfully deleted!${NC}"
