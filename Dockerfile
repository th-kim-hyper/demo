FROM mcr.microsoft.com/playwright/java:v1.47.0-noble-amd64

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

ARG TOMCAT_VERSION=10.1.28
# Tomcat 다운로드/설치 및 tomcat 사용자 생성
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl ca-certificates tar \
 && rm -rf /var/lib/apt/lists/* \
 \
 # Install Tomcat
 && curl -fsSL https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o /tmp/tomcat.tar.gz \
 && mkdir -p /usr/local/tomcat \
 && tar -xzf /tmp/tomcat.tar.gz -C /usr/local/tomcat --strip-components=1 \
 && rm /tmp/tomcat.tar.gz \
 && rm -rf /usr/local/tomcat/webapps/examples /usr/local/tomcat/webapps/docs

# Create tomcat user and set permissions (separate RUN for better error handling)
RUN set -eux; \
    TOMCAT_UID=8080 TOMCAT_GID=8080; \
    if ! getent group tomcat >/dev/null 2>&1; then \
      if command -v groupadd >/dev/null 2>&1; then \
        groupadd -r -g "${TOMCAT_GID}" tomcat; \
      else \
        echo "tomcat:x:${TOMCAT_GID}:" >> /etc/group; \
      fi; \
    fi; \
    if ! id -u tomcat >/dev/null 2>&1; then \
      if command -v useradd >/dev/null 2>&1; then \
        useradd -r -u "${TOMCAT_UID}" -g tomcat -d /usr/local/tomcat -s /bin/false tomcat; \
      else \
        echo "tomcat:x:${TOMCAT_UID}:${TOMCAT_GID}::/usr/local/tomcat:/bin/false" >> /etc/passwd; \
      fi; \
    fi; \
    chown -R tomcat:tomcat /usr/local/tomcat; \
    chmod -R g+rwx /usr/local/tomcat/conf /usr/local/tomcat/logs /usr/local/tomcat/temp /usr/local/tomcat/work /usr/local/tomcat/webapps

# Tomcat 스크립트에 실행 권한 부여
RUN chmod +x /usr/local/tomcat/bin/*.sh

ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

WORKDIR $CATALINA_HOME
EXPOSE 8080

# non-root 사용자로 실행
USER tomcat

CMD ["catalina.sh", "run"]