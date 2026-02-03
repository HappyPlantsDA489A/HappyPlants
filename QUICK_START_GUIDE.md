# How to Delete All GitHub Issues - Quick Guide

## About Your Script Idea

You asked about this script:
```bash
gh issue list [LIST OPTIONS] --json number -q '.[].number' | xargs -I {} gh issue delete {}
```

**Answer:** It's a good foundation, but needed improvements:
- `[LIST OPTIONS]` is a placeholder - needs actual options
- Missing `--yes` flag causes prompts for each deletion (tedious with 82 issues!)
- No error handling or progress tracking
- Should specify the repository explicitly
- Needs `--state all` to include both open and closed issues

## Improved One-Liner

Here's the improved version of your one-liner:

```bash
gh issue list --repo HappyPlantsDA489A/HappyPlants --limit 10000 --state all --json number --jq '.[].number' | xargs -I {} gh issue delete {} --repo HappyPlantsDA489A/HappyPlants --yes
```

**Changes made:**
- ✅ Added `--repo` to specify the repository
- ✅ Added `--limit 10000` to handle large repositories (covers most cases)
- ✅ Added `--state all` to include both open and closed issues
- ✅ Added `--yes` flag to skip confirmation prompts
- ✅ Removed `[LIST OPTIONS]` placeholder

**Note:** If you have more than 10,000 issues, you'll need to run this multiple times.

## Better Option: Use the Script

I've created a much better script at `scripts/delete_all_issues.sh` that includes:
- ✅ Safety confirmation prompts
- ✅ Progress tracking with colored output
- ✅ Error handling and reporting
- ✅ Success/failure summary
- ✅ Prerequisite checks (gh CLI installed, authenticated)

## How to Run the Script

### Prerequisites

1. **Install GitHub CLI:**
   - macOS: `brew install gh`
   - Windows: `winget install --id GitHub.cli`
   - Linux: See detailed instructions in `scripts/README.md`

2. **Authenticate:**
   ```bash
   gh auth login
   ```
   Follow the prompts to authenticate with your GitHub account.

### Running the Script

1. **Navigate to your repository:**
   ```bash
   cd /path/to/HappyPlants
   ```

2. **Go to the scripts directory:**
   ```bash
   cd scripts
   ```

3. **Make it executable (if not already):**
   ```bash
   chmod +x delete_all_issues.sh
   ```

4. **Run the script:**
   ```bash
   ./delete_all_issues.sh
   ```

5. **Confirm deletion:**
   - The script will show you how many issues will be deleted (82 in your case)
   - Type `yes` (exactly) to proceed
   - Watch the progress as each issue is deleted
   - See a summary when complete

## What You'll See

```
================================================
GitHub Issue Deletion Script
================================================

Repository: HappyPlantsDA489A/HappyPlants

WARNING: This will delete ALL issues in HappyPlantsDA489A/HappyPlants

Total issues found: 82

Are you sure you want to delete ALL 82 issues? (yes/no): yes

Starting deletion process...

Deleting issue #84... ✓
Deleting issue #82... ✓
Deleting issue #81... ✓
...
(and so on for all 82 issues)

================================================
Deletion Summary
================================================
Successfully deleted: 82

All issues have been successfully deleted!
```

## Optional: Backup Issues First

Before deleting, you might want to backup your issues:

```bash
gh issue list --repo HappyPlantsDA489A/HappyPlants --limit 10000 --state all --json number,title,body,state,author,createdAt,updatedAt,labels > issues_backup.json
```

This creates a JSON file with all your issues that you can reference later.

**Note:** If you have more than 10,000 issues, you'll need to export in batches.

## Troubleshooting

**"command not found: gh"**
- Install GitHub CLI (see Prerequisites above)

**"Error: Not authenticated with GitHub CLI"**
- Run `gh auth login` and follow the prompts

**"Failed to delete" errors**
- You might not have admin permissions on the repository
- There might be network issues
- GitHub might be rate-limiting your requests

## Summary

✅ **Script created**: `scripts/delete_all_issues.sh`
✅ **Documentation created**: `scripts/README.md`
✅ **Main README updated**: Quick reference added
✅ **Script tested**: Syntax validated
✅ **Improved your one-liner**: Added necessary flags

Everything is ready to use! Just follow the steps above to delete all 82 issues from your repository.
