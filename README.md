# Hyper Command

![Gradle](https://img.shields.io/badge/Gradle-v8%2E5-g?logo=gradle&style=flat-square)
![Zulu JDK](https://img.shields.io/badge/Zulu%20JDK-8-blue?style=flat-square)
![GitHub License](https://img.shields.io/github/license/ideal-state/hyper-command?style=flat-square)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/ideal-state/hyper-command?style=flat-square&logo=github)
![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ideal-state/hyper-command/release.yml?style=flat-square)
![GitHub Release](https://img.shields.io/github/v/release/ideal-state/hyper-command?style=flat-square)
![Discord](https://img.shields.io/discord/1191122625389396098?style=flat-square&logo=discord)

------------------------------------------------------

> 一个简单快速的命令系统

### 在 Maven 项目中使用

```xml

<dependency>
    <groupId>team.idealstate.hyper</groupId>
    <artifactId>hyper-command-impl</artifactId>
    <version>${version}</version>
</dependency>
```

### 在 Gradle 项目中使用

```groovy
dependencies {
    implementation "team.idealstate.hyper:hyper-command-impl:${version}"
}
```

```kotlin
dependencies {
    implementation("team.idealstate.hyper:hyper-command-impl:${version}")
}
```

### 在哪下载 ?

> 前往 [releases](https://github.com/ideal-state/hyper-command/releases) 页

### 如何构建

```shell
git clone https://github.com/ideal-state/hyper-command.git
```

```shell
cd ./hyper-command
```

```shell
./gradlew.bat :clean :hyper-command-api:jar :hyper-command-impl:jar
```

或

```shell
./gradlew :clean :hyper-command-api:jar :hyper-command-impl:jar
```

> 等待构建完成，在 ./build/libs 下会生成 .jar 工件

### 关于开发配置

> [`local.properties`](./local.properties)

### 怎样成为贡献者 ?

在贡献之前，你需要了解相应的规范。[前往查看](https://github.com/ideal-state)

