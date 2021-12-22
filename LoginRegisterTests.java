package edu.byu.cs240.familymap.familymapclient;

import static android.widget.Toast.LENGTH_LONG;

import org.junit.Test;

import static org.junit.Assert.*;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;

import DataAccess.DataAccessException;
import Request.LoadRequest;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.EventResult;
import Result.LoginResult;
import Result.PersonResult;
import Result.RegisterResult;
import Service.ClearService;
import Service.LoadService;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginRegisterTests {
    @Test
    public void LoginPass() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");

        String result = ServerProxy.login("localhost", "8080", loginRequest);

        assertTrue(result.contains("true"));
        assertTrue(result.contains("personID"));
        assertTrue(result.contains("sheila"));
        assertTrue(result.contains("authtoken"));

        Gson gson = new Gson();

        LoginResult loginResult = gson.fromJson(result, LoginResult.class);

        String people = ServerProxy.getPeople("localhost", "8080", loginResult.getAuthtoken());

        PersonResult personResult = gson.fromJson(people, PersonResult.class);

        int personListSize = personResult.getData().size();

        assertEquals(8, personListSize);

        String events = ServerProxy.getEvents("localhost", "8080", loginResult.getAuthtoken());

        EventResult eventResult = gson.fromJson(events, EventResult.class);

        int eventListSize = eventResult.getData().size();

        assertEquals(16, eventListSize);
    }

    @Test
    public void LoginFail() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sheila", "spencer");

        String result = ServerProxy.login("localhost", "8080", loginRequest);

        assertTrue(result.contains("false"));
        assertTrue(result.contains("Error"));

        LoginRequest loginRequest2 = new LoginRequest("sheila", null);

        String result2 = ServerProxy.login("localhost", "8080", loginRequest2);

        assertTrue(result2.contains("false"));
        assertTrue(result2.contains("Error"));
    }

    @Test
    public void RegisterPass() throws Exception {
        ClearService clearService = new ClearService();
        clearService.clear();
        RegisterRequest registerRequest = new RegisterRequest("choir1997", "123456", "choir1997@gmail.com", "Rikki", "Choi", "f");

        String result = ServerProxy.register("localhost", "8080", registerRequest);

        assertTrue(result.contains("true"));
        assertTrue(result.contains("personID"));
        assertTrue(result.contains("choir1997"));
        assertTrue(result.contains("authtoken"));

        Gson gson = new Gson();

        RegisterResult registerResult = gson.fromJson(result, RegisterResult.class);

        String people = ServerProxy.getPeople("localhost", "8080", registerResult.getAuthtoken());

        PersonResult personResult = gson.fromJson(people, PersonResult.class);

        int personListSize = personResult.getData().size();

        assertEquals(31, personListSize);

        String events = ServerProxy.getEvents("localhost", "8080", registerResult.getAuthtoken());

        EventResult eventResult = gson.fromJson(events, EventResult.class);

        int eventListSize = eventResult.getData().size();

        assertEquals(93, eventListSize);


        JsonReader jsonReader = new JsonReader(new FileReader("C:\\Users\\choir\\FamilyMapServerStudent-master\\passoffFiles\\LoadData.json"));
        LoadRequest loadRequest = gson.fromJson(jsonReader, LoadRequest.class);

        LoadService loadService = new LoadService();
        loadService.load(loadRequest);

    }

    @Test
    public void RegisterFail() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("sheila", "parker", "sheila@parker.com", "Sheila", "Parker", "f");
        String result = ServerProxy.register("localhost", "8080", registerRequest);

        assertTrue(result.contains("false"));
        assertTrue(result.contains("Error"));

        RegisterRequest registerRequest2 = new RegisterRequest("sheila", null,"sheila@parker.com", "Sheila", "Parker", "f");
        String result2 = ServerProxy.register("localhost", "8080", registerRequest);

        assertTrue(result2.contains("false"));
        assertTrue(result2.contains("Error"));
    }

}