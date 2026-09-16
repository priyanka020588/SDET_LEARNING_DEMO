# CI: free hosted pipelines

Jenkins does **not** give you a free cloud account. It is software you install on a machine you provide. There is no `jenkins.io` login that hands you a running server.

## Local Jenkins on this Mac

Jenkins is already running at http://localhost:8080/. The `sdet-demo` job is a **multibranch pipeline** pointed at this git repo (`file:///workspace/repo`).

How a code change becomes a build:

1. Commit on a branch that contains `Jenkinsfile` (uncommitted files are invisible to Jenkins).
2. Within about 2 minutes Jenkins scans branches and starts a run. Or open http://localhost:8080/job/sdet-demo/ and click **Scan Multibranch Pipeline Now**.
3. Feature branches run **smoke**. `main` runs **regression**.
4. Open the branch job → the build → **Test Result** and archived `reports/` (Extent HTML).

Start / stop:

```bash
docker start sdet-jenkins
docker stop sdet-jenkins
```

Recreate with Chrome + the job definition (keeps your Jenkins login, wipes only the container):

```bash
docker compose -f ci/jenkins/docker-compose.yml up -d --build
```

For this repo, the free server that already exists is **GitHub Actions**. GitHub runs the agents. You create pipelines as YAML files. They start on their own when someone pushes, opens a PR, or when the nightly schedule fires.

## Use GitHub Actions (recommended)

After these files are on `main`:

1. Open the repo on GitHub.
2. Click **Actions**.
3. Open **CI**.
4. Click **Run workflow** to start a smoke or regression run by hand.
5. When a run finishes, open it and download **smoke-reports** or **regression-reports** (Extent HTML, screenshots, Surefire XML).

What runs without you clicking:

| Trigger | Suite | When |
| --- | --- | --- |
| Push to `main` | smoke | Every merge/push |
| Pull request | smoke | Every PR |
| Nightly cron | regression | 11:30pm IST |
| **Run workflow** button | you choose | Manual |

That is a pipeline running autonomously. You do not keep a laptop or a Jenkins VM online.

Create another pipeline by adding a new file under `.github/workflows/`, for example `nightly.yml`. GitHub picks it up on the next push. No extra server signup.

Limits that matter for learning:

- Public repos: GitHub-hosted minutes are free.
- Private repos: 2,000 minutes/month on the free plan.
- Scheduled workflows pause if the default branch has no activity for 60 days. A push to `main` turns them back on.

## Why not “free Jenkins hosting”

| Option | What you actually get |
| --- | --- |
| Jenkins open source | Free licence. You still need a computer. |
| CloudBees CI | Paid. |
| Random “free Jenkins hosting” sites | Not official. Treat as throwaway and do not put secrets there. |
| AWS/GCP/Azure free tier | A VM, usually for 12 months, then it bills. You patch Jenkins yourself. |
| Oracle Cloud Always Free | A small VM that can stay free. Closest real “free Jenkins server”. You still install, expose, and maintain it. |

Putting Jenkins on the public internet without HTTPS and a strong admin password is a common way to get the instance hijacked. GitHub Actions avoids that problem because you never open a Jenkins port.

## If you still want a Jenkins UI on a free VM

Do this only if you want to click **New Item** in Jenkins itself. GitHub Actions already covers autonomous runs.

1. Create an [Oracle Cloud Always Free](https://www.oracle.com/cloud/free/) account (card is required for verification; Always Free compute should not bill if you stay on those shapes).
2. Launch an Always Free Linux VM (Ampere A1 ARM is the usual free shape). Add an SSH key. Give it a public IP.
3. In the VCN security list, allow **SSH (22)** from your IP only. Do **not** open 8080 to `0.0.0.0/0`. Use an SSH tunnel instead:

   ```bash
   ssh -L 8080:localhost:8080 ubuntu@YOUR_VM_IP
   ```

   Then open http://localhost:8080 on your laptop.

4. On the VM:

   ```bash
   sudo apt-get update
   sudo apt-get install -y docker.io git openjdk-17-jdk
   sudo usermod -aG docker $USER
   # log out and back in so docker works without sudo
   docker volume create jenkins_home
   docker run -d --name jenkins --restart unless-stopped \
     -p 127.0.0.1:8080:8080 \
     -v jenkins_home:/var/jenkins_home \
     jenkins/jenkins:lts-jdk17
   docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
   ```

5. Unlock Jenkins, install suggested plugins, then:

   - **New Item** → **Pipeline** → **Pipeline script from SCM**
   - SCM: Git
   - Repository URL: this GitHub repo
   - Script path: `Jenkinsfile`

6. To make it autonomous inside Jenkins: the job → **Configure** → **Build Triggers** → **GitHub hook trigger** and/or **Build periodically** (`H 18 * * *`).

Jenkins on that VM also needs **Chrome or Chromium** on the agent, or the Selenium job will fail. GitHub-hosted `ubuntu-latest` already has Chrome, which is why Actions is the lower-friction path for this project.

## Interview version

> Jenkins is free to download, not free to host. In this project CI runs on GitHub Actions: smoke on every PR, full regression nightly, reports uploaded as artifacts. The same stages live in a `Jenkinsfile` so a self-hosted Jenkins can run the same suite later.
