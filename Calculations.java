package edu.byu.cs240.familymap.familymapclient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import DataModel.Event;
import DataModel.Person;

public class Calculations {
    public Calculations() {

    }

    public static class SortByYear implements Comparator<Event> {

        @Override
        public int compare(Event o1, Event o2) {
            return o1.getYear() - o2.getYear();
        }
    }

    public static class SortByType implements Comparator<Event> {

        @Override
        public int compare(Event o1, Event o2) {
            return o1.getEventType().compareTo(o2.getEventType());
        }
    }

    public List<Person> searchPersonFilter(String searchString) {
        List<Person> filteredPersonList = new ArrayList<>();
        List<Person> currentPersonList = DataCache.getPeopleList();

        for (Person person: currentPersonList) {
            if (person.getFirstName().toLowerCase().contains(searchString) || person.getLastName().toLowerCase().contains(searchString)) {
                filteredPersonList.add(person);
            }
        }

        return filteredPersonList;
    }

    public List<Event> searchEventFilter(String searchString) {
        List<Event> filteredEventList = new ArrayList<>();
        List<Event> currentEventList = DataCache.getEventList();
        List<Person> peopleList = DataCache.getPeopleList();

        for (Event event: currentEventList) {
            if (event.getCountry().toLowerCase().contains(searchString) || event.getCity().toLowerCase().contains(searchString) ||
                    event.getEventType().toLowerCase().contains(searchString) || String.valueOf(event.getYear()).toLowerCase().contains(searchString)) {

                Person currentPerson = new Person();
                for (Person person : peopleList) {
                    if (person.getPersonID().equals(event.getPersonID())) {
                        currentPerson = person;
                    }
                }
                //todo: adding filtered events

                if (!DataCache.showMaleEvents() && DataCache.showFemaleEvents()) {
                    if (!currentPerson.getGender().equals("f")) {
                        continue;
                    }
                    else {
                        if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                            if (!currentPerson.getGender().equals("f")) {
                                continue;
                            }
                        }
                        if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                            if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                                continue;
                            }
                        }
                        if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                            if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                                continue;
                            }
                        }
                    }
                }

                if (DataCache.showMaleEvents() && !DataCache.showFemaleEvents()) {
                    if (!currentPerson.getGender().equals("m")) {
                        continue;
                    }
                    else {
                        if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                            if (!currentPerson.getGender().equals("m")) {
                                continue;
                            }
                        }
                        if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                            if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                                continue;
                            }
                        }
                        if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                            if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                                continue;
                            }
                        }
                    }
                }

                else if (DataCache.showMaleEvents()) {
                    if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                        if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                            continue;
                        }
                    }
                    if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                        if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                            continue;
                        }
                    }
                }

                else if (!DataCache.showFemaleEvents()) {
                    continue;
                }
                //todo: end of filtered events

                filteredEventList.add(event);
            }
        }
        return filteredEventList;
    }

    public boolean findPerson(Person currentPerson, List<Person> personList) {
        for (Person person : personList) {
            if (person.getPersonID().equals(currentPerson.getPersonID())) {
                return true;
            }
        }
        return false;
    }
}
