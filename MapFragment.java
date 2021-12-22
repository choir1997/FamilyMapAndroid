package edu.byu.cs240.familymap.familymapclient;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import DataModel.Event;
import DataModel.Person;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    TextView markerInfo;
    ImageView genderImageView;
    MenuItem search;
    MenuItem settings;
    List<Marker> markerList = new ArrayList<>();
    Map<String, Float> eventToColorKeys = new HashMap<>();
    List<Event> eventList = DataCache.getEventList();
    List<Person> peopleList = DataCache.getPeopleList();
    List<Polyline> lineList = new ArrayList<>();
    List<Event> currentEventList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        DataCache.setMaleOnlyEvents(eventList);
        DataCache.setFemaleOnlyEvents(eventList);
        DataCache.setFatherSideEvents(peopleList);
        DataCache.setMotherSideEvents(peopleList);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);

        search = menu.findItem(R.id.searchMenuItem);
        settings = menu.findItem(R.id.settingsMenuItem);

        search.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_search).colorRes(R.color.white).actionBarSize());
        settings.setIcon(new IconDrawable(getActivity(), FontAwesomeIcons.fa_gear).colorRes(R.color.white).actionBarSize());

        if (DataCache.isSearchSettingsHidden()) {
            search.setVisible(false);
            settings.setVisible(false);
        }

        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                return false;
            }
        });

        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Inflate the layout for this fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        markerInfo = view.findViewById(R.id.mapTextView);
        genderImageView = view.findViewById(R.id.genderImageView);

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapLoadedCallback(this);

        List<Event> eventList = DataCache.getEventList();

        List<Float> colorList = new ArrayList<>();

        Random rn = new Random();
        rn.nextInt(361);

        colorList.add(BitmapDescriptorFactory.HUE_YELLOW);
        colorList.add(BitmapDescriptorFactory.HUE_BLUE);
        colorList.add(BitmapDescriptorFactory.HUE_ORANGE);
        colorList.add(BitmapDescriptorFactory.HUE_ROSE);
        colorList.add(BitmapDescriptorFactory.HUE_CYAN);
        colorList.add(BitmapDescriptorFactory.HUE_GREEN);
        colorList.add(BitmapDescriptorFactory.HUE_MAGENTA);
        colorList.add(BitmapDescriptorFactory.HUE_VIOLET);
        colorList.add(BitmapDescriptorFactory.HUE_AZURE);
        colorList.add(BitmapDescriptorFactory.HUE_RED);

        for (int i = 0; i < 25; i++) {
            colorList.add((float)rn.nextInt(361));
        }

        //todo: ASSIGNING KEY VALUE PAIRS FOR MARKER COLORS
        for (int i = 0 ; i < eventList.size(); i++) {
            if (!eventToColorKeys.containsKey(eventList.get(i).getEventType().toLowerCase())) {
                eventToColorKeys.put(eventList.get(i).getEventType().toLowerCase(), colorList.get(i));
            }
        }

        if (DataCache.getCurrentEventList().size() == 0) {
            setCurrentMarkers(eventList);
        }
        else {
            setCurrentMarkers(DataCache.getCurrentEventList());
        }


        DataCache.setMarkerList(markerList);

        markerInfo.setClickable(false);
        genderImageView.setClickable(false);

        if (DataCache.isClickedEventFromPersonActivity()) {
            for (Marker marker : markerList) {
                if (DataCache.getCurrentEvent() == marker.getTag()) {
                    try {
                        setPersonInfo(marker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (!lineList.isEmpty()) {
                    for (Polyline line : lineList) {
                        line.remove();
                    }
                }
                try {
                    setPersonInfo(marker);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (map != null) {
            map.clear();
            if (!DataCache.showMaleEvents() && DataCache.showFemaleEvents()) {
                if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    DataCache.setFemaleOnlyEvents(eventList);
                    setCurrentMarkers(DataCache.getFemaleEventList());
                    currentEventList = DataCache.getFemaleEventList();
                }
                if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                    DataCache.setFemaleOnlyEvents(DataCache.getMotherSideEvents());
                    setCurrentMarkers(DataCache.getFemaleEventList());
                    currentEventList = DataCache.getFemaleEventList();
                }
                if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    DataCache.setFemaleOnlyEvents(DataCache.getFatherSideEvents());
                    setCurrentMarkers(DataCache.getFemaleEventList());
                    currentEventList = DataCache.getFemaleEventList();
                }
            }
            if (DataCache.showMaleEvents() && !DataCache.showFemaleEvents()) {
                if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    DataCache.setMaleOnlyEvents(eventList);
                    setCurrentMarkers(DataCache.getMaleEventList());
                    currentEventList = DataCache.getMaleEventList();
                }
                if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                    DataCache.setMaleOnlyEvents(DataCache.getMotherSideEvents());
                    setCurrentMarkers(DataCache.getMaleEventList());
                    currentEventList = DataCache.getMaleEventList();
                }
                if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    DataCache.setMaleOnlyEvents(DataCache.getFatherSideEvents());
                    setCurrentMarkers(DataCache.getMaleEventList());
                    currentEventList = DataCache.getMaleEventList();
                }
            }
            else if (DataCache.showMaleEvents()) {
                if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    setCurrentMarkers(eventList);
                    currentEventList = eventList;
                }
                if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                    setCurrentMarkers(DataCache.getMotherSideEvents());
                    currentEventList = DataCache.getMotherSideEvents();
                }
                if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    setCurrentMarkers(DataCache.getFatherSideEvents());
                    currentEventList = DataCache.getFatherSideEvents();
                }
            }
            else if (!DataCache.showFemaleEvents()) {
                setCurrentMarkers(new ArrayList<>());
                currentEventList = null;
            }
            DataCache.setCurrentMarkerList(markerList);
            DataCache.setCurrentEventList(currentEventList);
        }

        if (DataCache.isClickedEventFromPersonActivity()) {
            for (Marker marker : markerList) {
                if (DataCache.getCurrentEvent() == marker.getTag()) {
                    try {
                        setPersonInfo(marker);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        Event currentEvent = DataCache.getCurrentEvent();
        boolean foundEvent = false;

        if (currentEvent != null && currentEventList != null) {
            Log.d("current event list size", String.valueOf(DataCache.getCurrentEventList().size()));

            for (Event event : currentEventList) {
                if (currentEvent.getEventID().equals(event.getEventID())) {
                    foundEvent = true;
                }
            }
            if (foundEvent) {

                for (Marker marker : markerList) {
                    if (DataCache.getCurrentEvent() == marker.getTag()) {
                        try {
                            setPersonInfo(marker);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }


                Person spouse = new Person();
                Person currentPerson = new Person();
                for (Person person : peopleList) {
                    if (person.getPersonID().equals(currentEvent.getPersonID())) {
                        currentPerson = person;
                    }
                }
                for (Person person : peopleList) {
                    if (person.getPersonID().equals(currentPerson.getSpouseID())) {
                        spouse = person;
                    }
                }
                List<Event> spouseEvents = new ArrayList<>();
                for (Event event : currentEventList) {
                    if (event.getPersonID().equals(spouse.getPersonID())) {
                        spouseEvents.add(event);
                    }
                }

                Collections.sort(spouseEvents, new Calculations.SortByType());
                Collections.sort(spouseEvents, new Calculations.SortByYear());

                boolean foundSpouse = false;

                if (!spouseEvents.isEmpty()) {
                    Event endEvent = spouseEvents.get(0);
                    for (Marker markerItem : markerList) {
                        if (markerItem.getTag() == endEvent) {
                            foundSpouse = true;
                        }
                    }
                    if (foundSpouse) {
                        if (DataCache.isShowSpouseLines()) {
                            drawAndRemoveLine(currentEvent, endEvent, Color.BLUE, 5);
                        }
                    }
                }

                //todo: drawing life story lines
                if (DataCache.isShowLifeStoryLines()) {
                    boolean foundStartEvent = false;
                    boolean foundEndEvent = false;
                    Event startEvent;
                    Event endEvent;
                    List<Event> eventListByID = DataCache.getEventListByPersonID();
                    for (int i = 0; i < eventListByID.size(); i++) {
                        if (i == eventListByID.size() - 1) {
                            break;
                        }
                        startEvent = eventListByID.get(i);
                        endEvent = eventListByID.get(i + 1);

                        for (Marker markerItem : markerList) {
                            if (markerItem.getTag() == startEvent) {
                                foundStartEvent = true;
                            }
                            if (markerItem.getTag() == endEvent) {
                                foundEndEvent = true;
                            }
                        }

                        if (foundStartEvent && foundEndEvent) {
                            drawAndRemoveLine(startEvent, endEvent, Color.RED, 5);
                        }
                    }
                }
                //todo: end of drawing life story lines

                if (DataCache.isShowFamilyTreeLines()) {
                    List<Person> childList = new ArrayList<>();
                    List<Event> newEventList = new ArrayList<>();
                    childList.add(currentPerson);
                    newEventList.add(DataCache.getCurrentEvent());

                    drawLineForAncestors(childList, newEventList, 20);
                }
            }
        }

        if (!foundEvent) {
            Log.d("EVENT NOT FOUND", "TRUE");
            markerInfo.setText(R.string.clickMarker);
            ViewGroup.LayoutParams layoutParams = markerInfo.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            markerInfo.setLayoutParams(layoutParams);
            genderImageView.setImageDrawable(null);
            markerInfo.setClickable(false);
            genderImageView.setClickable(false);
        }
    }

    public void setCurrentMarkers(List<Event> events) {
        markerList.clear();
        LatLng latlng;

        if (events.isEmpty()) {
            return;
        }
        for (Event event : events) {
            latlng = new LatLng(event.getLatitude(), event.getLongitude());
            Marker newMarker;

            for (Map.Entry<String, Float> entry : eventToColorKeys.entrySet()) {
                if (event.getEventType().toLowerCase().equals(entry.getKey())) {
                    float color = entry.getValue();
                    newMarker = map.addMarker(new MarkerOptions().position(latlng).icon(BitmapDescriptorFactory.defaultMarker(color)));
                    assert newMarker != null;
                    newMarker.setTag(event);
                    markerList.add(newMarker);
                }
            }
        }
    }

    @Override
    public void onMapLoaded() {
    }

    public void setPersonInfo(Marker marker) throws Exception {
        Event event = (Event) marker.getTag();

        DataCache.setCurrentEvent(event);

        assert event != null;
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(event.getLatitude(), event.getLongitude()), 3));

        List<Person> personList = DataCache.getPeopleList();
        String personName = null;
        Person currentPerson = null;

        for (Person person : personList) {
            if (person.getPersonID().equals(event.getPersonID())) {
                personName = person.getFirstName() + " " + person.getLastName();
                currentPerson = person;
            }
        }

        Person spouse = new Person();

        for (Person person : peopleList) {
            if (person.getPersonID().equals(currentPerson.getSpouseID())) {
                spouse = person;
            }
        }
        List<Event> spouseEvents = new ArrayList<>();
        for (Event eventItem : eventList) {
            if (eventItem.getPersonID().equals(spouse.getPersonID())) {
                spouseEvents.add(eventItem);
            }
        }

        Collections.sort(spouseEvents, new Calculations.SortByType());
        Collections.sort(spouseEvents, new Calculations.SortByYear());

        boolean foundSpouse = false;

        if (!spouseEvents.isEmpty()) {
            Event endEvent = spouseEvents.get(0);
            for (Marker markerItem : markerList) {
                if (markerItem.getTag() == endEvent) {
                    foundSpouse = true;
                }
            }
            if (foundSpouse) {
                if (DataCache.isShowSpouseLines()) {
                    drawAndRemoveLine(event, endEvent, Color.BLUE, 5);
                }
            }
        }

        PersonInfo personInfo = new PersonInfo();
        personInfo.setCurrentEventsPersonsLists(currentPerson);

        //drawing life story lines
        if (DataCache.isShowLifeStoryLines()) {
            boolean foundStartEvent = false;
            boolean foundEndEvent = false;
            Event startEvent;
            Event endEvent;
            List<Event> eventListByID = DataCache.getEventListByPersonID();
            for (int i = 0; i < eventListByID.size(); i++) {
                if (i == eventListByID.size() - 1) {
                    break;
                }
                startEvent = eventListByID.get(i);
                endEvent = eventListByID.get(i + 1);

                for (Marker markerItem : markerList) {
                    if (markerItem.getTag() == startEvent) {
                        foundStartEvent = true;
                    }
                    if (markerItem.getTag() == endEvent) {
                        foundEndEvent = true;
                    }
                }

                if (foundStartEvent && foundEndEvent) {
                    drawAndRemoveLine(startEvent, endEvent, Color.RED, 5);
                }
            }
        }
        //end of life story lines


        //drawing family tree lines
        if (DataCache.isShowFamilyTreeLines()) {
            List<Person> childList = new ArrayList<>();
            List<Event> newEventList = new ArrayList<>();
            childList.add(currentPerson);
            newEventList.add(DataCache.getCurrentEvent());
            drawLineForAncestors(childList, newEventList, 20);
        }
        //end of family tree lines

        String finalText = personName + "\n" + event.getEventType().toUpperCase() + ": "
                + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")";

        markerInfo.setText(finalText);
        ViewGroup.LayoutParams layoutParams = markerInfo.getLayoutParams();
        layoutParams.width = 800;
        markerInfo.setLayoutParams(layoutParams);

        Drawable genderIcon;

        if (currentPerson.getGender().equals("m")) {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.male_icon).sizeDp(40);
            genderImageView.setImageDrawable(genderIcon);
        } else {
            genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.female_icon).sizeDp(40);
        }

        genderImageView.setImageDrawable(genderIcon);

        markerInfo.setClickable(true);
        genderImageView.setClickable(true);

        Person finalCurrentPerson = currentPerson;
        markerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfo personInfo = new PersonInfo();
                try {
                    personInfo.setCurrentEventsPersonsLists(finalCurrentPerson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getContext(), PersonActivity.class);
                startActivity(intent);
            }
        });

        genderImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfo personInfo = new PersonInfo();
                try {
                    personInfo.setCurrentEventsPersonsLists(finalCurrentPerson);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getContext(), PersonActivity.class);
                startActivity(intent);
            }
        });
    }

    public void drawLineForAncestors(List<Person> childList, List<Event> newEventList, int width) {
        Person mother = new Person();
        Person father = new Person();

        List<Person> newChildList = new ArrayList<>();
        List<Event> newEventListUpdated = new ArrayList<>();

        Event startEvent = null;

        for (Person child : childList) {
            if (child.getMotherID() == null && child.getFatherID() == null) { //we are at last generation
                return;
            }
            for (Person person : peopleList) {
                if (child.getMotherID().equals(person.getPersonID())) {
                    mother = person;
                    newChildList.add(mother);
                    for (Event event : newEventList) {
                        if (event.getPersonID().equals(child.getPersonID())) {
                            startEvent = event;
                        }
                    }

                    Event endEvent = getFirstEventByPersonID(mother.getPersonID());
                    newEventListUpdated.add(endEvent);

                    boolean foundStartEvent = false;
                    boolean foundEndEvent = false;

                    for (Marker marker: markerList) {
                        if (marker.getTag() == startEvent) {
                            foundStartEvent = true;
                        }
                        if (marker.getTag() == endEvent) {
                            foundEndEvent = true;
                        }
                    }
                    if (endEvent != null && startEvent != null && foundStartEvent && foundEndEvent) {
                        drawAndRemoveLine(startEvent, endEvent, Color.BLACK, width);
                    }
                }
                if (child.getFatherID().equals(person.getPersonID())) {
                    father = person;
                    newChildList.add(father);
                    for (Event event : newEventList) {
                        if (event.getPersonID().equals(child.getPersonID())) {
                            startEvent = event;
                        }
                    }
                    Event endEvent = getFirstEventByPersonID(father.getPersonID());
                    newEventListUpdated.add(endEvent);

                    boolean foundStartEvent = false;
                    boolean foundEndEvent = false;

                    for (Marker marker: markerList) {
                        if (marker.getTag() == startEvent) {
                            foundStartEvent = true;
                        }
                        if (marker.getTag() == endEvent) {
                            foundEndEvent = true;
                        }
                    }
                    if (endEvent != null && startEvent != null && foundStartEvent && foundEndEvent) {
                        drawAndRemoveLine(startEvent, endEvent, Color.BLACK, width);
                    }
                }
            }
        }
        drawLineForAncestors(newChildList, newEventListUpdated, width - 7);
    }

    public Event getFirstEventByPersonID(String personID) {
        List<Event> tempList = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getPersonID().equals(personID)) {
                tempList.add(event);
            }
        }

        if (!tempList.isEmpty()) {
            return tempList.get(0);
        }

        return null;
    }

    public void drawAndRemoveLine(Event startEvent, Event endEvent, int googleColor, int width) {
        LatLng startPoint = new LatLng(startEvent.getLatitude(), startEvent.getLongitude());
        LatLng endPoint = new LatLng(endEvent.getLatitude(), endEvent.getLongitude());

        PolylineOptions options = new PolylineOptions()
                .add(startPoint)
                .add(endPoint)
                .color(googleColor)
                .width(width);

        Polyline line = map.addPolyline(options);

        lineList.add(line);
    }
}