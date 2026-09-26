FROM debian:bookworm-slim

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        openjdk-17-jdk-headless \
        chromium \
        chromium-driver \
        ca-certificates \
        curl \
        unzip \
    && ln -sfn /usr/lib/jvm/java-17-openjdk-* /usr/lib/jvm/java-17 \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/usr/lib/jvm/java-17
ENV CHROME_BIN=/usr/lib/chromium/chromium
ENV CHROMEDRIVER=/usr/bin/chromedriver

WORKDIR /app
