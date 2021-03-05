package com.example.chatfb;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfb.models.ChatMessage;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdaptadorMensajes extends RecyclerView.Adapter<AdaptadorMensajes.MensajesViewHolder> implements View.OnClickListener {
    private View.OnClickListener listener;
    private ArrayList<ChatMessage> datos;

    public AdaptadorMensajes(ArrayList<ChatMessage> datos) {
        this.datos = datos;
    }

    @Override
    public MensajesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflamos el layout para representar los elementos
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        //creamos un objeto AlumnosViewHolder para obtener las referencias a las vistas del layout y le pasamos la vista inflada con el layout
        MensajesViewHolder avh= new MensajesViewHolder(itemView);
        // fijamos el evento en la vista del elemento que acabamos de construir
        itemView.setOnClickListener(this);
        return avh;
    }

    @Override
    public void onBindViewHolder(MensajesViewHolder holder, int pos) {
        //obtenemos los datos a mostrar en esa posición
        ChatMessage item= datos.get(pos);
        // se los pasamos al método bindMensajes del viewHolder para que los asigne
        holder.bindMensajes(item);

    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    // codificamos el método del interfaz View.OnClickListener y le asignamos el listener de la actividad que contiene al RecyclerView
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    // sobreescribimos el método onClick del evento y lo delegamos  al listener de la actividad que contiene el RecyclerView para que lo procese
    @Override
    public void onClick(View view) {
        if(listener != null)// si no es nulo lanzamos el metodo onClick de la actividad que contiene el RecyclerView
            listener.onClick(view);
    }


    public static class MensajesViewHolder extends RecyclerView.ViewHolder {


        private TextView mensaje;
        private TextView usuario;
        private TextView time;
        private LinearLayout linearLayout;


        public MensajesViewHolder(View miVista) {
            super(miVista);

            mensaje = (TextView) miVista.findViewById(R.id.mensaje);
            usuario = (TextView) miVista.findViewById(R.id.usuario);
            time =(TextView) miVista.findViewById(R.id.time);
            linearLayout = (LinearLayout) miVista.findViewById(R.id.linearLayoutMessage);

        }

        public void bindMensajes(ChatMessage c) {
            if(c.getMessageUser().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                linearLayout.setBackgroundResource(R.drawable.bubble_corner_right);
            }
            mensaje.setText(c.getMessageText());
            usuario.setText(c.getMessageUser());
            time.setText(c.getMessageTime());

        }
    }
}
