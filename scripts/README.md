# GitHub Issue Deletion Script

This directory contains a script to delete all GitHub issues from the HappyPlantsDA489A/HappyPlants repository.

## ⚠️ IMPORTANT WARNING

**This script will PERMANENTLY delete ALL issues from your repository. This action CANNOT be undone!**

Before running this script:
1. Make sure you have backed up or exported any important issue data
2. Verify you have the correct repository permissions
3. Consider closing issues instead of deleting them if you want to preserve history

## Prerequisites

### 1. Install GitHub CLI

The script uses the GitHub CLI (`gh`) to interact with GitHub.

**macOS:**
```bash
brew install gh
```

**Windows:**
```bash
winget install --id GitHub.cli
```
Or download from: https://github.com/cli/cli/releases

**Linux (Debian/Ubuntu):**
```bash
type -p curl >/dev/null || sudo apt install curl -y
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
sudo chmod go+r /usr/share/keyrings/githubcli-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/githubcli-archive-keyring.gpg] https://cli.github.com/packages stable main" | sudo tee /etc/apt/sources.list.d/github-cli.list > /dev/null
sudo apt update
sudo apt install gh -y
```

**Linux (Fedora/CentOS/RHEL):**
```bash
sudo dnf install gh
```

### 2. Authenticate with GitHub

After installing the GitHub CLI, authenticate with your GitHub account:

```bash
gh auth login
```

Follow the prompts to:
1. Choose "GitHub.com"
2. Choose your preferred authentication method (usually "Login with a web browser")
3. Copy the one-time code shown
4. Press Enter to open your browser
5. Paste the code and authorize GitHub CLI

### 3. Verify Authentication

Check that you're properly authenticated:

```bash
gh auth status
```

You should see your username and the authenticated account.

## How to Run the Script

### Step 1: Navigate to the Scripts Directory

```bash
cd /path/to/HappyPlants/scripts
```

### Step 2: Make the Script Executable

```bash
chmod +x delete_all_issues.sh
```

### Step 3: Run the Script

```bash
./delete_all_issues.sh
```

### Step 4: Confirm Deletion

The script will:
1. Show you how many issues will be deleted
2. Ask for confirmation (you must type `yes` exactly)
3. Delete each issue one by one, showing progress
4. Display a summary when complete

## What the Script Does

1. **Checks Prerequisites**: Verifies that `gh` CLI is installed and authenticated
2. **Counts Issues**: Shows the total number of issues that will be deleted
3. **Requires Confirmation**: You must type `yes` to proceed
4. **Deletes Issues**: Iterates through all issues and deletes them one by one
5. **Shows Progress**: Displays real-time progress with ✓ for success and ✗ for failures
6. **Provides Summary**: Shows how many issues were successfully deleted vs. failed

## Troubleshooting

### "command not found: gh"
- The GitHub CLI is not installed. Follow the installation instructions above.

### "Error: Not authenticated with GitHub CLI"
- Run `gh auth login` to authenticate.

### "Failed to delete" errors
Possible causes:
- **Insufficient permissions**: You need admin or maintainer access to delete issues
- **Network issues**: Check your internet connection
- **Rate limiting**: GitHub may rate-limit the API calls. Wait a few minutes and try again.

### Permission Denied
If you get "permission denied" when running the script:
```bash
chmod +x delete_all_issues.sh
```

## Alternative: Manual Deletion via GitHub CLI

If you prefer a simpler one-liner (your original approach with improvements):

```bash
gh issue list --repo HappyPlantsDA489A/HappyPlants --limit 10000 --state all --json number --jq '.[].number' | xargs -I {} gh issue delete {} --repo HappyPlantsDA489A/HappyPlants --yes
```

**Note:** This one-liner:
- Uses `--limit 10000` to handle large repositories (should cover most cases)
- Won't show progress or confirmation prompts
- May not handle errors as gracefully
- Could hit rate limits with many issues
- If you have more than 10,000 issues, you'll need to run it multiple times

The provided script is recommended for better control and feedback.

## Exporting Issues Before Deletion

If you want to keep a record of your issues before deleting them:

```bash
# Export all issues to JSON (using high limit to get all issues)
gh issue list --repo HappyPlantsDA489A/HappyPlants --limit 10000 --state all --json number,title,body,state,author,createdAt,updatedAt,labels > issues_backup.json

# Export to CSV format (basic info)
gh issue list --repo HappyPlantsDA489A/HappyPlants --limit 10000 --state all --json number,title,state,author --template '{{range .}}{{.number}},{{.title}},{{.state}},{{.author.login}}{{"\n"}}{{end}}' > issues_backup.csv
```

**Note:** If you have more than 10,000 issues, you'll need to export in batches or use the GitHub API directly.

## Need Help?

If you encounter issues or need assistance:
1. Check that you have the latest version of GitHub CLI: `gh --version`
2. Verify your permissions on the repository
3. Check GitHub's status page: https://www.githubstatus.com/
