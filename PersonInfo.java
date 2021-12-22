package edu.byu.cs240.familymap.familymapclient;

import java.util.ArrayList;
import java.util.List;

import DataModel.Event;
import DataModel.Person;

public class PersonInfo {
    public PersonInfo() {

    }

    public void setCurrentEventsPersonsLists(Person currentPerson) throws Exception {

        DataCache.setCurrentPerson(currentPerson);

        List<Event> eventList = DataCache.getEventList();
        List<Event> eventListForPersonID = new ArrayList<>();
        //todo: setting events and family list for person...
        for (Event eventItem : eventList) {
            if (eventItem.getPersonID().equals(currentPerson.getPersonID())) {
                eventListForPersonID.add(eventItem);
            }
        }

        if (eventListForPersonID.isEmpty()) {
            throw new Exception("person not found");
        }

        DataCache.setEventByPersonID(eventListForPersonID);

        List<Person> familyList = new ArrayList<>(4);
        List<Person> personList = DataCache.getPeopleList();

        for (int i = 0; i < 4; i++) {
            familyList.add(null);
        }

        for (Person person : personList) {
            if (currentPerson.getSpouseID() == null) { //todo: found root person, there will be no child or spouse
                familyList.set(3, null);
            }
            if (currentPerson.getMotherID() == null || currentPerson.getFatherID() == null) {
                //todo: found last generation person, there will be no more parents
                familyList.set(0, null);
                familyList.set(1, null);
            }
            if (currentPerson.getFatherID() != null && person.getPersonID().equals(currentPerson.getFatherID())) {
                familyList.set(0, person);
            }
            if (currentPerson.getMotherID() != null && person.getPersonID().equals(currentPerson.getMotherID())) {
                familyList.set(1, person);
            }
            if (currentPerson.getSpouseID() != null && person.getPersonID().equals(currentPerson.getSpouseID())) {
                familyList.set(2, person);
            }
            if (person.getMotherID() != null && person.getMotherID().equals(currentPerson.getPersonID())) {
                familyList.set(3, person);
            }
            if (person.getFatherID() != null && person.getFatherID().equals(currentPerson.getPersonID())) {
                familyList.set(3, person);
            }

        }

        DataCache.setFamilyList(familyList);
    }

}
