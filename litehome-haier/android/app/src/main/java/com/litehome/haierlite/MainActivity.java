package com.litehome.haierlite;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.TextUtils;
import android.util.TypedValue;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Native, self-contained Android 7.1 screen harness for LiteHome Haier Lite.
 * It renders ACTUAL Android views and is designed to be screenshot-tested on
 * the GitHub-hosted Android Emulator. It is a UI harness, not the final TV APK.
 */
public final class MainActivity extends Activity {
    private static final int WHITE=Color.WHITE, CYAN=0xff83e5f3, SUB=0xffc2cedd;
    private static final String[][] HOME={
            {"TV","Телеканалы","Кабельное и эфирное ТВ"},
            {"▶","Zona","Фильмы и сериалы"},
            {"▦","Приложения","Все установленные"},
            {"⌕","Поиск","Фильмы в интернете"},
            {"◈","Источники","TV / HDMI / AV"},
            {"⚙","Настройки","Обои и быстрый запуск"}};
    private static final String[][] SETTINGS={
            {"›","Обои","Градиент или своя фотография"},
            {"›","Приложение телеканалов","Назначить кнопку ТВ"},
            {"›","Приложение Zona","Назначить кнопку Zona"},
            {"›","Приложение источников","Не заменяет кнопку SOURCE"},
            {"›","Часы-заставка","Простые часы на тёмном фоне"},
            {"›","Погода","Открыть в браузере"},
            {"›","Системные настройки","Настройки Android / Haier"}};
    private int palette=0;
    private String page="home";
    private FrameLayout root;

