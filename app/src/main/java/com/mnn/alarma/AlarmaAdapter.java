package com.mnn.alarma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

public class AlarmaAdapter extends RecyclerView.Adapter<AlarmaAdapter.MyViewHolder> {
    private Context mContext;
    private List<Alarma> alarmaList;
    private Calendar calendar;
    TinyDB tinyDB;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtHora, txtDias;
        Switch activateAlarm;
        CardView alarmasCard;
        ImageView btnEliminar;

        MyViewHolder(View view) {
            super(view);
            txtTitulo = (TextView) view.findViewById(R.id.tituloAlarma);
            txtDias = (TextView) view.findViewById(R.id.dias);
            txtHora = (TextView) view.findViewById(R.id.horaAlarma);
            alarmasCard = (CardView) view.findViewById(R.id.card_view);
            activateAlarm = (Switch) view.findViewById(R.id.activateAlarmSwitch);
            btnEliminar = (ImageView) view.findViewById(R.id.btnEliminar);
            view.setOnClickListener(v -> Toast.makeText(mContext, "Clic en ", Toast.LENGTH_SHORT).show());
        }
    }

    public AlarmaAdapter(Context mContext, List<Alarma> alarmaList) {
        this.mContext = mContext;
        this.alarmaList = alarmaList;
        calendar = Calendar.getInstance();
        tinyDB = new TinyDB(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_card, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Alarma alarma = alarmaList.get(position);
        String horaFormateada = (alarma.getHora() < 10) ? String.valueOf("0" + alarma.getHora()) : String.valueOf(alarma.getHora());
        String minutoFormateado = (alarma.getMinuto() < 10) ? String.valueOf("0" + alarma.getMinuto()) : String.valueOf(alarma.getMinuto());
        holder.txtTitulo.setText(alarma.getTitulo());
        //holder.txtHora.setText(getHoraYmin(alarma.getHora(), alarma.getMinuto()));
        holder.txtHora.setText(horaFormateada + ":" + minutoFormateado);
        holder.txtDias.setText(showDiasFromList(alarma.getDias()));
        holder.activateAlarm.setChecked(alarma.isActivado());
        if (!alarma.isActivado()){
            desPintarTxts(holder.txtDias);
            desPintarTxts(holder.txtTitulo);
            desPintarTxts(holder.txtHora);
        }
        // Cambiando el estilo cuando switch == true
        holder.activateAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                setAlarm(alarma, alarma.getDiasNumeric(), position);
                pintarTxts(holder.txtDias);
                pintarTxts(holder.txtHora);
                pintarTxts(holder.txtTitulo);
            }
            else {
                cancelAlarm(alarma,false, position);
                desPintarTxts(holder.txtDias);
                desPintarTxts(holder.txtTitulo);
                desPintarTxts(holder.txtHora);
            }
        });
        holder.alarmasCard.setOnClickListener((v) -> {
            //TODO: hacer actualización de datos de alarma
//            Intent intent = new Intent(mContext, AggRecordatorio.class);
//            intent.putExtra("isUpdate", true);
//            intent.putExtra("alarmName", alarma.getTitulo());
//            intent.putExtra("tonoName", alarma.getTono());
//            intent.putExtra("horaAlarma", alarma.getHora());
//            intent.putExtra("minAlarma", alarma.getMinuto());
//            intent.putIntegerArrayListExtra("dias", (ArrayList<Integer>) alarma.getDiasNumeric());
//            mContext.startActivity(intent);
            Toast.makeText(mContext, alarma.getAlarmaId(), Toast.LENGTH_SHORT).show();
        });
        holder.btnEliminar.setOnClickListener((View v) -> {
            cancelAlarm(alarma, true,position);
        });
    }
    private void pintarTxts(TextView t){
        t.setTextColor(mContext.getResources().getColor(R.color.hora_alarma));
    }
    private void desPintarTxts(TextView t){
        t.setTextColor(mContext.getResources().getColor(R.color.dias_gray));
    }
    @Override
    public int getItemCount() {
        return alarmaList.size();
    }
    private String showDiasFromList(List<String> diasAlarma) {
        if (diasAlarma.size() == 7)
            return "Todos los días";
        return StringUtils.join(diasAlarma, " • ");
    }
    //las elimina de la lista
    private void removerAlarmas(Alarma alarma, int position){
        try {
            alarmaList.remove(position);
            tinyDB.putListAlarm("allAlarmas", alarmaList);
            notifyDataSetChanged();
            Toast.makeText(mContext, "Alarma " + alarma.getTitulo() + " eliminada",
                    Toast.LENGTH_SHORT).show();
        }
        catch (Exception ex) {
            Toast.makeText(mContext, "Error eliminando las alarmas", Toast.LENGTH_LONG).show();
        }
    }
    //Cancela las larmas programadas en el AlarmReceiver
    private void cancelAlarm(Alarma alarma, boolean vaAeliminar, int pos) {
        try {
            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(mContext, AlarmReceiver.class);
            List<Integer> alarmasId = new ArrayList<>(tinyDB.getListInt("alarmasId"));
            for (int id : alarmasId){
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, id,
                        intent, 0);
                alarmManager.cancel(pendingIntent);
            }
            alarma.setActivado(false);
            alarmaList.set(pos, alarma);
            tinyDB.putListAlarm("allAlarmas", alarmaList);
            notifyDataSetChanged();
            //false significa q solo va a desactivar, no lo elimina de la lista
            if(vaAeliminar)
                removerAlarmas(alarma, pos);
        } catch (Exception e) {
            Toast.makeText(mContext, "Ocurrió un error cancelando las alarmas",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void setAlarm(Alarma alarma, List<Integer> diasRepeticion, int pos){
        Calendar alarmCalendar = Calendar.getInstance();
        List<Integer> alarmaIds = new ArrayList<>();
        try {
            for (int dia : diasRepeticion) {
                int alarmaId = Integer.parseInt(RandomStringUtils.randomNumeric(5));
                alarmaIds.add(alarmaId);
                alarmCalendar.set(Calendar.DAY_OF_WEEK, dia);
                alarmCalendar.set(Calendar.HOUR_OF_DAY, alarma.getHora());
                alarmCalendar.set(Calendar.MINUTE, alarma.getMinuto());
                alarmCalendar.set(Calendar.SECOND, 0);
                alarmCalendar.set(Calendar.MILLISECOND, 0);
                if(alarmCalendar.before(Calendar.getInstance())) {
                    //q suene el día de la otra semana
                    alarmCalendar.add(Calendar.DATE, 7);
                }
                //String leftTime = "AlarmSet  " + alarmCalendar.getTime();
                Intent intent = new Intent(mContext, AlarmReceiver.class);
                intent.putExtra("nombre", alarma.getTitulo());
                intent.putExtra("mensaje", alarma.getNota());
                intent.putExtra("sonido", alarma.getUriTonePath());
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, alarmaId,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), pendingIntent);
            }
            alarma.setActivado(true);
            alarmaList.set(pos, alarma);
            tinyDB.putListAlarm("allAlarmas", alarmaList);
            notifyDataSetChanged();
            tinyDB.putListInt("alarmasId", alarmaIds);
        }
        catch (Exception ex){
            Toast.makeText(mContext, "Error re-creando las alarmas " + ex.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

    }
}
