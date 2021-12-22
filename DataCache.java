package edu.byu.cs240.familymap.familymapclient;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import DataModel.Event;
import DataModel.Person;

public final class DataCache {
    private static Person currentUser;
    private static List<Person> peopleList;
    private static List<Event> eventList;
    private static List<Event> eventListByPersonID;
    private static List<Person> familyList;
    private static List<Marker> markerList;
    private static Person currentPerson;
    private static Event currentEvent;

    private static boolean searchSettingsHidden = false;
    private static boolean clickedEventFromPersonActivity = false;
    private static boolean showLifeStoryLines = true;
    private static boolean showFamilyTreeLines = true;
    private static boolean showSpouseLines = true;
    private static boolean showFatherSide = true;
    private static boolean showMotherSide = true;
    private static boolean showMaleEvents = true;
    private static boolean showFemaleEvents = true;

    private static List<Event> maleEventList;
    private static List<Event> femaleEventList;
    private static final List<Event> fatherSideEvents = new ArrayList<>();
    private static final List<Event> motherSideEvents = new ArrayList<>();
    private static final List<Person> fatherSidePeople = new ArrayList<>();
    private static final List<Person> motherSidePeople = new ArrayList<>();
    private static List<Marker> currentMarkerList = new ArrayList<>();
    private static List<Event> currentEventList = new ArrayList<>();

    private DataCache() {
        peopleList = null;
        eventList = null;
        currentUser = null;
    }

    public static void setCurrentEventList(List<Event> eventList) {
        currentEventList = eventList;
    }

    public static List<Event> getCurrentEventList() {return currentEventList;}

