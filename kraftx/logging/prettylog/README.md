# kraftx-logging-prettylog

A logging provider for kraftx-logging that wraps [PrettyLog](https://github.com/LukynkaCZE/PrettyLog)

![Example](https://i-have-a.degradationk.ink/Lizzy681366e3sIzmprQEVBda.png)

## Installation

```kt
dependencies {
    implementation("sh.illumi.kraftx:kraftx-logging-prettylog:$kraftVersion")
    implementation("cz.lukynka:PrettyLog:$prettyLogVersion")
}
```

## Usage

```kt
// in a ServiceContainer
val log by registering(LoggingService) {
    withProvider(PrettyLog) {
        withLevel(LogLevel.All)
        
        configure {
            // Optional configuration
            saveToFile = true
            saveDirectoryPath = "./logs/"
            logFileNameFormat = "yyyy-MM-dd-Hms"
            loggerStyle = LoggerStyle.PREFIX
        }
    }
}
```

You can read more about the configuration options in the [PrettyLog Readme](https://github.com/LukynkaCZE/PrettyLog?tab=readme-ov-file#logger-settings)
