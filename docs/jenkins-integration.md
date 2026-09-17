# Jenkins integration — what we did, step by step

This is the walkthrough of how this repo’s tests started running on a **local Jenkins** on a Mac, using Docker. Read it in order. Every later Jira task (Dockerfile, compose, Jenkinsfile, Extent) assumes you have been through these steps once.

Jenkins is **not** a free cloud account. There is no `jenkins.io` signup that gives you a running server. You run the software yourself. In this project that software runs in Docker on your laptop at http://localhost:8080/.

---

## 0. What “done” looks like

After this integration:

1. Docker runs a container named `sdet-jenkins`.
2. You log into http://localhost:8080/.
3. Job **sdet-demo** is a **multibranch pipeline**.
4. Any **git commit** on a branch that contains `Jenkinsfile` is picked up (scan every ~2 minutes, or **Scan Multibranch Pipeline Now**).
5. Feature branches run the **smoke** TestNG suite. `main` runs **regression**.
6. The build page shows JUnit results, archived `reports/` + `logs/`, and an **Extent Report** link.

Jenkins only sees **commits**. Unsaved editor buffers and uncommitted files never enter a build.

---

## 1. Get the Jenkins image

The official image was already on the machine:

```bash
docker images jenkins/jenkins
```

If you need to pull it:

```bash
docker pull jenkins/jenkins:latest
```

`latest` is the weekly line. For a learning laptop that is fine. Production teams usually pin `lts-jdk17`.

---

## 2. First start — detached, empty home volume

Ports **8080** (UI) and **50000** (inbound agents) had to be free. Then:

```bash
docker volume create sdet-jenkins-home

docker run -d \
  --name sdet-jenkins \
  --restart unless-stopped \
  -p 8080:8080 \
  -p 50000:50000 \
  -v sdet-jenkins-home:/var/jenkins_home \
  -v /Users/karanagarwal/projects/sdet_learning_demo:/workspace/repo \
  jenkins/jenkins:latest
```

What each piece is for:

| Piece | Why |
| --- | --- |
| `-d` | Detached: container keeps running after the terminal closes |
| `--name sdet-jenkins` | Stable name for `docker start` / `docker logs` |
| `--restart unless-stopped` | Comes back when Docker Desktop starts, unless you stopped it on purpose |
| `-v sdet-jenkins-home:/var/jenkins_home` | Login, plugins, and jobs survive `docker rm` of the container |
| `-v …:/workspace/repo` | Jenkins can git-clone this project from `file:///workspace/repo` without GitHub credentials |

Wait until logs show `Jenkins is fully up and running`:

```bash
docker logs -f sdet-jenkins
```

Unlock password (only needed before you finish the wizard):

```bash
docker exec sdet-jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Open http://localhost:8080/ → paste password → **Install suggested plugins** → create an admin user → Jenkins URL `http://localhost:8080/`.

Suggested plugins already include Git, Pipeline, and GitHub Branch Source. That is enough to run a `Jenkinsfile`.

Useful later:

```bash
docker stop sdet-jenkins
docker start sdet-jenkins
```

---

## 3. Pipeline as code — `Jenkinsfile` at the repo root

A Jenkins “job” can be clicked together in the UI. We did **not** want that as the source of truth. The pipeline lives in git:

`Jenkinsfile` (Declarative Pipeline):

1. **agent any** — run on the Jenkins controller (this laptop’s container).
2. **parameters** — `SUITE`: `auto` / `smoke` / `regression`.
3. **environment** — `JAVA_HOME`, `CHROME_BIN`, `CHROMEDRIVER`, `HEADLESS=true`.
4. **Checkout** — `checkout scm` (the branch Jenkins scanned).
5. **Resolve suite** — `auto` means smoke on feature branches, regression on `main`.
6. **Test** — `./mvnw -B test -DsuiteXmlFile=… -Dheadless=true`.
7. **post always** — JUnit XML, archive `reports/` and `logs/`, publish Extent HTML.

Maven Wrapper (`mvnw` + `.mvn/wrapper/`) is committed so the agent does not need Maven pre-installed. It only needs Java (the Jenkins image has JDK 21; this project compiles to 17).

---

## 4. Why a custom image, not stock `jenkins/jenkins`

The first real test run went **red**. The tests never asserted anything. `BaseTest.setUp` threw:

```text
SessionNotCreatedException
Caused by: Driver server process died prematurely.
os.arch: aarch64
```

This Mac is ARM. The Jenkins container is Linux ARM. Selenium Manager downloaded **x86_64** ChromeDriver (`linux64`). That binary cannot run in the container (`failed to open elf at /lib64/ld-linux-x86-64.so.2`).

Stock Jenkins also has **no browser**. Selenium needs Chromium *and* a matching ARM `chromedriver`.

So we built `ci/jenkins/Dockerfile`:

- `FROM jenkins/jenkins:latest`
- as **root**, `apt-get install chromium chromium-driver`
- `CHROME_BIN=/usr/lib/chromium/chromium` (real binary, not only the wrapper script)
- `CHROMEDRIVER=/usr/bin/chromedriver` (Debian’s ARM package, same version as Chromium)
- `JAVA_OPTS=-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true` (required for `file://` git)
- copy `plugins.txt` and run `jenkins-plugin-cli` (HTML Publisher)
- copy `init.groovy.d/relax-csp.groovy` so Extent’s inline CSS/JS is not blanked by Jenkins CSP
- as **jenkins**, `git config --global --add safe.directory '*'`

