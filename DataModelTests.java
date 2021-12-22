package edu.byu.cs240.familymap.familymapclient;

import static org.junit.Assert.*;

import com.google.gson.Gson;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import DataAccess.DataAccessException;
import DataModel.Event;
import DataModel.Person;
import Request.LoginRequest;
import Result.EventResult;
import Result.LoginResult;
import Result.PersonResult;

public class DataModelTests {
    PersonResult personResult;
    EventResult eventResult;

    public void Login() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sheila", "parker");

        String result = ServerProxy.login("localhost", "8080", loginRequest);

        assertTrue(result.contains("true"));
        assertTrue(result.contains("personID"));
        assertTrue(result.contains("sheila"));
        assertTrue(result.contains("authtoken"));

        Gson gson = new Gson();

        LoginResult loginResult = gson.fromJson(result, LoginResult.class);

        String people = ServerProxy.getPeople("localhost", "8080", loginResult.getAuthtoken());

        personResult = gson.fromJson(people, PersonResult.class);

        int personListSize = personResult.getData().size();

        assertEquals(8, personListSize);

        String events = ServerProxy.getEvents("localhost", "8080", loginResult.getAuthtoken());

        eventResult = gson.fromJson(events, EventResult.class);

        int eventListSize = eventResult.getData().size();

        assertEquals(16, eventListSize);
    }
    @Test
    public void FamilyRelationshipsTestPass() throws Exception {
        Person person  = new Person("Sheila_Parker", "sheila", "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        PersonInfo personInfo = new PersonInfo();

        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        personInfo.setCurrentEventsPersonsLists(person);

        List<String> compareList = new ArrayList<>();
        compareList.add("Blaine");
        compareList.add("Betty");
        compareList.add("Davis");
        compareList.add(null);

        List<Person> familyList = DataCache.getFamilyList();

        for (int i = 0; i < familyList.size(); i++) {
            if (i == familyList.size() - 1) {
                assertEquals(compareList.get(i), familyList.get(i));
                break;
            }
            assertEquals(compareList.get(i), familyList.get(i).getFirstName());
        }
    }

    @Test
    public void FamilyRelationshipsTestFail() throws Exception {
        //inserting incorrect ID and checking if it throws exception
        Person person  = new Person("wrongID", "sheila", "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");
        PersonInfo personInfo = new PersonInfo();

        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        assertThrows(Exception.class, () -> personInfo.setCurrentEventsPersonsLists(person));
    }

    @Test
    public void FilterEventsPass() throws Exception {
        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        //male Filter
        DataCache.setMaleOnlyEvents(eventResult.getData());
        assertEquals(6, DataCache.getMaleEventList().size());

        //female Filter
        DataCache.setFemaleOnlyEvents(eventResult.getData());
        assertEquals(10, DataCache.getFemaleEventList().size());

        //mom side
        DataCache.setMotherSideEvents(personResult.getData());
        assertEquals(5, DataCache.getMotherSideEvents().size());

        //dad side
        DataCache.setFatherSideEvents(personResult.getData());
        assertEquals(5, DataCache.getFatherSideEvents().size());
    }

    @Test
    public void FilterEventsFail() {
        DataCache.setMaleOnlyEvents(new ArrayList<>());
        assertEquals(0, DataCache.getMaleEventList().size());

        DataCache.setFemaleOnlyEvents(new ArrayList<>());
        assertEquals(0, DataCache.getFemaleEventList().size());

        assertThrows(IndexOutOfBoundsException.class, () -> DataCache.setMotherSideEvents(new ArrayList<>()));
        assertThrows(IndexOutOfBoundsException.class, () -> DataCache.setFatherSideEvents(new ArrayList<>()));
    }

    @Test
    public void SortEventsPass() throws Exception {
        Person person  = new Person("Sheila_Parker", "sheila", "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");

        PersonInfo personInfo = new PersonInfo();
        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        personInfo.setCurrentEventsPersonsLists(person);

        List<Event> eventList = DataCache.getEventListByPersonID();

        List<String> compareList = new ArrayList<>();
        compareList.add("Sheila_Birth");
        compareList.add("Sheila_Marriage");
        compareList.add("Sheila_Asteroids");
        compareList.add("Other_Asteroids");
        compareList.add("Sheila_Death");

        for (int i = 0; i < eventList.size(); i++) {
            assertEquals(compareList.get(i), eventList.get(i).getEventID());
        }
    }

    @Test
    public void SortEventsFail() throws Exception {
        Person person  = new Person("Sheila_Parker", "sheila", "Sheila", "Parker", "f", "Blaine_McGary", "Betty_White", "Davis_Hyer");

        PersonInfo personInfo = new PersonInfo();
        Login();
        DataCache.setEventAndPeopleList(null, personResult.getData());
        assertThrows(NullPointerException.class, () -> personInfo.setCurrentEventsPersonsLists(person));
    }

    @Test
    public void SearchPass() throws Exception {
        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        String searchString = "Rod";

        Calculations calculations = new Calculations();
        List<Person> searchPersonList = calculations.searchPersonFilter(searchString);

        List<String> expectedPersonList = new ArrayList<>();
        expectedPersonList.add("Ken");
        expectedPersonList.add("Mrs");

        for (int i = 0; i < searchPersonList.size(); i++) {
            assertEquals(expectedPersonList.get(i), searchPersonList.get(i).getFirstName());
        }

        String searchString2 = "4";

        List<Event> searchEventList = calculations.searchEventFilter(searchString2);
        List<String> expectedEventList = new ArrayList<>();
        expectedEventList.add("completed asteroids");
        expectedEventList.add("completed asteroids");
        expectedEventList.add("birth");

        for (int i = 0; i < searchEventList.size(); i++) {
            assertEquals(expectedEventList.get(i), searchEventList.get(i).getEventType().toLowerCase());
        }
    }

    @Test
    public void SearchFail() throws Exception {
        Login();
        DataCache.setEventAndPeopleList(eventResult.getData(), personResult.getData());

        String searchString = "cs";

        Calculations calculations = new Calculations();
        List<Person> searchPersonList = calculations.searchPersonFilter(searchString);

        assertEquals(0, searchPersonList.size());

        String searchString2 = "1987";

        List<Event> searchEventList = calculations.searchEventFilter(searchString2);

        assertEquals(0, searchEventList.size());
    }
}
