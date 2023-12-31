package com.example.final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.final_project.Settings.Data;
import com.example.final_project.Settings.Message;
import com.example.final_project.Settings.Rest_api;
import com.example.final_project.Settings.Urderlined;
import com.example.final_project.Settings.Validation_field;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Activity_sign_up extends AppCompatActivity {
    private Button btn_sign_up, btn_sign_in;
    private ImageButton btn_calendar;
    private TextView txt_name, txt_email, txt_password, txt_phone, txt_dni, birthdate;
    Integer id_career;
    private Spinner career;
    Message message=new Message();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        TextView link_have_account=findViewById(R.id.link_have_account);
        Urderlined urderlined=new Urderlined();//urderline=subrayado
        btn_sign_up=(Button) findViewById(R.id.btn_sign_up3);
        btn_sign_in=(Button) findViewById(R.id.btn_sign_in3);
        txt_dni=findViewById(R.id.txt_dni);
        txt_email=findViewById(R.id.txt_email);
        txt_name=findViewById(R.id.txt_name);
        txt_password=findViewById(R.id.txt_password1);
        txt_phone=findViewById(R.id.txt_phone);
        birthdate=findViewById(R.id.birthdate1);
        career=findViewById(R.id.career);
        btn_calendar=findViewById(R.id.btn_calendar1);

        btn_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate_calendar();
            }
        });

        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent new_window=new Intent(getApplicationContext(), Activity_sign_in.class);//new_window=nueva ventana
                startActivity(new_window);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(txt_password.getText().toString().isEmpty() || txt_phone.getText().toString().isEmpty() || txt_name.getText().toString().isEmpty() || txt_dni.getText().toString().isEmpty() || txt_email.getText().toString().isEmpty() || birthdate.getText().toString().isEmpty()){
                    message.message("Alerta", "No dejar campos vacios",Activity_sign_up.this);
                }else{
                    if(Validation_field.isValidName(txt_name.getText().toString())){
                        if(Validation_field.isValidEmail(txt_email.getText().toString())){
                            if(Validation_field.isValidPassword(txt_password.getText().toString())){
                                if(Validation_field.isValidPhoneNumber(txt_phone.getText().toString())){
                                    if(Validation_field.isValidDni(txt_dni.getText().toString())){
                                        if(Validation_field.isValidBirthdate(birthdate.getText().toString())){
                                            validate_repeat_email();
                                        }else{
                                            message.message("Alerta", "Fecha no es valida, revisa nuestro manual de usuario",Activity_sign_up.this);
                                        }
                                    }else{
                                        message.message("Alerta", "Caracteres incorrectos en DNI, revisa nuestro manual de usuario",Activity_sign_up.this);
                                    }
                                }else{
                                    message.message("Alerta", "Caracteres incorrectos en Telefono, revisa nuestro manual de usuario",Activity_sign_up.this);
                                }
                            }else{
                                message.message("Alerta", "Caracteres incorrectos en contraseña, revisa nuestro manual de usuario",Activity_sign_up.this);
                            }
                        }else{
                            message.message("Alerta", "Caracteres incorrectos en correo electronico, revisa nuestro manual de usuario",Activity_sign_up.this);
                        }
                    }else{
                        message.message("Alerta", "Caracteres incorrectos en nombre, revisa nuestro manual de usuario",Activity_sign_up.this);
                    }
                }
            }
        });

        urderlined.aesthetics_textView(link_have_account, "¿Ya tienes una cuenta?");
        fill_career();
    }

    private void validate_repeat_email(){
        RequestQueue queue= Volley.newRequestQueue(this);//queue=cola

        String url= Rest_api.url_mysql+Rest_api.select_email;
        StringRequest request=new StringRequest(Request.Method.POST, url,//request=peticion
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e("array",response+"");
                        try{
                            JSONArray jsonArray=new JSONArray(response);
                            Log.e("array",jsonArray+"");

                            if(jsonArray.length()>0){
                                message.message("Alerta", "Correo ya esta en uso",Activity_sign_up.this);
                            }else{
                                Data data=new Data(txt_email.getText().toString(), txt_password.getText().toString(), career.getSelectedItem().toString(),txt_name.getText().toString(), txt_phone.getText().toString(), txt_dni.getText().toString(), birthdate.getText().toString());
                                String account=generate_account();
                                Data.setAccount(account);
                                int index=(career.getSelectedItem().toString()).indexOf("-");
                                id_career=Integer.parseInt((career.getSelectedItem().toString()).substring(0, index));
                                Data.setId_career(id_career);
                                Data.setPhoto("https://firebasestorage.googleapis.com/v0/b/final-project-d3437.appspot.com/o/Profile_pictures%2Fsin_foto.jpg?alt=media&token=87e00569-a4d5-41be-adcb-7e1a443ce40f");
                                Intent new_window=new Intent(getApplicationContext(), Activity_verification.class);//new_window=nueva ventana
                                new_window.putExtra("activity",0);
                                startActivity(new_window);
                            }

                        }catch(Exception e){
                            Log.e("array","Error");
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Error: " + error.getMessage();
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        Log.e("Volley Error", errorMessage);
                    }
                }){
            @NonNull
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters=new HashMap<String,String>();//parameters=parametros
                parameters.put("email", txt_email.getText().toString());

                return parameters;
            }
        };

        queue.add(request);
    }

    private void generate_calendar(){//generate_calendar=generar calendario
        Calendar calendar=Calendar.getInstance();
        int current_year=calendar.get(Calendar.YEAR);
        int current_month=calendar.get(Calendar.MONTH)+1;
        int current_day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {//dialog=dialogo
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String day_format, month_format;//day_format=formato de dia, month_format=formato de mes

                if(dayOfMonth<10){
                    day_format="0"+String.valueOf(dayOfMonth);
                }else{
                    day_format=String.valueOf(dayOfMonth);
                }

                if(month<10){
                    month_format="0"+String.valueOf((month+1));
                }else{
                    month_format=String.valueOf((month+1));
                }

                Data.setBirth_date(year+"-"+month_format+"-"+day_format);
                birthdate.setText(Data.getBirth_date());
            }
        }, current_year,current_month,current_day);
        dialog.show();
    }

    private String generate_account(){//generate_account=generar cuenta
        Calendar calendar=Calendar.getInstance();
        Random random=new Random();
        String account=null;//account=cuenta

        String current_year=String.valueOf(calendar.get(Calendar.YEAR));
        String current_month=String.valueOf(calendar.get(Calendar.MONTH)+1);
        String current_day=String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String day_birth=(String.valueOf(Data.getBirth_date())).substring(8,10);//day_birth=dia de nacimiento
        String current_seconds=String.valueOf(calendar.get(Calendar.SECOND));
        String random1=String.valueOf(random.nextInt(99));
        String random2=String.valueOf(random.nextInt(99));
        account=current_year+current_month+current_day+day_birth+current_seconds+random1+random2;

        return account;
    }

    private void fill_career(){//fill_career=llenar carreras
        String url=Rest_api.url_mysql+Rest_api.select_careers;
        RequestQueue queue=Volley.newRequestQueue(this);//queue=cola

        StringRequest request=new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        try{
                            JSONArray jsonArray=new JSONArray(response);

                            String[] careers=new String[jsonArray.length()];
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject career_object=jsonArray.getJSONObject(i);//career_object=objeto carrera
                                String id=career_object.getString("id_career");
                                String name=career_object.getString("career_name");
                                String career=id+"-"+name;
                                careers[i]=career;
                            }

                            ArrayAdapter<String> adapter=new ArrayAdapter<>(Activity_sign_up.this, android.R.layout.simple_spinner_item, careers);//adapter=adaptador
                            career.setAdapter(adapter);

                        }catch(JSONException e){
                            e.printStackTrace();
                            message.message("Error", "Revisa bien: "+e, Activity_sign_up.this);
                        }
                    }
                },new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.message("Error", "Revisa bien: "+error, Activity_sign_up.this);
                    }
                });

        queue.add(request);
    }
}
