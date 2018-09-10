package com.mnn.alarma;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AlarmaAdapter adapter;
    private List<Alarma> alarmaList;
    private TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinyDB = new TinyDB(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        alarmaList = new ArrayList<>();
        adapter = new AlarmaAdapter(this, alarmaList);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAggRecordatorio);
        fab.setOnClickListener(aggRecordatorio);
        //
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        //
        getAllAlarmas();
    }

    public void getAllAlarmas() {
        alarmaList.clear();
        alarmaList.addAll(tinyDB.getListAlarm("allAlarmas", Alarma.class));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllAlarmas();
    }
    public View.OnClickListener aggRecordatorio = (v -> {
        startActivity(new Intent(this, AggRecordatorio.class));
    });
}
