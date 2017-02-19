package com.brandon.chavez.android.app.sngapp.utilidades;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.brandon.chavez.android.app.sngapp.R;
import java.util.List;

public class AdapterPacienteHistoriales extends RecyclerView.Adapter<AdapterPacienteHistoriales.ViewHolder> {

    private Context context;
    private List<DatosPacienteHistoriales> lDatosPacienteHistoriales;
    private OnHistorialSelected onHistorialSelected;

    public AdapterPacienteHistoriales(Context context, List<DatosPacienteHistoriales> lDatosPacienteHistoriales, OnHistorialSelected onHistorialSelected)
    {
        this.context = context;
        this.lDatosPacienteHistoriales = lDatosPacienteHistoriales;
        try{
            this.onHistorialSelected = onHistorialSelected;
        }catch (ClassCastException e){
            throw new ClassCastException("must implement OnHistorialSelected");
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CardView cvItemPacienteHistorialesAdap;
        public TextView tvCodigoItemPacienteHistorialesAdap;
        public TextView tvNombreItemPacienteHistorialesAdap;
        public TextView tvDescripcionItemPacienteHistorialesAdap;
        public TextView tvAlturaItemPacienteHistorialesAdap;
        public TextView tvPesoItemPacienteHistorialesAdap;
        public TextView tvImcItemPacienteHistorialesAdap;
        public TextView tvFechaItemPacienteHistorialesAdap;
        public TextView tvHoraItemPacienteHistorialesAdap;

        public ViewHolder(View view)
        {
            super(view);
            cvItemPacienteHistorialesAdap = (CardView) view.findViewById(R.id.cvItemPacienteHistoriales);
            tvCodigoItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvCodigoItemPacienteHistoriales);
            tvNombreItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvNombreItemPacienteHistoriales);
            tvDescripcionItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvDescripcionItemPacienteHistoriales);
            tvAlturaItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvAlturaItemPacienteHistoriales);
            tvPesoItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvPesoItemPacienteHistoriales);
            tvImcItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvImcItemPacienteHistoriales);
            tvFechaItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvFechaItemPacienteHistoriales);
            tvHoraItemPacienteHistorialesAdap = (TextView) view.findViewById(R.id.tvHoraItemPacienteHistoriales);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_historiales, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final DatosPacienteHistoriales datosPacienteHistoriales = lDatosPacienteHistoriales.get(position);
        holder.tvCodigoItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getCodigoHistorial());
        holder.tvNombreItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getNombreHistorial());
        holder.tvDescripcionItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getDescripcionHistorial());
        holder.tvAlturaItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getAlturaHistorial());
        holder.tvPesoItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getPesoHistorial());
        holder.tvImcItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getImcHistorial());
        holder.tvFechaItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getFechaHistorial());
        holder.tvHoraItemPacienteHistorialesAdap.setText(datosPacienteHistoriales.getHoraHistorial());
        holder.cvItemPacienteHistorialesAdap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                onHistorialSelected.onHistorialClick(datosPacienteHistoriales);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return lDatosPacienteHistoriales.size();
    }

    public interface OnHistorialSelected
    {
        void onHistorialClick(DatosPacienteHistoriales datosPacienteHistoriales);
    }

}
