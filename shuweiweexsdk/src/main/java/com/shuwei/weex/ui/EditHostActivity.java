package com.shuwei.weex.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.shuwei.weex.R;
import com.shuwei.weex.WeexModule;
import com.shuwei.weex.weexutils.DebugUtils;

public class EditHostActivity extends AppCompatActivity {

    private EditText etServer;
    private EditText etHost;
    private Button btnServer;
    private Button btnLocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_host);

        etHost = findViewById(R.id.etHost);
        etServer = findViewById(R.id.etServer);
        btnServer = findViewById(R.id.btnServer);
        btnLocal = findViewById(R.id.btnLocal);

        if(DebugUtils.getDebugHost(this) != null) {
            etHost.setText(DebugUtils.getDebugHost(this));
        }
        if(DebugUtils.getDebugServer(this) != null) {
            etServer.setText(DebugUtils.getDebugServer(this));
        }

        btnServer.setOnClickListener(v -> {
            DebugUtils.setDebugHost(v.getContext(), etHost.getText().toString());
            DebugUtils.setDebugServer(v.getContext(), etServer.getText().toString());
            DebugUtils.setIsServer(v.getContext(), true);
            WeexModule.enableRemoteDebug(v.getContext());
        });

        btnLocal.setOnClickListener(v -> {
            DebugUtils.setDebugHost(v.getContext(), etHost.getText().toString());
            DebugUtils.setDebugServer(v.getContext(), etServer.getText().toString());
            DebugUtils.setIsServer(v.getContext(), false);
        });
    }
}
