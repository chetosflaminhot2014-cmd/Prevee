package mx.preeve.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(10, 14, 22);
    private static final int PANEL = Color.rgb(22, 29, 43);
    private static final int TEXT = Color.rgb(239, 243, 250);
    private static final int MUTED = Color.rgb(154, 167, 187);
    private static final int BLUE = Color.rgb(91, 145, 255);
    private WebView web;
    private EditText address;
    private TextView status;

    @Override public void onCreate(Bundle saved) {
        super.onCreate(saved);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        buildUi();
        web.loadUrl("https://www.google.com");
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(12), dp(10), dp(12), 0);

        TextView brand = new TextView(this);
        brand.setText("NEXORA  /  BROWSER");
        brand.setTextColor(TEXT);
        brand.setTextSize(15);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        brand.setLetterSpacing(.08f);
        brand.setPadding(dp(4), dp(8), 0, dp(12));
        root.addView(brand);

        LinearLayout bar = new LinearLayout(this);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(10), dp(3), dp(5), dp(3));
        bar.setBackground(round(PANEL, 18));
        address = new EditText(this);
        address.setSingleLine(true);
        address.setTextSize(14);
        address.setTextColor(TEXT);
        address.setHintTextColor(MUTED);
        address.setHint("Buscar o escribir dirección");
        address.setBackgroundColor(Color.TRANSPARENT);
        address.setImeOptions(EditorInfo.IME_ACTION_GO);
        bar.addView(address, new LinearLayout.LayoutParams(0, dp(46), 1));
        Button go = button("Ir", BLUE);
        bar.addView(go, new LinearLayout.LayoutParams(dp(54), dp(40)));
        root.addView(bar);

        address.setOnEditorActionListener((v, action, event) -> {
            navigate(address.getText().toString());
            return true;
        });
        go.setOnClickListener(v -> navigate(address.getText().toString()));

        LinearLayout controls = new LinearLayout(this);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(7), 0, dp(7));
        Button back = button("‹", PANEL);
        Button forward = button("›", PANEL);
        Button reload = button("↻", PANEL);
        Button home = button("⌂", PANEL);
        Button ai = button("IA", BLUE);
        controls.addView(back, weightButton());
        controls.addView(forward, weightButton());
        controls.addView(reload, weightButton());
        controls.addView(home, weightButton());
        controls.addView(ai, weightButton());
        root.addView(controls);
        back.setOnClickListener(v -> { if (web.canGoBack()) web.goBack(); });
        forward.setOnClickListener(v -> { if (web.canGoForward()) web.goForward(); });
        reload.setOnClickListener(v -> web.reload());
        home.setOnClickListener(v -> web.loadUrl("https://www.google.com"));
        ai.setOnClickListener(v -> showAssistantNotice());

        status = new TextView(this);
        status.setText("Navegación web");
        status.setTextColor(MUTED);
        status.setTextSize(11);
        status.setPadding(dp(3), dp(2), 0, dp(5));
        root.addView(status);

        web = new WebView(this);
        web.setBackgroundColor(Color.WHITE);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setSupportMultipleWindows(false);
        web.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                if ("http".equals(scheme) || "https".equals(scheme)) return false;
                return true;
            }
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap icon) {
                address.setText(url);
                status.setText("Cargando página…");
            }
            @Override public void onPageFinished(WebView view, String url) {
                address.setText(url);
                status.setText(view.getTitle() == null ? "Página lista" : view.getTitle());
            }
        });
        root.addView(web, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
    }

    private void navigate(String raw) {
        String value = raw == null ? "" : raw.trim();
        if (value.isEmpty()) return;
        String url;
        if (value.matches("(?i)^https?://.*")) url = value;
        else if (value.matches("(?i)^[a-z0-9.-]+\\.[a-z]{2,}(/.*)?$")) url = "https://" + value;
        else url = "https://www.google.com/search?q=" + Uri.encode(value);
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
            .hideSoftInputFromWindow(address.getWindowToken(), 0);
        web.loadUrl(url);
    }

    private void showAssistantNotice() {
        new AlertDialog.Builder(this)
            .setTitle("Asistente Nexora")
            .setMessage("La interfaz del asistente está preparada para la siguiente fase. Para responder con IA real, hay que conectar un servicio de modelo mediante un servidor seguro. Esta versión no inventa respuestas ni contiene una IA simulada.")
            .setPositiveButton("Entendido", null)
            .show();
    }

    private Button button(String label, int color) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(TEXT);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setBackground(round(color, 14));
        return b;
    }
    private LinearLayout.LayoutParams weightButton() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(42), 1);
        p.setMargins(dp(2), 0, dp(2), 0);
        return p;
    }
    private android.graphics.drawable.GradientDrawable round(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }
    private int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density + 0.5f); }

    @Override public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
