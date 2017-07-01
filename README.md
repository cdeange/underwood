# underwood

House of Cards flag animation

![](assets/underwood.gif)

## Usage

```
<com.deange.underwood.FlagView
        android:id="@+id/flag"
        android:layout_width="250dp"
        android:layout_height="wrap_content"
```

| Action                                   | Method             |
|------------------------------------------|--------------------|
| Animate the flag (as shown in the gif)   | `flag.animateIn()` |
| Show the flag                            | `flag.show()`      |
| Hide the flag (basically View.INVISIBLE) | `flag.hide()`      |

## Download

This library can easily be included in your app via Gradle. Because I'm too lazy to manage a maven repository and all that, you can just pull it in via jitpack:

```groovy
repositories {
    maven {
        url 'https://jitpack.io'
    }
}

dependencies {
    compile 'com.github.cdeange:underwood:0.0.1'
}
```

## Contributing

Feel free to submit any pull requests or issues to this repository as you please.
