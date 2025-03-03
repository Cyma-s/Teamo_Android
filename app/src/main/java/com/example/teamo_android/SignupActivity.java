package com.example.teamo_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.Duration;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private ImageView backBtn;
    private EditText idEdit, passwordEdit, nameEdit;
    private Button idCheckBtn, nextBtn;
    private TextView idCondition, passwordCondition, idCheck;
    private Spinner deptNameSpin, admissionYearSpin;

    private String idText, passwordText, nameText, deptNameText, admissionYearText;
    private Boolean checkId = false, checkValidId = false, checkPassword = false, checkName = false, checkDeptName = false, checkAdmissionYear = false;
    public RequestQueue queue;
    private ArrayAdapter deptAdapter, admissionAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initElements();
    }

    private void initElements() {
        backBtn = (ImageView) findViewById(R.id.btn_back_signup);
        idEdit = (EditText) findViewById(R.id.edit_id_signup);
        passwordEdit = (EditText) findViewById(R.id.edit_password_signup);
        nameEdit = (EditText) findViewById(R.id.edit_name_signup);
        idCheckBtn = (Button) findViewById(R.id.btn_id_validation_signup);
        nextBtn = (Button) findViewById(R.id.btn_next_signup);
        idCondition = (TextView) findViewById(R.id.text_id_condition_signup);
        passwordCondition = (TextView) findViewById(R.id.text_password_condition_signup);
        idCheck = (TextView) findViewById(R.id.text_id_check_signup);
        deptNameSpin = (Spinner) findViewById(R.id.spin_dept_name_signup);
        admissionYearSpin = (Spinner) findViewById(R.id.spin_admission_year_signup);

        deptAdapter = ArrayAdapter.createFromResource(this, R.array.dept_name, R.layout.spinner_item);
        deptAdapter.setDropDownViewResource(R.layout.spinner_item);
        admissionAdapter = ArrayAdapter.createFromResource(this, R.array.admission_year, R.layout.spinner_item);
        admissionAdapter.setDropDownViewResource(R.layout.spinner_item);
        deptNameSpin.setAdapter(deptAdapter);
        admissionYearSpin.setAdapter(admissionAdapter);
        initOnClickListeners();
    }

    private void initOnClickListeners() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkId && checkValidId && checkPassword && checkName && checkDeptName && checkAdmissionYear)
                    continueRegister();
                else {
                    Toast.makeText(SignupActivity.this, "입력하신 정보를 다시 한 번 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { checkIdValidity(); }
        });

        idEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String id = idEdit.getText().toString();

                if((id.length() < 5) || !(Pattern.matches("^[a-zA-Z0-9]*$", id))) {
                    idCondition.setVisibility(View.VISIBLE);
                    checkId = false;
                }
                else {
                    idCondition.setVisibility(View.GONE);
                    idText = id;
                    checkId = true;
                }
            }
        });

        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String password = passwordEdit.getText().toString();

                if((password.length() < 7) || !(Pattern.matches("^[a-zA-Z0-9]*$", password))) {
                    passwordCondition.setVisibility(View.VISIBLE);
                    checkPassword = false;
                }
                else {
                    passwordCondition.setVisibility(View.GONE);
                    passwordText = password;
                    checkPassword = true;
                }
            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                nameText = nameEdit.getText().toString();
                checkName = true;
            }
        });

        deptNameSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                deptNameText = parent.getItemAtPosition(position).toString();
                checkDeptName = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                deptNameText = null;
                checkDeptName = false;
            }
        });

        admissionYearSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                admissionYearText = parent.getItemAtPosition(position).toString();
                checkAdmissionYear = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                admissionYearText = null;
                checkAdmissionYear = false;
            }
        });
    }

    // ID 중복확인을 위한 서버 통신 파트
    private void checkIdValidity() {
        queue = Volley.newRequestQueue(getApplicationContext());
        String idValidationUrl = getString(R.string.url) + "/user/id/validation/" + idText;
        Log.d("idis", idValidationUrl);
        final StringRequest request = new StringRequest(Request.Method.GET, idValidationUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("true")) {  // 중복된 경우
                            checkValidId = false;
                            Toast.makeText(getApplicationContext(),  "중복된 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else {   // 없는 경우
                            idCheck.setVisibility(View.VISIBLE);
                            checkValidId = true;
                            Toast.makeText(getApplicationContext(),  "아이디 사용가능.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("thisis", error.toString());
                Toast.makeText(getApplicationContext(),  "서버 통신 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    // 사용자 정보 저장을 위한 서버 통신 파트
    private void continueRegister() {
        Intent next_intent = new Intent(SignupActivity.this, EmailConfirmationActivity.class);
        next_intent.putExtra("id", idText);
        next_intent.putExtra("password", passwordText);
        next_intent.putExtra("name", nameText);
        next_intent.putExtra("deptName", deptNameText);
        next_intent.putExtra("admissionYear", admissionYearText);

        String data = idText + " " + passwordText + " " + nameText + " " + deptNameText + " " + admissionYearText;
        Log.d("Data", data);

        startActivity(next_intent);
    }
}