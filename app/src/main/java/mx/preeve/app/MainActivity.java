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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private final int navy = Color.rgb(23, 49, 92);
    private final int blue = Color.rgb(45, 104, 190);
    private final int pale = Color.rgb(241, 246, 252);
    private LinearLayout root, content, nav;
    private SharedPreferences prefs;
    private String page = "Inicio";
    private final List<String> pages = Arrays.asList("Inicio","Materias","Tareas","Calificaciones","Horario","Perfil");

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(navy);
        prefs = getSharedPreferences("prevee_data", MODE_PRIVATE);
        buildShell();
        showPage("Inicio");
    }

    private void buildShell() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(pale);
        LinearLayout brand = new LinearLayout(this);
        brand.setOrientation(LinearLayout.HORIZONTAL);
        brand.setGravity(Gravity.CENTER_VERTICAL);
        brand.setPadding(dp(16), dp(12), dp(16), dp(12));
        brand.setBackgroundColor(navy);
        android.widget.ImageView logo = new android.widget.ImageView(this);
        logo.setImageResource(mx.preeve.app.R.drawable.prevee_logo);
        LinearLayout.LayoutParams logoParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        logoParams.setMargins(0, 0, dp(12), 0);
        brand.addView(logo, logoParams);
        LinearLayout brandWords = new LinearLayout(this);
        brandWords.setOrientation(LinearLayout.VERTICAL);
        TextView brandTitle = new TextView(this);
        brandTitle.setText("Preeve");
        brandTitle.setTextColor(Color.WHITE);
        brandTitle.setTextSize(21);
        brandTitle.setTypeface(null, Typeface.BOLD);
        TextView brandSubtitle = new TextView(this);
        brandSubtitle.setText("ESPACIO ESCOLAR · 1.0");
        brandSubtitle.setTextColor(Color.rgb(205, 220, 242));
        brandSubtitle.setTextSize(10);
        brandWords.addView(brandTitle);
        brandWords.addView(brandSubtitle);
        brand.addView(brandWords);
        root.addView(brand);
        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(24));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setBackgroundColor(Color.WHITE);
        nav.setPadding(dp(4), dp(6), dp(4), dp(6));
        root.addView(nav);
        setContentView(root);
        drawNav();
    }

    private void drawNav() {
        nav.removeAllViews();
        String[] shortNames = {"Inicio","Materias","Tareas","Notas","Horario","Perfil"};
        for (int i=0;i<pages.size();i++) {
            final String target = pages.get(i);
            Button b = new Button(this);
            b.setText(shortNames[i]);
            b.setTextSize(10);
            b.setAllCaps(false);
            b.setMinWidth(0);
            b.setPadding(dp(1),0,dp(1),0);
            b.setTextColor(page.equals(target) ? blue : Color.DKGRAY);
            b.setBackgroundColor(Color.TRANSPARENT);
            b.setOnClickListener(v -> showPage(target));
            nav.addView(b, new LinearLayout.LayoutParams(0, dp(48), 1));
        }
    }

    private void showPage(String target) {
        page = target;
        drawNav();
        content.removeAllViews();
        if ("Inicio".equals(target)) home();
        else if ("Materias".equals(target)) subjects();
        else if ("Tareas".equals(target)) tasks();
        else if ("Calificaciones".equals(target)) grades();
        else if ("Horario".equals(target)) schedule();
        else profile();
    }

    private void home() {
        TextView hello = text("Tu espacio para organizar la escuela", 23, navy, true);
        content.addView(hello);
        content.addView(text("PRIMER MODELO · VERSIÓN 1.0", 11, blue, true));
        card("Resumen", "Organiza tus materias, consulta tareas y lleva un registro personal de tus actividades.");
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(metric("6","Secciones"), new LinearLayout.LayoutParams(0, dp(90), 1));
        row.addView(metric("3","Tareas demo"), new LinearLayout.LayoutParams(0, dp(90), 1));
        content.addView(row);
        section("Acceso rápido");
        for (String s : Arrays.asList("Materias","Tareas","Calificaciones","Horario","Perfil")) {
            Button b = button("Abrir " + s, true);
            b.setOnClickListener(v -> showPage(s));
            content.addView(b);
        }
        note("Los datos de ejemplo no son calificaciones ni información oficial de una escuela.");
    }

    private View metric(String number, String label) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setBackground(round(Color.WHITE, 16));
        TextView n = text(number, 25, blue, true);
        TextView l = text(label, 12, Color.DKGRAY, false);
        box.addView(n); box.addView(l);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-1);
        p.setMargins(dp(4),dp(8),dp(4),dp(8));
        box.setLayoutParams(p);
        return box;
    }

    private void subjects() {
        title("Mis materias", "Una vista inicial para organizar tu jornada.");
        String[][] data = {{"Español","Lectura, escritura y proyectos"},{"Matemáticas","Ejercicios y problemas"},{"Ciencias","Apuntes y actividades"},{"Inglés","Vocabulary and readings"},{"Tecnología","Proyectos y prácticas"},{"Artes","Actividades creativas"}};
        for (String[] d : data) card(d[0], d[1]);
    }

    private void tasks() {
        title("Tareas", "Agrega tus pendientes. Se guardan en este dispositivo.");
        EditText field = edit("Escribe una tarea...");
        content.addView(field);
        Button add = button("＋ Agregar tarea", true);
        content.addView(add);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        content.addView(list);
        Runnable[] render = new Runnable[1];
        render[0] = () -> {
            list.removeAllViews();
            String saved = prefs.getString("tasks", "Leer páginas 12 y 13|Terminar actividad de Ciencias|Preparar materiales de Tecnología");
            if (saved.isEmpty()) { list.addView(text("No tienes tareas pendientes.", 14, Color.DKGRAY, false)); return; }
            for (String task : new ArrayList<>(Arrays.asList(saved.split("\\|",-1)))) {
                if (task.trim().isEmpty()) continue;
                LinearLayout item = new LinearLayout(this);
                item.setGravity(Gravity.CENTER_VERTICAL);
                item.setPadding(dp(12),dp(8),dp(6),dp(8));
                item.setBackground(round(Color.WHITE,12));
                TextView t = text("□  " + task, 14, navy, false);
                item.addView(t,new LinearLayout.LayoutParams(0,-2,1));
                Button del = button("Quitar", false);
                del.setOnClickListener(v -> {
                    List<String> values = new ArrayList<>(Arrays.asList(prefs.getString("tasks","").split("\\|")));
                    values.remove(task);
                    prefs.edit().putString("tasks", android.text.TextUtils.join("|", values)).apply();
                    render[0].run();
                });
                item.addView(del);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
                lp.setMargins(0,dp(6),0,dp(6));
                list.addView(item,lp);
            }
        };
        add.setOnClickListener(v -> {
            String value = field.getText().toString().trim();
            if (value.isEmpty()) { field.setError("Escribe una tarea"); return; }
            String old = prefs.getString("tasks", "Leer páginas 12 y 13|Terminar actividad de Ciencias|Preparar materiales de Tecnología");
            prefs.edit().putString("tasks", old.isEmpty()?value:old+"|"+value).apply();
            field.setText("");
            render.run();
        });
        render.run();
    }

    private void grades() {
        title("Calificaciones", "Datos ficticios para probar el diseño.");
        String[][] rows = {{"Español","9.0"},{"Matemáticas","8.5"},{"Ciencias","9.2"},{"Inglés","9.5"},{"Tecnología","10.0"},{"Artes","9.0"}};
        for (String[] r : rows) {
            LinearLayout line = new LinearLayout(this);
            line.setGravity(Gravity.CENTER_VERTICAL);
            line.setPadding(dp(14),dp(13),dp(14),dp(13));
            line.setBackground(round(Color.WHITE,12));
            TextView subject = text(r[0],15,navy,true);
            line.addView(subject,new LinearLayout.LayoutParams(0,-2,1));
            line.addView(text(r[1],18,blue,true));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2);
            lp.setMargins(0,dp(5),0,dp(5));
            content.addView(line,lp);
        }
        note("Las notas son únicamente de muestra. No representan un historial académico real.");
    }

    private void schedule() {
        title("Horario", "Horario ilustrativo · editable en futuras versiones.");
        String[][] data = {{"7:00 – 7:50","Español"},{"7:50 – 8:40","Matemáticas"},{"8:40 – 9:30","Ciencias"},{"9:30 – 10:00","Receso"},{"10:00 – 10:50","Inglés"},{"10:50 – 11:40","Tecnología"},{"11:40 – 12:30","Artes"},{"12:30 – 1:10","Tutoría"}};
        for (String[] d : data) card(d[0], d[1]);
    }

    private void profile() {
        title("Mi perfil", "Personaliza cómo aparece tu perfil en este dispositivo.");
        content.addView(text("Nombre",14,navy,true));
        EditText name = edit("Tu nombre");
        name.setText(prefs.getString("name","Estudiante"));
        content.addView(name);
        content.addView(text("Grupo",14,navy,true));
        EditText group = edit("Ej. 1°D");
        group.setText(prefs.getString("group","Mi grupo"));
        content.addView(group);
        Button save = button("Guardar perfil", true);
        content.addView(save);
        TextView status = text("",13,blue,false);
        content.addView(status);
        save.setOnClickListener(v -> {
            prefs.edit().putString("name",name.getText().toString().trim()).putString("group",group.getText().toString().trim()).apply();
            status.setText("Perfil guardado en este dispositivo.");
        });
        card("Privacidad", "Tu perfil se guarda localmente en este teléfono. No se envía a ningún servidor.");
    }

    private void title(String heading, String subtitle) {
        content.addView(text(heading,24,navy,true));
        content.addView(text(subtitle,14,Color.DKGRAY,false));
        View spacer = new View(this);
        content.addView(spacer,new LinearLayout.LayoutParams(1,dp(8)));
    }
    private void section(String value) { content.addView(text(value,17,navy,true)); }
    private void card(String heading, String body) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(15),dp(13),dp(15),dp(13));
        box.setBackground(round(Color.WHITE,14));
        box.addView(text(heading,16,navy,true));
        box.addView(text(body,14,Color.DKGRAY,false));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,dp(6),0,dp(6));
        content.addView(box,p);
    }
    private void note(String s) {
        TextView t = text(s,12,Color.DKGRAY,false);
        t.setPadding(dp(4),dp(12),dp(4),dp(12));
        content.addView(t);
    }
    private TextView text(String s,int size,int color,boolean bold) {
        TextView t = new TextView(this);
        t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(null,Typeface.BOLD);
        t.setPadding(0,dp(5),0,dp(5));
        return t;
    }
    private EditText edit(String hint) {
        EditText e = new EditText(this);
        e.setSingleLine(true); e.setTextSize(15); e.setHint(hint);
        e.setPadding(dp(12),dp(8),dp(12),dp(8));
        e.setBackground(round(Color.WHITE,10));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1,-2);
        p.setMargins(0,dp(6),0,dp(8)); e.setLayoutParams(p);
        return e;
    }
    private Button button(String label,boolean primary) {
        Button b = new Button(this);
        b.setText(label); b.setAllCaps(false); b.setTextSize(14);
        b.setTextColor(primary?Color.WHITE:navy);
        b.setBackground(round(primary?blue:Color.rgb(230,238,248),10));
        b.setPadding(dp(8),0,dp(8),0);
        return b;
    }
    private GradientDrawable round(int color,int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius)); return d;
    }
    private int dp(int v) { return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
}
