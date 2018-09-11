package com.mnn.alarma;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AggRecordatorio extends AppCompatActivity {
    private List<ToggleButton> dayToggles = new ArrayList<>();
    public static final int GET_TONES_REQUEST_ID=777;
    private Uri tonoChosenUri;
    private String[] diasArray;
    List<Integer> diasNumericList;
    private String alarmName="", tonoName="";
    private Button btnHecho, btnCancelar;
    private LinearLayout btnHora, btnBuscarTono, btnTituloAlarma;
    private TextView txtTonoAlarma, txtHoraAlarma, txtTitulo;
    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";
    public final Calendar c = Calendar.getInstance();
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);
    private int horaAlarma = 0, minutoAlarma = 0;
    TinyDB tinyDB;
    Alarma alarma = null;
    Boolean isUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agg_recordatorio);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        setTitle("Agregar Recordatorio");
        bindViews();

        diasArray = getResources().getStringArray(R.array.dias);
        tinyDB = new TinyDB(this);
        btnTituloAlarma.setOnClickListener(dialogAlarmName);
        btnBuscarTono.setOnClickListener(getTones);
        btnCancelar.setOnClickListener(cancelar);
        btnHora.setOnClickListener(escogerHora);
        btnHecho.setOnClickListener(guardarAlarma);

        //TODO: hacer actualización de datos de alarma
