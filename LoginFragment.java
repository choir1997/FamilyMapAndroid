package edu.byu.cs240.familymap.familymapclient;

import static android.widget.Toast.LENGTH_LONG;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Result.LoginResult;

public class LoginFragment extends Fragment {
    Button loginFragmentBtn, registerFragmentBtn;
    EditText serverHost, serverPort, username, password;
    LoginRequest loginRequest;
    String loginFailedMessage;

    private static final String TOAST_MESSAGE_KEY = "Toast Message";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_login, container, false);

        loginFragmentBtn = v.findViewById(R.id.loginBtn);
        registerFragmentBtn = v.findViewById(R.id.registerBtn);

        loginFragmentBtn.setEnabled(false);
        loginFragmentBtn.setBackgroundColor(Color.parseColor("#808080"));


        serverHost = v.findViewById(R.id.serverHost);
        serverPort = v.findViewById(R.id.serverPort);
        username = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!validateFields()) {
                    loginFragmentBtn.setEnabled(false);
                    loginFragmentBtn.setBackgroundColor(Color.parseColor("#808080"));
                }
                else {
                    loginFragmentBtn.setEnabled(true);
                    loginFragmentBtn.setBackgroundColor(Color.parseColor("#FF018786"));
                }
            }
        };

        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);

        loginFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //todo: need to check with server to make sure account is there
                try {
                    loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());

                    Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            loginFailedMessage = bundle.getString(TOAST_MESSAGE_KEY);
                            Toast.makeText(getActivity(), loginFailedMessage, LENGTH_LONG).show();
                        }
                    };

                    LoginTask loginTask = new LoginTask(uiThreadMessageHandler, serverHost.getText().toString(),
                            serverPort.getText().toString(), loginRequest);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(loginTask);
                } catch (Exception e) {
                    Log.d("Fragment Message", "Login Failed Here");
                }
            }
        });

        registerFragmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, new RegisterFragment());
                fragmentTransaction.commit();
            }
        });

        return v;
    }

    private boolean validateFields() {

        if (serverHost.length() == 0) {
            serverHost.setError("Must enter server host");
            return false;
        }
        if (serverPort.length() == 0) {
            serverPort.setError("Must enter server port");
            return false;
        }
        if (username.length() == 0) {
            username.setError("Must enter username");
            return false;
        }
        if (password.length() == 0) {
            password.setError("Must enter password");
            return false;
        }

        return true;
    }

    private class LoginTask implements Runnable {
        Handler uiMessage;
        String serverHost;
        String serverPort;
        LoginRequest request;
        String getDataMessage;
        LoginResult loginResult;

        public LoginTask(Handler uiMessage, String serverHost, String serverPort, LoginRequest request) {
            this.uiMessage = uiMessage;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
            this.request = request;
        }

        @Override
        public void run() {
            //todo: call server Proxy
            try {
                ServerProxy serverProxy = new ServerProxy();

                String loginData = serverProxy.login(serverHost, serverPort, request);

                Log.d("Login Data", loginData);

                Gson gson = new Gson();

                loginResult = gson.fromJson(loginData, LoginResult.class);

                if (!loginResult.isSuccess()) {
                    throw new Exception("login failed");
                }

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @SuppressLint("HandlerLeak")
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        getDataMessage = bundle.getString(TOAST_MESSAGE_KEY);
                        Toast.makeText(getActivity(), getDataMessage, LENGTH_LONG).show();
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, new MapFragment());
                        fragmentTransaction.commit();
                    }
                };

                GetDataTask getDataTask = new GetDataTask(uiThreadMessageHandler, serverHost, serverPort, loginResult.getPersonID(), loginResult.getAuthtoken());
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(getDataTask);

            } catch (Exception e) {
                Log.d("Fragment message", "there was an error in run");
                sendMessage();
            }
        }
        private void sendMessage() {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            messageBundle.putString(TOAST_MESSAGE_KEY, "Login Failed");
            message.setData(messageBundle);

            uiMessage.sendMessage(message);
        }
    }
}