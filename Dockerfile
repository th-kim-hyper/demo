FROM mcr.microsoft.com/playwright/java:v1.47.0-noble-amd64

ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

ARG TOMCAT_VERSION=10.1.44

# Tomcat 다운로드/설치 및 tomcat 사용자 생성
RUN apt-get update && apt-get install -y --no-install-recommends \
  curl ca-certificates tar && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://archive.apache.org/dist/tomcat/tomcat-10/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -o /tmp/tomcat.tar.gz

RUN mkdir -p /usr/local/tomcat \
  && tar -xzf /tmp/tomcat.tar.gz -C /usr/local/tomcat --strip-components=1 \
  && rm /tmp/tomcat.tar.gz \
  && mv /usr/local/tomcat/webapps /usr/local/tomcat/webapps.ori \
  && rm -rf /usr/local/tomcat/webapps/* \
  # && mkdir -p /usr/local/tomcat/webapps/ROOT \
  # && echo "It works!" > /usr/local/tomcat/webapps/ROOT/index.html

# RUN groupadd --system tomcat && useradd --system --gid tomcat tomcat 
# RUN chown -R tomcat:tomcat /usr/local/tomcat /usr/local/tomcat/webapps
RUN chmod -R g+rwx /usr/local/tomcat/conf /usr/local/tomcat/logs /usr/local/tomcat/temp /usr/local/tomcat/work /usr/local/tomcat/webapps

# Tomcat 스크립트에 실행 권한 부여
RUN chmod +x /usr/local/tomcat/bin/*.sh
RUN chmod -R 777 /usr/local/tomcat/webapps
# RUN chown -R tomcat:tomcat /usr/local/tomcat/webapps

ENV CATALINA_HOME=/usr/local/tomcat
ENV PATH=$CATALINA_HOME/bin:$PATH

WORKDIR $CATALINA_HOME
EXPOSE 8080

# non-root 사용자로 실행
# USER tomcat

CMD ["catalina.sh", "run"]