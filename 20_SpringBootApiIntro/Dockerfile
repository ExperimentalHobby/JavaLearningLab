# ---- ビルドステージ ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# pom.xmlだけを先にコピーして依存解決レイヤーをキャッシュする(pom.xmlが変わらない限り、
# srcを変更してもこのレイヤーは再利用され、docker buildのたびに全依存関係を再ダウンロードしない)。
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

COPY src src
# イメージビルドではテストを実行しない(テストの担保はmvn test/CIの役割とし、
# ビルドステージはパッケージングに専念させる)。
RUN ./mvnw clean package -DskipTests -B

# ---- 実行ステージ ----
FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app

# rootユーザーでアプリを実行しないよう専用ユーザーを作成する。
RUN useradd --system --create-home appuser
COPY --from=build --chown=appuser:appuser /workspace/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
