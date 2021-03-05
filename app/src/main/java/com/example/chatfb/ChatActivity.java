package com.example.chatfb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatfb.models.ChatMessage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView miRecView;
    private AdaptadorMensajes adaptador;
    private int posicion = 0;
    private ImageButton buttonSend;
    private EditText inputText;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private ArrayList<ChatMessage> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Login con google, insertamos el token necesario
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        buttonSend = (ImageButton) findViewById(R.id.sendButton);
        inputText = (EditText) findViewById(R.id.inputText);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creamos instancia de la base de datos e introducimos mensaje en la base de datos.
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("messages");

                ChatMessage messageNew = new ChatMessage(inputText.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getEmail());
                myRef.push().setValue(messageNew); //Con push insertamos en la BD el mensaje con un ID único

                inputText.getText().clear();
            }
        });

        //fijaAdaptador();
        cargaAdaptador();
    }


    private void cargaAdaptador() {

        messages = new ArrayList<>();

        miRecView = (RecyclerView) findViewById(R.id.reciclador);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("messages");


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot2 : snapshot.getChildren()) {
                        ChatMessage chatMessage = dataSnapshot2.getValue(ChatMessage.class);
                        messages.add(chatMessage);
                    }

                    adaptador = new AdaptadorMensajes(messages);
                    fijaAdaptador();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void fijaAdaptador() {

        adaptador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // aqui procesamos el evento delegado por la vista en el AdaptadorAlumnos
                posicion = miRecView.getChildAdapterPosition(v);//devuelve la posision pulsada
                openContextMenu(miRecView); // abrimos el menu contextual

            }
        });
        miRecView.setAdapter(adaptador);
        miRecView.setLayoutManager(new GridLayoutManager(this, 1));
        miRecView.setItemAnimator(new DefaultItemAnimator());
        miRecView.scrollToPosition(adaptador.getItemCount() - 1);
    }

    //Funcion para logout de la aplicacion
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Usted ha salido de la aplicación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Funciones para crear menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
