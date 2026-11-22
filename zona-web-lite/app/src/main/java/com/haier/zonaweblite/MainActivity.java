package com.haier.zonaweblite;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Independent one-WebView shell for the two official Zona web portals.
 * NOT a modified/repackaged Zona APK. No proprietary content, API or scraper.
 * No player replacement: playback and audio are provided by the official site.
 */
public final class MainActivity extends Activity {
    private static final String OLD_SITE="https://w1.zona.plus/";
    private static final String NEW_SITE="https://w1.zona.im/";
    private static final int BACKGROUND=0xff101521, CARD=0xff202e43, AQUA=0xff83e5f3;
    private FrameLayout root;
    private WebView browser;
    private View fullscreen;
    private WebChromeClient.CustomViewCallback fullscreenCallback;
    private SharedPreferences preferences;
    private boolean posters;

    private int dp(int x){return Math.round(x*getResources().getDisplayMetrics().density);}
    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        preferences=getSharedPreferences("zona_web_lite",Context.MODE_PRIVATE);
        posters=preferences.getBoolean("images",false);
        showMenu();
    }
    private GradientDrawable background(int fill,int stroke){
        GradientDrawable g=new GradientDrawable();
        g.setColor(fill);g.setCornerRadius(dp(12));g.setStroke(dp(2),stroke);
        return g;
    }
    private TextView label(String value,int size,int color){
        TextView t=new TextView(this);
        t.setText(value);t.setTextSize(size);t.setTextColor(color);
        t.setGravity(Gravity.CENTER_VERTICAL);
        return t;
    }
    private Button option(String value,Runnable action){
        Button b=new Button(this);
        b.setText(value);b.setTextColor(Color.WHITE);b.setAllCaps(false);
        b.setTextSize(17);b.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
        b.setPadding(dp(22),0,dp(20),0);
        b.setBackground(background(CARD,0xff3b506a));
        b.setFocusable(true);
        b.setOnFocusChangeListener((view,focused)->b.setBackground(
                background(focused?0xff25495b:CARD,focused?AQUA:0xff3b506a)));
        b.setOnClickListener(v->action.run());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(66));
        lp.setMargins(0,0,0,dp(12));
        b.setLayoutParams(lp);
        return b;
    }
    private void startRoot(){
        root=new FrameLayout(this);
        root.setBackgroundColor(BACKGROUND);
        setContentView(root);
    }
    private void showMenu(){
        hideFullscreen();
        stopWeb();
        startRoot();
        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);root.addView(scroll,new FrameLayout.LayoutParams(-1,-1));
        LinearLayout layout=new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(28),dp(18),dp(28),dp(20));
        scroll.addView(layout);
        TextView title=label("Zona Web Lite · Haier",26,Color.WHITE);
        title.setTypeface(null,1);layout.addView(title);
        TextView sub=label("Минимальная оболочка. Плеер и озвучка — от сайта Zona.",14,0xffb4c3d4);
        sub.setPadding(0,0,0,dp(16));layout.addView(sub);
        String last=preferences.getString("last_page","");
        if(last.startsWith("https://")){
            layout.addView(option("Продолжить: последняя страница",()->load(last)));
        }
        layout.addView(option("Zona для старых телевизоров",()->load(OLD_SITE)));
        layout.addView(option("Zona (новая веб-версия)",()->load(NEW_SITE)));
        layout.addView(option(posters?"Постеры: ВКЛ → отключить":"Постеры: ВЫКЛ → включить",()->{
            posters=!posters;preferences.edit().putBoolean("images",posters).apply();showMenu();
        }));
        layout.addView(option("Очистить только веб-кэш",()->{
            // Preserve cookies, login state, localStorage and last viewed page.
            WebView temporary=new WebView(this);temporary.clearCache(true);temporary.destroy();
            new android.app.AlertDialog.Builder(this).setMessage(
                "Кэш загруженных ресурсов очищен. Сессия и последняя страница сохранены.")
                .setPositiveButton("OK",null).show();
        }));
        layout.addView(option("Выход",this::finish));
        TextView footer=label("MENU — вернуться сюда · BACK — назад · OK — выбрать.\n"
            +"Сохранение точной секунды фильма зависит от веб-сайта. Это неофициальный клиент.",13,0xff9cacc0);
        footer.setPadding(0,dp(4),0,dp(8));
        layout.addView(footer);
        for(int i=0;i<layout.getChildCount();i++){
            View view=layout.getChildAt(i);
            if(view instanceof Button){view.requestFocus();break;}
        }
    }
    private void load(String url){
        if(!url.startsWith("https://"))return;
        startRoot();
        browser=new WebView(this);
        browser.setBackgroundColor(BACKGROUND);
        browser.setFocusable(true);
        browser.setFocusableInTouchMode(true);
        WebSettings settings=browser.getSettings();
        settings.setJavaScriptEnabled(true); // required by Zona's site
        settings.setDomStorageEnabled(true); // persist site session/history if site supports it
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setLoadsImagesAutomatically(posters);
        settings.setBlockNetworkImage(!posters);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        CookieManager.getInstance().setAcceptCookie(true);
        browser.setWebViewClient(new WebViewClient(){
            @Override public void onPageFinished(WebView view,String url){
                super.onPageFinished(view,url);
                if(url!=null && url.startsWith("https://")){
                    preferences.edit().putString("last_page",url).apply();
                }
            }
        });
        browser.setWebChromeClient(new WebChromeClient(){
            @Override public void onShowCustomView(View view, CustomViewCallback callback){
                if(fullscreen!=null){callback.onCustomViewHidden();return;}
                fullscreen=view;fullscreenCallback=callback;
                root.addView(fullscreen,new FrameLayout.LayoutParams(-1,-1));
                browser.setVisibility(View.GONE);
            }
            @Override public void onHideCustomView(){hideFullscreen();}
        });
        root.addView(browser,new FrameLayout.LayoutParams(-1,-1));
        browser.loadUrl(url);
        browser.requestFocus();
    }
    private void hideFullscreen(){
        if(fullscreen!=null){
            if(root!=null && fullscreen.getParent()==root)root.removeView(fullscreen);
            fullscreen=null;
            if(fullscreenCallback!=null){fullscreenCallback.onCustomViewHidden();fullscreenCallback=null;}
            if(browser!=null)browser.setVisibility(View.VISIBLE);
        }
    }
    private void stopWeb(){
        if(browser==null)return;
        String url=browser.getUrl();
        if(url!=null && url.startsWith("https://"))
            preferences.edit().putString("last_page",url).apply();
        browser.stopLoading();
        if(root!=null && browser.getParent()==root)root.removeView(browser);
        browser.destroy();browser=null;
    }
    @Override public void onBackPressed(){
        if(fullscreen!=null){hideFullscreen();return;}
        if(browser!=null){
            if(browser.canGoBack())browser.goBack();else showMenu();
        }else finish();
    }
    @Override public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_MENU){showMenu();return true;}
        return super.onKeyDown(keyCode,event);
    }
    @Override protected void onPause(){
        if(browser!=null)browser.onPause();
        CookieManager.getInstance().flush();
        super.onPause();
    }
    @Override protected void onResume(){
        super.onResume();
        if(browser!=null)browser.onResume();
    }
    @Override protected void onDestroy(){
        hideFullscreen();stopWeb();super.onDestroy();
    }
}