//        isUpdate = false;
//        Intent intent = getIntent();
//        isUpdate = intent.getBooleanExtra("isUpdate", false);
//        if (isUpdate) {
//            setTitle("Actualizar Recordatorio");
//            alarmName = intent.getStringExtra("alarmName");
//            String tonoAlarma = intent.getStringExtra("tonoName");
//            int hora = intent.getIntExtra("horaAlarma",0);
//            int min = intent.getIntExtra("minAlarma",0);
//            String AM_PM = (hora < 12) ? "a.m." : "p:m";
//            txtTitulo.setText("Nombre de Alarma: " + alarmName);
//            txtTonoAlarma.setText("Tono de alarma: " + tonoAlarma);
//            txtHoraAlarma.setText("Hora de alarma: " + formatHoraMin(hora) + DOS_PUNTOS + formatHoraMin(min) + " " + AM_PM);
        //}
    }
    private String formatHoraMin(int h){
        return (h < 10)? String.valueOf(CERO + h) : String.valueOf(h);
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
    private void bindViews() {
        btnTituloAlarma = (LinearLayout) findViewById(R.id.lvTituloAlarma);
        btnHora = (LinearLayout) findViewById(R.id.lvEscogerHora);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnBuscarTono = (LinearLayout) findViewById(R.id.lvBuscarTono);
        txtTonoAlarma = (TextView) findViewById(R.id.txtTonoAlarma);
        txtHoraAlarma = (TextView) findViewById(R.id.txtHoraAlarma);
        txtTitulo = (TextView) findViewById(R.id.txtTituloAlarma);
        btnHecho = (Button) findViewById(R.id.btnHecho);
        dayToggles.add((ToggleButton) findViewById(R.id.sunday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.monday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.tuesday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.wednesday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.thursday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.friday_toggle));
        dayToggles.add((ToggleButton) findViewById(R.id.saturday_toggle));
    }
    public List<String> getSelectedDays() {
        List<String> diasSeleccionados = new ArrayList<>();
        diasNumericList = new ArrayList<>();
        for (int i = 0; i < dayToggles.size(); i++) {
            if (dayToggles.get(i).isChecked()) {
                diasSeleccionados.add(diasArray[i]);
                diasNumericList.add(i+1);
            }
        }
        return diasSeleccionados;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == GET_TONES_REQUEST_ID){
            Uri toneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (toneUri!=null){
                Ringtone ringtone = RingtoneManager.getRingtone(this, toneUri);
                tonoChosenUri = toneUri;
                tonoName = (ringtone.getTitle(this));
                txtTonoAlarma.setText("Tono de alarma: " + tonoName);
            }
        }
        else
            Toast.makeText(this, "Seleccione un tono", Toast.LENGTH_SHORT).show();
    }
    public View.OnClickListener getTones = v -> {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Seleccione un tono");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        AggRecordatorio.this.startActivityForResult(intent, GET_TONES_REQUEST_ID);
    };
    private View.OnClickListener cancelar = v -> finish();
    private View.OnClickListener escogerHora = (v-> {
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String horaFormateada =  formatHoraMin(hourOfDay);
                String minutoFormateado = formatHoraMin(minute);
                String AM_PM = (hourOfDay < 12) ? "a.m." : "p:m";
                minutoAlarma = minute;
                horaAlarma = hourOfDay;
                txtHoraAlarma.setText("Hora de alarma: " + horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
        }, hora, minuto, true);
        recogerHora.show();
    });
    public View.OnClickListener dialogAlarmName = (v ->{
        AlertDialog.Builder alert = new AlertDialog.Builder(AggRecordatorio.this);
        alert.setTitle("Title");
        final EditText input = new EditText(AggRecordatorio.this);
        input.setSingleLine();
        alert.setView(input).setTitle("Ingresar Nombre de Alarma");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alarmName = input.getText().toString();
                txtTitulo.setText("Nombre de Alarma: " + alarmName);
            }
        });
        alert.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        alert.show();
    });
    private View.OnClickListener guardarAlarma = (View v) -> {
        //TODO : hacer validaciones | calcular tiempo faltante para la alarma
        if ((getSelectedDays().size()==0) ||
                (txtTitulo.getText().equals("")) ||
                (tonoName.equals(""))){
            Toast.makeText(this, "Faltan datos", Toast.LENGTH_LONG).show();
        }
        else{
            //si no puede crear el servicio alarma entonces no lo guarde
            alarma = new Alarma(alarmName,horaAlarma, minutoAlarma,
                    tonoName, getSelectedDays(), "",true, tonoChosenUri.getPath());
            alarma.setDiasNumeric(diasNumericList);
            try {
                setAlarm(horaAlarma, minutoAlarma, diasNumericList);
            }
            catch (Exception ex){
                Toast.makeText(this, "Error creando la alarma" + ex.getMessage(),
                        Toast.LENGTH_LONG).show();
                return;
            }
            List<Alarma> alarmaList = new ArrayList<>(tinyDB.getListAlarm("allAlarmas", Alarma.class));
            alarmaList.add(alarma);
            tinyDB.putListAlarm("allAlarmas", alarmaList);
            Toast.makeText(this, "Alarma Creada", Toast.LENGTH_LONG).show();
            finish();
        }
    };
    private void setAlarm(int hora, int min, List<Integer> diasRepeticion){
        Calendar alarmCalendar = Calendar.getInstance();
        List<Integer> alarmaIds = new ArrayList<>();
        for (int dia : diasRepeticion) {
            int alarmaId = Integer.parseInt(RandomStringUtils.randomNumeric(5));
            alarmaIds.add(alarmaId);
            alarmCalendar.set(Calendar.DAY_OF_WEEK, dia);
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hora);
            alarmCalendar.set(Calendar.MINUTE, min);
            alarmCalendar.set(Calendar.SECOND, 0);
            alarmCalendar.set(Calendar.MILLISECOND, 0);
            if(alarmCalendar.before(Calendar.getInstance())) {
                alarmCalendar.add(Calendar.DATE, 7);
            }
            //String leftTime = "AlarmSet  " + alarmCalendar.getTime();
            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("nombre", alarma.getTitulo());
            intent.putExtra("mensaje", alarma.getNota());
            intent.putExtra("sonido", alarma.getUriTonePath());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmaId,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
        }
        tinyDB.putListInt("alarmasId", alarmaIds);
    }
}
