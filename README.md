# Cytaty Dnia

Aplikacja na Androida napisana w Javie, inspirowana światem Cyberpunk 2077. Pozwala odkrywać cytaty z gry, losować kolejne i zapisywać ulubione wypowiedzi.

## Funkcje

- **Cytat dnia** – codziennie wybierany cytat.
- **Losowanie cytatów** – możliwość wylosowania innej wypowiedzi.
- **Ulubione cytaty** – dodawanie i usuwanie cytatów z ulubionych.
- **Zapisywanie danych** – ulubione cytaty są zachowywane po zamknięciu aplikacji.
- **Data i godzina** – wyświetlanie aktualnej daty w języku polskim.

## Technologie

- Java
- Android Studio
- Android SDK
- XML
- SharedPreferences
- JSON


## Zrzuty ekranu
<img width="632" height="1132" alt="Główny widok aplikacji" src="https://github.com/user-attachments/assets/f67e6012-25a2-4dbb-a795-5af0ec7e2dc3" />


## Wybrane fragmenty kodu

### 1. Losowanie cytatu dnia

Aplikacja zapamiętuje wylosowany cytat dla danego dnia, dzięki czemu po ponownym uruchomieniu nie zmienia się on bez potrzeby.

```java
int dailyIndex = preferences.getInt("daily_index_" + today, -1);

if (dailyIndex < 0 || dailyIndex >= quotes.length) {
    dailyIndex = new Random().nextInt(quotes.length);
    preferences.edit()
            .putInt("daily_index_" + today, dailyIndex)
            .apply();
}

showQuote(quotes[dailyIndex]);
```

### 2. Dodawanie cytatów do ulubionych

Użytkownik może zapisać cytat na liście ulubionych lub usunąć go z niej.

```java
private void toggleFavorite() {
    String key = currentKey();

    if (favorites.contains(key)) {
        favorites.remove(key);
        Toast.makeText(this, "Usunięto z ulubionych",
                Toast.LENGTH_SHORT).show();
    } else {
        favorites.add(key);
        Toast.makeText(this, "Dodano do ulubionych",
                Toast.LENGTH_SHORT).show();
    }

    saveFavorites();
    updateFavoriteButton();
}
```

### 3. Zapisywanie ulubionych cytatów

Lista ulubionych jest zapisywana lokalnie na urządzeniu przy użyciu `SharedPreferences` i formatu JSON.

```java
private void saveFavorites() {
    JSONArray array = new JSONArray();

    for (String favorite : favorites) {
        array.put(favorite);
    }

    preferences.edit()
            .putString("favorites", array.toString())
            .apply();
}
```

## Inspiracja

Projekt inspirowany grą Cyberpunk 2077 i jej charakterystycznym klimatem oraz dialogami postaci.

## Uruchomienie

1. Pobierz repozytorium.
2. Otwórz projekt w Android Studio.
3. Poczekaj na synchronizację Gradle.
4. Uruchom aplikację na emulatorze lub urządzeniu z Androidem.


## Pobierz aplikację

Gotową aplikację na Androida możesz pobrać w sekcji Releases.

[Pobierz](https://github.com/Virucoder/cytaty-dzienne-android/releases/tag/v1.0.0)