`DriverFactory` reads `CHROMEDRIVER` and `CHROME_BIN` so tests do not call Selenium Manager for a second, wrong, driver.

The same smoke suite **passed on the Mac** and **passed inside the Linux container** after this. The red build was infrastructure, not the TestNG cases.

---

## 5. `docker-compose.yml` — how we start it now

`ci/jenkins/docker-compose.yml` replaced the long `docker run`:

- **build** the Dockerfile, tag `sdet-jenkins:local`
- **shm_size: 2gb** — Chrome in Docker needs more than the default 64MB `/dev/shm` (we also pass `--disable-dev-shm-usage` in `DriverFactory`)
- **ports** 8080 and 50000
- **environment** `CHROME_BIN`, `CHROMEDRIVER`, `JAVA_TOOL_OPTIONS=-Dwebdriver.chrome.driver=/usr/bin/chromedriver`
- **volumes** named `sdet-jenkins-home` (external, already created) + bind-mount of the git repo

```bash
docker compose -f ci/jenkins/docker-compose.yml up -d --build
```

The volume keeps your Jenkins user. Rebuilding the **image** is how Chromium/plugins updates land. Deleting the volume would wipe the wizard and jobs.

`ALLOW_LOCAL_CHECKOUT` is required because newer Git plugin versions block `file://` clones unless you opt in.

---

## 6. Job that builds on every commit — multibranch

A single Pipeline job pointed at one branch is not enough. We installed job config:

`ci/jenkins/jobs/sdet-demo-config.xml` → `/var/jenkins_home/jobs/sdet-demo/config.xml`

That job is a **WorkflowMultiBranchProject**:

- SCM: Git `file:///workspace/repo` (the bind mount)
- Discover all branches
- `Jenkinsfile` at repo root
- **PeriodicFolderTrigger** every 2 minutes (`H/2 * * * *`)

After the container is up:

1. http://localhost:8080/job/sdet-demo/
2. **Scan Multibranch Pipeline Now**
3. Branch `ci/jenkins-pipeline` (and any other branch with a `Jenkinsfile`) gets its own child job
4. First build runs automatically

To force a run after a commit: scan again, or wait up to two minutes.

GitHub webhooks cannot reach `localhost`. Polling the local git directory is the trigger we used. The same `Jenkinsfile` would work with a GitHub URL if Jenkins were reachable from the internet.

---

## 7. How a code change becomes a green (or red) build

```text
you commit on a branch
        │
        ▼
Docker bind-mount already has that .git
        │
        ▼
sdet-demo scans file:///workspace/repo
        │
        ▼
child job for that branch checks out the commit
        │
        ▼
Jenkinsfile: suite → ./mvnw test (headless Chromium)
        │
        ▼
JUnit + artifacts + Extent HTML on the build page
```

If the build is red, read the **Console Output** first. Distinguish:

| Pattern | Meaning |
| --- | --- |
| `SessionNotCreatedException` / chromedriver died | Browser/driver on the agent, not the test |
| `Tests run: N, Failures: M` with assertion messages | Real product/test failure |
| `checkout scm` / `file://` / `ALLOW_LOCAL_CHECKOUT` | Git plugin / local clone |
| Maven wrapper download errors | Agent network |

---

## 8. Extent reports on the Jenkins build

The framework already writes `reports/extent-report.html` (Extent Spark) plus screenshots under `reports/screenshots/`.

Jenkins does not show that file as a nice link unless you:

1. Install the **HTML Publisher** plugin (`ci/jenkins/plugins.txt` → `htmlpublisher`).
2. Call `publishHTML` in `Jenkinsfile` `post { always { ... } }` on `reports/extent-report.html`.
3. Relax Jenkins **CSP** (`ci/jenkins/init.groovy.d/relax-csp.groovy`) or Spark’s inline CSS/JS renders as a blank page.

Without HTML Publisher, the same files still appear under **Build Artifacts**.

---

## 9. Files that matter (touch these in the follow-up tickets)

| File | Role |
| --- | --- |
| `Jenkinsfile` | Stages, suite choice, Maven command, publishers |
| `ci/jenkins/Dockerfile` | What is *inside* the Jenkins image (OS packages, plugins, env) |
| `ci/jenkins/docker-compose.yml` | How the container is *run* (ports, mounts, shm, env) |
| `ci/jenkins/plugins.txt` | Extra Jenkins plugins baked into the image |
| `ci/jenkins/init.groovy.d/` | One-time Jenkins JVM settings (CSP) |
| `ci/jenkins/jobs/sdet-demo-config.xml` | Multibranch job definition |
| `src/main/java/.../DriverFactory.java` | `CHROME_BIN` / `CHROMEDRIVER` so Selenium uses the ARM browser |

GitHub Actions (`.github/workflows/ci.yml`) is a separate, hosted path. It is not required for local Jenkins.

---

## 10. Commands cheat sheet

```bash
# start / stop
docker start sdet-jenkins
docker stop sdet-jenkins

# rebuild image, keep Jenkins home
docker compose -f ci/jenkins/docker-compose.yml up -d --build

# logs
docker logs -f sdet-jenkins

# UI
open http://localhost:8080/job/sdet-demo/
```

Commit, then scan. That is the whole loop.
