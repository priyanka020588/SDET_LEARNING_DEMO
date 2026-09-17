FROM jenkins/jenkins:latest

USER root
RUN apt-get update \
    && apt-get install -y --no-install-recommends chromium chromium-driver \
    && rm -rf /var/lib/apt/lists/*

ENV CHROME_BIN=/usr/lib/chromium/chromium
ENV CHROMEDRIVER=/usr/bin/chromedriver
ENV JAVA_OPTS="-Dhudson.plugins.git.GitSCM.ALLOW_LOCAL_CHECKOUT=true"

COPY --chown=jenkins:jenkins plugins.txt /usr/share/jenkins/ref/plugins.txt
COPY --chown=jenkins:jenkins init.groovy.d/relax-csp.groovy /usr/share/jenkins/ref/init.groovy.d/relax-csp.groovy

USER jenkins
RUN git config --global --add safe.directory '*'
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt
