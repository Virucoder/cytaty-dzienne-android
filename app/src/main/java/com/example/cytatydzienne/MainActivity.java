package com.example.cytatydzienne;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    private static class Quote {
        final String text;
        final String author;

        Quote(String text, String author) {
            this.text = text;
            this.author = author;
        }

        String display() {
            return text + " — " + author;
        }
    }

    private final Quote[] quotes = new Quote[] {
            new Quote("Dzień dobry, Night City! \n" +
                    "Trzydzieści zgonów to wynik wczorajszej loterii ciał. 10 z tych trupów wyjechało z Heywood. \n" +
                    "Wojna gangów. Zastrzelono policjanta, a to oznacza, że wszyscy macie przej...ne. NCPD wam nie odpuści.", "- Cyberpunk 2077"),
            new Quote("V nawet szklanki wody ci nie dał? Niedopuszczalne. Nieludzkie. To nie ja. Zobaczysz różnicę. Dam ci pełne wiadra.", "- Johnny Silverhand"),
            new Quote("Nic na tym świecie nie istnieje bez procesu przemiany w coś innego. Podobnie jak ty. Jeśli musisz zabijać, zabijaj. Jeśli musisz spalić wszystko do cna, niech spłonie.", "- Skye"),
            new Quote("Zaczynasz strzelać, a my możemy się włączyć i zanim się obejrzysz? Ktoś może zginąć.", "- Jackie Welles"),
            new Quote("Jeśli mam zginąć, wolałbym spaść do grobu z bronią w ręku i w ogniu.", "- V"),
            new Quote("*pokazuje środkowy palec* „Hej V, ile palców trzymam?”", "- Johnny Silverhand"),
            new Quote("Ze wszystkich głów, w których mogłem się znaleźć, cholernie się cieszę, że to była twoja.", "- Johnny Silverhand"),
            new Quote("Widzę, że niektóre rzeczy się nie zmieniają. Night City to nadal syf i kluski z paśnika.", "- Johnny Silverhand"),
            new Quote("Im szerszy uśmiech, tym większe kłamstwa.", "- Goro Takemura"),
            new Quote("V, ty i ja. Zapiszemy się w historii!", "Jackie Welles"),
            new Quote("To, co jest darmowe, często okazuje się kosztowne.", "- Goro Takemura"),
            new Quote("Ludzie kłamią, najczęściej oszukując samych siebie. Umarli są inni — ich głos jest niezwykle donośny.", "- Saburo Arasaka")
    };

    private TextView quoteText;
    private TextView authorText;
    private TextView dateText;
    private Button favoriteButton;
    private Quote currentQuote;
    private final List<String> favorites = new ArrayList<>();
    private android.content.SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteText = findViewById(R.id.quoteText);
        authorText = findViewById(R.id.authorText);
        dateText = findViewById(R.id.dateText);
        favoriteButton = findViewById(R.id.favoriteButton);
        Button randomButton = findViewById(R.id.randomButton);
        Button showFavoritesButton = findViewById(R.id.showFavoritesButton);

        preferences = getSharedPreferences("cytat_dnia", MODE_PRIVATE);
        loadFavorites();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateText.setText(new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("pl", "PL"))
                .format(new Date()));

        int dailyIndex = preferences.getInt("daily_index_" + today, -1);
        if (dailyIndex < 0 || dailyIndex >= quotes.length) {
            dailyIndex = new Random().nextInt(quotes.length);
            preferences.edit().putInt("daily_index_" + today, dailyIndex).apply();
        }
        showQuote(quotes[dailyIndex]);

        favoriteButton.setOnClickListener(v -> toggleFavorite());
        randomButton.setOnClickListener(v -> {
            Quote next;
            if (quotes.length == 1) {
                next = quotes[0];
            } else {
                do {
                    next = quotes[new Random().nextInt(quotes.length)];
                } while (next == currentQuote);
            }
            showQuote(next);
        });
        showFavoritesButton.setOnClickListener(v -> showFavorites());
    }

    private void showQuote(Quote quote) {
        currentQuote = quote;
        quoteText.setText(quote.text);
        authorText.setText("— " + quote.author);
        updateFavoriteButton();
    }

    private String currentKey() {
        return currentQuote.display();
    }

    private void toggleFavorite() {
        String key = currentKey();
        if (favorites.contains(key)) {
            favorites.remove(key);
            Toast.makeText(this, "Usunięto z ulubionych", Toast.LENGTH_SHORT).show();
        } else {
            favorites.add(key);
            Toast.makeText(this, "Dodano do ulubionych", Toast.LENGTH_SHORT).show();
        }
        saveFavorites();
        updateFavoriteButton();
    }

    private void updateFavoriteButton() {
        boolean isFavorite = favorites.contains(currentKey());
        favoriteButton.setText(isFavorite ? "♥  Usuń z ulubionych" : "♡  Dodaj do ulubionych");
    }

    private void saveFavorites() {
        JSONArray array = new JSONArray();
        for (String favorite : favorites) array.put(favorite);
        preferences.edit().putString("favorites", array.toString()).apply();
    }

    private void loadFavorites() {
        favorites.clear();
        String saved = preferences.getString("favorites", "[]");
        try {
            JSONArray array = new JSONArray(saved);
            for (int i = 0; i < array.length(); i++) favorites.add(array.getString(i));
        } catch (JSONException ignored) {
            // If saved data is damaged, start with an empty favorites list.
        }
    }

    private void showFavorites() {
        if (favorites.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Ulubione cytaty")
                    .setMessage("Nie masz jeszcze zapisanych cytatów. Dodaj cytat do ulubionych, klikając przycisk z sercem.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] items = favorites.toArray(new String[0]);
        new AlertDialog.Builder(this)
                .setTitle("Ulubione cytaty")
                .setItems(items, (dialog, which) -> {
                    String selected = items[which];
                    for (Quote quote : quotes) {
                        if (quote.display().equals(selected)) {
                            showQuote(quote);
                            return;
                        }
                    }
                    String[] parts = selected.split(" — ", 2);
                    quoteText.setText(parts[0]);
                    authorText.setText(parts.length > 1 ? "— " + parts[1] : "");
                    favoriteButton.setText("♥  Zapisano w ulubionych");
                })
                .setNegativeButton("Zamknij", null)
                .show();
    }
}
