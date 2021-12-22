package edu.byu.cs240.familymap.familymapclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import DataModel.Event;
import DataModel.Person;
import Result.EventResult;
import Result.PersonResult;

public class GetDataTask implements Runnable {
    Handler uiMessage;
    String serverHost;
    String serverPort;
    String authToken;
    String personID;
    String messageToSend;
    PersonResult personResult;
    EventResult eventResult;

    private static final String TOAST_MESSAGE_KEY = "Toast Message";

    public GetDataTask(Handler uiMessage, String serverHost, String serverPort, String personID, String authToken) {
        this.uiMessage = uiMessage;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.personID = personID;
        this.authToken = authToken;
    }

    @Override
    public void run() {
        //todo: call server Proxy
        try {
            ServerProxy serverProxy = new ServerProxy();

            String getPeopleResult = serverProxy.getPeople(serverHost, serverPort, authToken);

            Gson gson = new Gson();

            personResult = gson.fromJson(getPeopleResult, PersonResult.class);

            String getEventsResult = serverProxy.getEvents(serverHost, serverPort, authToken);

            eventResult = gson.fromJson(getEventsResult, EventResult.class);

            DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

            List<Person> personList = DataCache.getPeopleList();

            DataCache.setCurrentUser(DataCache.getPeopleList().get(0));

            String firstName = null;
            String lastName = null;

            for (Person person : personList) {
                if (person.getPersonID().equals(personID)) {
                    firstName = person.getFirstName();
                    lastName = person.getLastName();
                }
            }

            messageToSend = "Logged in as: " + firstName + " " + lastName;
            sendMessage(messageToSend);

        } catch (Exception e) {
            Log.d("Fragment message", "there was an error in run");
            messageToSend = "Get Data Task Failed";
            sendMessage(messageToSend);
        }
    }
    private void sendMessage(String messageToSend) {
        Message message = Message.obtain();

        Bundle messageBundle = new Bundle();
        messageBundle.putString(TOAST_MESSAGE_KEY, messageToSend);
        message.setData(messageBundle);

        uiMessage.sendMessage(message);
    }
}