    private float scale(){
        android.util.DisplayMetrics d=getResources().getDisplayMetrics();
        return Math.min(d.widthPixels/960f,d.heightPixels/540f);
    }
    private int px(float x){return Math.max(1,Math.round(x*scale()));}
    private TextView text(String value,int size,int color,boolean bold){
        TextView v=new TextView(this);
        v.setText(value);
        v.setTextColor(color);
        v.setTextSize(TypedValue.COMPLEX_UNIT_PX,size*scale());
        v.setGravity(Gravity.CENTER_VERTICAL);
        if(bold)v.setTypeface(null,1);
        return v;
    }
    private GradientDrawable bg(int color,int border){
        GradientDrawable d=new GradientDrawable();
        d.setColor(color); d.setCornerRadius(px(17)); d.setStroke(px(2),border);
        return d;
    }
    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if("settings".equals(getIntent().getStringExtra("screen")))showSettings();
        else showHome();
    }
    @Override public void onBackPressed(){showHome();}
    private LinearLayout createPage(){
        root=new FrameLayout(this);
        int[][] colors={{0xff1a2231,0xff0a0e19},{0xff092533,0xff0d1624},{0xff36221e,0xff11141e}};
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,colors[palette]));
        setContentView(root);
        LinearLayout all=new LinearLayout(this);
        all.setOrientation(LinearLayout.VERTICAL);
        all.setPadding(px(42),px(26),px(42),px(22));
        root.addView(all,new FrameLayout.LayoutParams(-1,-1));
        return all;
    }
    private void header(LinearLayout parent,String name){
        LinearLayout line=new LinearLayout(this);line.setGravity(Gravity.CENTER_VERTICAL);
        TextView title=text(name,30,WHITE,true);
        line.addView(title);
        if("LiteHome".equals(name)){
            TextView edition=text("HAIER LITE",13,CYAN,true);
            edition.setPadding(px(11),0,0,0);
            line.addView(edition);
        }
        View space=new View(this);
        line.addView(space,new LinearLayout.LayoutParams(0,1,1));
        TextView time=text(new SimpleDateFormat("HH:mm",Locale.forLanguageTag("ru-RU")).format(new Date()),27,0xffccd7e2,false);
        line.addView(time);
        parent.addView(line,new LinearLayout.LayoutParams(-1,px(58)));
    }
    private void foot(LinearLayout all,String value){
        TextView label=text(value,15,0xffacbcd0,false);
        label.setPadding(px(5),px(10),0,0);
        all.addView(label);
    }
    private View card(String[] item,Runnable onClick){
        LinearLayout col=new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER_VERTICAL);
        col.setPadding(px(23),px(15),px(15),px(13));
        col.setBackground(bg(0xbb1d2a3f,0x557a8da6));
        col.setFocusable(true);col.setClickable(true);
        col.addView(text(item[0],23,0xff7cdcef,true));
        TextView title=text(item[1],22,WHITE,true);
        title.setSingleLine(true);title.setEllipsize(TextUtils.TruncateAt.END);
        col.addView(title);
        TextView detail=text(item[2],14,SUB,false);
        detail.setSingleLine(true);detail.setEllipsize(TextUtils.TruncateAt.END);
        col.addView(detail);
        col.setOnFocusChangeListener((v,focus)->{
            v.setBackground(bg(focus?0xed24445a:0xbb1d2a3f,focus?CYAN:0x557a8da6));
            v.setElevation(focus?px(8):0);
        });
        col.setOnClickListener(v->onClick.run());
        return col;
    }
    private void cardRow(LinearLayout all,View a,View b,View c){
        LinearLayout row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0,px(7),0,px(7));
        for(View v:new View[]{a,b,c}){
            LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,px(145),1);
            p.setMargins(px(5),0,px(5),0);
            row.addView(v,p);
        }
        all.addView(row);
    }
    public void showHome(){
        page="home";
        LinearLayout all=createPage();header(all,"LiteHome");
        TextView lead=text("Ваш телевизор. Ничего лишнего.",17,0xffb6c4d7,false);
        lead.setPadding(px(6),0,0,px(14));all.addView(lead);
        View tv=card(HOME[0],()->dialog("Телеканалы","Кабельное ТВ на Haier открывается штатным SOURCE либо назначенным ТВ-приложением."));
        View zona=card(HOME[1],()->dialog("Zona","В настоящем лаунчере откроется установленное приложение Zona."));
        View apps=card(HOME[2],()->dialog("Приложения","На настоящем телевизоре здесь будет список установленных программ."));
        View search=card(HOME[3],()->dialog("Поиск","На телевизоре запускается поиск фильмов."));
        View sources=card(HOME[4],()->dialog("Источники","Для телевизора используется штатная кнопка SOURCE."));
        View settings=card(HOME[5],this::showSettings);
        cardRow(all,tv,zona,apps);cardRow(all,search,sources,settings);
        foot(all,"OK — выбрать    ·    BACK — назад    ·    SOURCE — ТВ / HDMI");
        tv.requestFocus();
    }
    private void setting(LinearLayout all,String[] item,Runnable action){
        LinearLayout row=new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(px(22),px(8),px(16),px(8));
        row.setBackground(bg(0xbb1d2a3f,0x557a8da6));
        row.setFocusable(true);row.setClickable(true);
        TextView arrow=text(item[0],29,0xff7cdcef,false);
        row.addView(arrow,new LinearLayout.LayoutParams(px(32),-1));
        LinearLayout labels=new LinearLayout(this);labels.setOrientation(LinearLayout.VERTICAL);
        TextView title=text(item[1],22,WHITE,true); title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);labels.addView(title);
        TextView sub=text(item[2],14,SUB,false);labels.addView(sub);
        row.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
        row.setOnFocusChangeListener((v,focused)->v.setBackground(bg(focused?0xed24445a:0xbb1d2a3f,focused?CYAN:0x557a8da6)));
        row.setOnClickListener(v->action.run());
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,px(91));
        lp.setMargins(px(5),px(5),px(5),px(5));all.addView(row,lp);
    }
    public void showSettings(){
        page="settings";
        LinearLayout all=createPage();header(all,"Настройки LiteHome");
        ScrollView scroll=new ScrollView(this);
        all.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);
        for(int i=0;i<SETTINGS.length;i++){
            final int id=i;
            setting(list,SETTINGS[i],()->{
                if(id==0)new AlertDialog.Builder(this)
                    .setTitle("Фон рабочего стола")
                    .setItems(new String[]{"Графит","Глубокий океан","Тёплый вечер"},(d,n)->{palette=n;showSettings();})
                    .setNegativeButton("Отмена",null).show();
                else dialog(SETTINGS[id][1],"Настройка доступна в настоящем LiteHome. Это тестовая сборка для GitHub Emulator.");
            });
        }
        foot(all,"Приложение не удаляет штатный лаунчер и не меняет каналы.");
        if(list.getChildCount()>0)list.getChildAt(0).requestFocus();
    }
    private void dialog(String title,String message){
        new AlertDialog.Builder(this).setTitle(title).setMessage(message).setPositiveButton("OK",null).show();
    }
}
