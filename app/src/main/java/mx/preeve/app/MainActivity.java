package mx.preeve.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private final int navy = Color.rgb(24, 48, 86);
    private final int blue = Color.rgb(40, 100, 184);
    private final int bg = Color.rgb(244, 247, 252);
    private final int muted = Color.rgb(102, 116, 137);
    private LinearLayout root, content, nav;
    private SharedPreferences prefs;
    private String page = "Inicio";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        prefs = getSharedPreferences("prevee_data", MODE_PRIVATE);
        buildShell();
        showPage("Inicio");
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }

    private void buildShell() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(12), dp(14), dp(12));
        header.setBackgroundColor(navy);

        ImageView logo = new ImageView(this);
        logo.setImageResource(R.drawable.prevee_logo);
        header.addView(logo, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.VERTICAL);
        brand.setPadding(dp(11), 0, 0, 0);
        TextView brandTitle = text("Preeve", 21, Color.WHITE, true);
        TextView brandSub = text("ESPACIO ESCOLAR", 10, Color.rgb(207, 221, 242), true);
        brand.addView(brandTitle);
        brand.addView(brandSub);
        header.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));

        Button help = smallHeaderButton("Soporte");
        help.setOnClickListener(v -> toast("Soporte de demostración"));
        header.addView(help);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(18), dp(16), dp(24));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setBackgroundColor(Color.WHITE);
        nav.setPadding(dp(8), dp(6), dp(8), dp(6));
        root.addView(nav);
        setContentView(root);
        drawNav();
    }

    private Button smallHeaderButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(11);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setPadding(dp(7), 0, dp(7), 0);
        b.setMinHeight(dp(36));
        b.setMinWidth(0);
        b.setBackground(round(Color.rgb(48, 72, 111), 10));
        return b;
    }

    private void drawNav() {
        nav.removeAllViews();
        String[] names = {"Inicio", "Notificaciones", "Perfil"};
        String[] symbols = {"⌂", "●", "○"};
        for (int i = 0; i < names.length; i++) {
            final String target = names[i];
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(Gravity.CENTER);
            TextView symbol = text(symbols[i], 19, page.equals(target) ? blue : muted, true);
            TextView label = text(names[i], 11, page.equals(target) ? blue : muted, page.equals(target));
            item.addView(symbol);
            item.addView(label);
            item.setPadding(0, dp(2), 0, dp(2));
            item.setOnClickListener(v -> showPage(target));
            nav.addView(item, new LinearLayout.LayoutParams(0, dp(50), 1));
        }
    }

    private void showPage(String target) {
        page = target;
        drawNav();
        content.removeAllViews();
        if ("Inicio".equals(target)) home();
        else if ("Notificaciones".equals(target)) notifications();
        else profile();
    }

    private void home() {
        TextView eyebrow = text("PANEL ESCOLAR", 11, blue, true);
        eyebrow.setLetterSpacing(0.08f);
        content.addView(eyebrow);
        content.addView(text("Hola, " + prefs.getString("name", "Estudiante"), 25, navy, true));
        content.addView(text("Consulta tus avisos y accesos escolares.", 14, muted, false));
        gap(14);

        LinearLayout profileCard = new LinearLayout(this);
        profileCard.setGravity(Gravity.CENTER_VERTICAL);
        profileCard.setPadding(dp(15), dp(14), dp(15), dp(14));
        profileCard.setBackground(round(Color.WHITE, 16));
        TextView avatar = text("P", 24, Color.WHITE, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(round(blue, 30));
        profileCard.addView(avatar, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout studentInfo = new LinearLayout(this);
        studentInfo.setOrientation(LinearLayout.VERTICAL);
        studentInfo.setPadding(dp(12), 0, 0, 0);
        studentInfo.addView(text(prefs.getString("name", "Estudiante"), 15, navy, true));
        studentInfo.addView(text("Grupo: " + prefs.getString("group", "1°D") + "  ·  Perfil de prueba", 12, muted, false));
        profileCard.addView(studentInfo);
        content.addView(profileCard);
        gap(18);

        section("Factores de seguimiento");
        content.addView(text("Selecciona una opción para registrar un ejemplo local.", 13, muted, false));
        gap(8);
        String[][] factors = {
            {"Falta", "Falta"}, {"Retardo", "Retardo"},
            {"No trabajo", "No trabajo"}, {"No tarea", "No tarea"},
            {"Sin material", "Sin material"}, {"Indisciplina", "Indisciplina"},
            {"Factor de riesgo", "Factor de riesgo"}, {"Felicitación", "Felicitación"},
            {"Excelente desempeño", "Excelente desempeño"}
        };
        for (int i = 0; i < factors.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.TOP);
            row.addView(factorCard(factors[i][0]), new LinearLayout.LayoutParams(0, dp(86), 1));
            if (i + 1 < factors.length) {
                View space = new View(this);
                row.addView(space, new LinearLayout.LayoutParams(dp(10), 1));
                row.addView(factorCard(factors[i + 1][0]), new LinearLayout.LayoutParams(0, dp(86), 1));
            } else {
                row.addView(new View(this), new LinearLayout.LayoutParams(0, 1, 1));
            }
            LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(-1, -2);
            rp.setMargins(0, 0, 0, dp(10));
            content.addView(row, rp);
        }

        section("Accesos rápidos");
        gap(5);
        quickAccess("Ver horario asignado", "Consulta el horario de ejemplo");
        quickAccess("Ver justificantes", "Revisa justificantes de demostración");
        quickAccess("Expediente de factores", "Registros agrupados por semestre");
        quickAccess("Entradas y salidas", "Consulta registros de ejemplo");
        gap(8);
        TextView disclaimer = text("DEMO · No está conectado a PREVEE oficial ni a los sistemas de una escuela. Los registros son solo locales.", 11, muted, false);
        disclaimer.setPadding(dp(3), dp(8), dp(3), dp(8));
        content.addView(disclaimer);
    }

    private View factorCard(String label) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(10), dp(10), dp(10));
        card.setBackground(round(Color.WHITE, 14));
        TextView dot = text("•", 20, label.equals("Felicitación") || label.equals("Excelente desempeño") ? Color.rgb(35, 145, 105) : blue, true);
        card.addView(dot);
        TextView name = text(label, 13, navy, true);
        card.addView(name);
        card.setOnClickListener(v -> {
            String old = prefs.getString("factor_records", "");
            String updated = old.isEmpty() ? label : old + "\n" + label;
            prefs.edit().putString("factor_records", updated).apply();
            toast("Registro de prueba agregado: " + label);
        });
        return card;
    }

    private void quickAccess(String title, String subtitle) {
        LinearLayout item = new LinearLayout(this);
        item.setGravity(Gravity.CENTER_VERTICAL);
        item.setPadding(dp(14), dp(12), dp(12), dp(12));
        item.setBackground(round(Color.WHITE, 13));
        LinearLayout words = new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.addView(text(title, 14, navy, true));
        words.addView(text(subtitle, 12, muted, false));
        item.addView(words, new LinearLayout.LayoutParams(0, -2, 1));
        item.addView(text("›", 25, blue, false));
        item.setOnClickListener(v -> {
            if (title.contains("horario")) showSchedule();
            else if (title.contains("Expediente")) showRecords();
            else toast(title + ": información de ejemplo");
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(5));
        content.addView(item, lp);
    }

    private void showSchedule() {
        content.removeAllViews();
        section("Horario asignado");
        content.addView(text("HORARIO DE DEMOSTRACIÓN", 11, blue, true));
        String[][] rows = {{"7:00 – 7:50","Español"},{"7:50 – 8:40","Matemáticas"},{"8:40 – 9:30","Ciencias"},{"9:30 – 10:00","Receso"},{"10:00 – 10:50","Inglés"},{"10:50 – 11:40","Tecnología"},{"11:40 – 12:30","Artes"},{"12:30 – 1:10","Tutoría"}};
        for (String[] row : rows) quickLine(row[0], row[1]);
        backButton();
    }

    private void showRecords() {
        content.removeAllViews();
        section("Expediente de factores");
        content.addView(text("Registros locales de demostración", 13, muted, false));
        String records = prefs.getString("factor_records", "");
        if (records.isEmpty()) content.addView(text("Todavía no hay registros. Selecciona un factor desde Inicio.", 14, muted, false));
        else for (String record : records.split("\\n")) cardLine(record, "Registro de prueba");
        backButton();
    }

    private void quickLine(String left, String right) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(13), dp(12), dp(13), dp(12));
        row.setBackground(round(Color.WHITE, 12));
        row.addView(text(left, 13, muted, false), new LinearLayout.LayoutParams(0, -2, 1));
        row.addView(text(right, 14, navy, true));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(4), 0, dp(4));
        content.addView(row, lp);
    }

    private void cardLine(String heading, String body) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(13), dp(12), dp(13), dp(12));
        box.setBackground(round(Color.WHITE, 12));
        box.addView(text(heading, 14, navy, true));
        box.addView(text(body, 12, muted, false));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(5));
        content.addView(box, lp);
    }

    private void notifications() {
        section("Notificaciones");
        content.addView(text("Avisos y novedades de ejemplo", 14, muted, false));
        gap(10);
        cardLine("Bienvenido a Preeve", "Esta es una versión de demostración con información local.");
        cardLine("Aviso escolar", "Aquí aparecerían avisos cuando la función esté desarrollada.");
        cardLine("Privacidad", "No se envían datos a una escuela ni a un servidor.");
    }

    private void profile() {
        section("Mi perfil");
        content.addView(text("Los datos se guardan solo en este dispositivo.", 13, muted, false));
        gap(8);
        content.addView(text("Nombre", 13, navy, true));
        EditText name = edit(prefs.getString("name", "Estudiante"));
        content.addView(name);
        content.addView(text("Grupo", 13, navy, true));
        EditText group = edit(prefs.getString("group", "1°D"));
        content.addView(group);
        Button save = actionButton("Guardar perfil", true);
        content.addView(save);
        save.setOnClickListener(v -> {
            prefs.edit().putString("name", name.getText().toString().trim().isEmpty() ? "Estudiante" : name.getText().toString().trim())
                .putString("group", group.getText().toString().trim().isEmpty() ? "1°D" : group.getText().toString().trim()).apply();
            toast("Perfil guardado");
        });
        gap(12);
        cardLine("Acerca de Preeve", "Prototipo independiente. No es una aplicación oficial ni accede a datos escolares reales.");
    }

    private EditText edit(String value) {
        EditText e = new EditText(this);
        e.setSingleLine(true);
        e.setTextSize(15);
        e.setText(value);
        e.setPadding(dp(12), dp(8), dp(12), dp(8));
        e.setBackground(round(Color.WHITE, 11));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(5), 0, dp(12));
        e.setLayoutParams(lp);
        return e;
    }

    private Button actionButton(String label, boolean primary) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        b.setTextSize(14);
        b.setTextColor(primary ? Color.WHITE : navy);
        b.setPadding(dp(12), dp(5), dp(12), dp(5));
        b.setBackground(round(primary ? blue : Color.rgb(229, 237, 248), 11));
        return b;
    }

    private void backButton() {
        Button b = actionButton("Volver al inicio", true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(14), 0, dp(8));
        content.addView(b, lp);
        b.setOnClickListener(v -> showPage("Inicio"));
    }

    private void section(String label) {
        content.addView(text(label, 19, navy, true));
    }

    private void gap(int height) {
        content.addView(new View(this), new LinearLayout.LayoutParams(1, dp(height)));
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setIncludeFontPadding(true);
        return t;
    }

    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