    public static Person getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Person currentUser) {
        DataCache.currentUser = currentUser;
    }


    public static boolean isShowLifeStoryLines() {
        return showLifeStoryLines;
    }

    public static void setShowLifeStoryLines(boolean showLifeStoryLines) {
        DataCache.showLifeStoryLines = showLifeStoryLines;
    }

    public static boolean isShowFamilyTreeLines() {
        return showFamilyTreeLines;
    }

    public static void setShowFamilyTreeLines(boolean showFamilyTreeLines) {
        DataCache.showFamilyTreeLines = showFamilyTreeLines;
    }

    public static boolean isShowSpouseLines() {
        return showSpouseLines;
    }

    public static void setShowSpouseLines(boolean showSpouseLines) {
        DataCache.showSpouseLines = showSpouseLines;
    }

    public static boolean isShowFatherSide() {
        return showFatherSide;
    }

    public static void setShowFatherSide(boolean showFatherSide) {
        DataCache.showFatherSide = showFatherSide;
    }

    public static boolean isShowMotherSide() {
        return showMotherSide;
    }

    public static void setShowMotherSide(boolean showMotherSide) {
        DataCache.showMotherSide = showMotherSide;
    }

    public static void setCurrentEvent(Event event) {
        currentEvent = event;
    }

    public static Event getCurrentEvent() {return currentEvent;}

    public static void setClickedEventFromPersonActivity(boolean bool) {
        clickedEventFromPersonActivity = bool;
    }

    public static boolean isClickedEventFromPersonActivity() {return clickedEventFromPersonActivity;}

    public static void setMarkerList(List<Marker> list) {
        markerList = list;
    }

    public static List<Marker> getMarkerList() {return markerList;}

    public static void setEventAndPeopleList(List<Event> eventListParam, List<Person> peopleListParam) {
        eventList = eventListParam;
        peopleList = peopleListParam;
    }

    public static void setEventByPersonID(List<Event> eventList) {
        eventListByPersonID = eventList;
    }

    public static void setSearchSettingsHidden(boolean bool) {
        searchSettingsHidden = bool;
    }

    public static boolean isSearchSettingsHidden() {return searchSettingsHidden;}

    public static void setCurrentPerson(Person person) {
        currentPerson = person;
    }

    public static Person getCurrentPerson() {return currentPerson;}

    public static void setFamilyList(List<Person> personList) {
        familyList = personList;
    }

    public static List<Person> getFamilyList() {return familyList; }

    public static List<Event> getEventListByPersonID() {return eventListByPersonID;}

    public static List<Person> getPeopleList() {
        return peopleList;
    }

    public static List<Event> getEventList() {
        return eventList;
    }

    public static void setShowMaleEvents(boolean bool) { showMaleEvents = bool; }

    public static boolean showMaleEvents() {return showMaleEvents;}

    public static void setShowFemaleEvents(boolean bool) { showFemaleEvents = bool; }

    public static boolean showFemaleEvents() {return showFemaleEvents;}

    public static void setMaleOnlyEvents(List<Event> list) {

        Person currentPerson = new Person();

        List<Event> maleList = new ArrayList<>();

        for (Event event : list) {
            for (Person person : peopleList) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    currentPerson = person;
                    break;
                }
            }
            if (currentPerson.getGender().equals("m")) {
                maleList.add(event);
            }
        }
        maleEventList = maleList;
    }

    public static List<Event> getMaleEventList() { return maleEventList; }

    public static void setFemaleOnlyEvents(List<Event> list) {
        Person currentPerson = new Person();

        List<Event> femaleList = new ArrayList<>();

        for (Event event : list) {
            for (Person person : peopleList) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    currentPerson = person;
                    break;
                }
            }
            if (currentPerson.getGender().equals("f")) {
                femaleList.add(event);
            }
        }
        femaleEventList = femaleList;
    }

    public static List<Event> getFemaleEventList() { return femaleEventList; }

    public static void setFatherSideEvents(List<Person> list) {
        List<Person> fatherSidePersonList = new ArrayList<>();
        int position = 2;
        int exponent = 2;
        boolean getLeftSide = false;
        int startIndex = 0;

        if (list.get(1).getSpouseID().equals(list.get(0).getPersonID())) {
            if (list.get(2).getGender().equals("m")) {
                getLeftSide = true;
            }
            startIndex = 2;
            position = 3;
        }

        else {
            if (list.get(1).getGender().equals("m")) {
                getLeftSide = true;
            }
            startIndex = 1;
        }

        for (int i = startIndex; i < list.size(); i++) {
            fatherSidePersonList.add(list.get(i));
            if (i == position) {
                filterList(fatherSidePersonList, getLeftSide, true);
                fatherSidePersonList.clear();
                position = position + (int) Math.pow(2, exponent);
                exponent = exponent + 1;
            }
        }

        for (Person person: fatherSidePeople) {
            for (Event event : eventList) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    fatherSideEvents.add(event);
                }
            }
        }
    }

    public static void filterList(@NonNull List<Person> list, boolean getLeftSide, boolean isFather) {
        if (getLeftSide) {
            for (int i = 0; i < list.size() / 2; i++) {
                if (isFather) {
                    fatherSidePeople.add(list.get(i));
                }
                else {
                    motherSidePeople.add(list.get(i));
                }
            }
            return;
        }
        for (int i = list.size() / 2; i < list.size(); i++) {
            if (isFather) {
                fatherSidePeople.add(list.get(i));
            }

            else {
                motherSidePeople.add(list.get(i));
            }
        }
    }

    public static List<Event> getFatherSideEvents() { return fatherSideEvents; }

    public static void setMotherSideEvents(List<Person> list) {
        List<Person> motherSidePersonList = new ArrayList<>();
        int position = 2;
        int exponent = 2;
        boolean getLeftSide = true;
        int startIndex = 0;

        if (list.get(1).getSpouseID().equals(list.get(0).getPersonID())) {
            if (list.get(2).getGender().equals("m")) {
                getLeftSide = false;
            }
            startIndex = 2;
            position = 3;
        }

        else {
            if (list.get(1).getGender().equals("m")) {
                getLeftSide = false;
            }
            startIndex = 1;
        }

        for (int i = startIndex; i < list.size(); i++) {
            motherSidePersonList.add(list.get(i));
            if (i == position) {
                filterList(motherSidePersonList, getLeftSide, false);
                motherSidePersonList.clear();
                position = position + (int) Math.pow(2, exponent);
                exponent = exponent + 1;
            }
        }

        for (Person person: motherSidePeople) {
            for (Event event : eventList) {
                if (event.getPersonID().equals(person.getPersonID())) {
                    motherSideEvents.add(event);
                }
            }
        }
    }

    public static List<Event> getMotherSideEvents() { return motherSideEvents; }

    public static List<Person> getMotherSidePeople() { return motherSidePeople; }

    public static List<Person> getFatherSidePeople() { return fatherSidePeople; }

    public static List<Marker> getCurrentMarkerList() {
        return currentMarkerList;
    }

    public static void setCurrentMarkerList(List<Marker> currentMarkerList) {
        DataCache.currentMarkerList = currentMarkerList;
    }
}
