A shopping list curator which is blatantly copied from https://github.com/gowhale/go-shopping-list but
in [Kotlin](https://kotlinlang.org/) using [Compose for Desktop](https://www.jetbrains.com/lp/compose-mpp/).

I've experimented with https://arrow-kt.io/. For now I'm just using
its [Either](https://arrow-kt.io/docs/apidocs/arrow-core/arrow.core/-either/) type for a more functional style of error
handling.

### Demos

#### Todoist

https://user-images.githubusercontent.com/20602676/191100148-6cf38f81-7f6a-4e30-a4c9-9e438360bc53.mp4

### Running desktop application

```
./gradlew run
```

### Building uber JAR distribution

```
./gradlew packageUberJarForCurrentOS
```

### Building native desktop distribution

TODO: resources aren't being copied by compose plugin, see [issue](https://github.com/JetBrains/compose-jb/issues/2190).

### Todoist

You can find your personal token in the [integrations settings view](https://todoist.com/prefs/integrations) of the
Todoist web app. Set this to the **Todoist
Token** setting.

### Maintaining Professional Standards

There is a single unit test.