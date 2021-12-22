package edu.byu.cs240.familymap.familymapclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import DataModel.Event;
import DataModel.Person;

public class PersonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        setTitle("Family Map: Person Details");

        ExpandableListView expandableListView = findViewById(R.id.expandableListView);
        TextView firstNameView = findViewById(R.id.personFirstName1);
        TextView lastNameView = findViewById(R.id.personLastName1);
        TextView genderView = findViewById(R.id.personGender1);

        Person currentPerson = DataCache.getCurrentPerson();

        firstNameView.setText(currentPerson.getFirstName());
        lastNameView.setText(currentPerson.getLastName());

        if (currentPerson.getGender().equals("f")) {
            genderView.setText(R.string.female);
        }
        else {
            genderView.setText(R.string.male);
        }

        List<Person> personItems = DataCache.getFamilyList();
        List<Event> eventItems = DataCache.getEventListByPersonID();

        Collections.sort(eventItems, new Calculations.SortByType());
        Collections.sort(eventItems, new Calculations.SortByYear());

        if (!DataCache.showMaleEvents() && DataCache.showFemaleEvents()) {
            if (!currentPerson.getGender().equals("f")) {
                eventItems.clear();
            }
            else {
                if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    if (!currentPerson.getGender().equals("f")) {
                        eventItems.clear();
                    }
                }
                if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                    if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                        eventItems.clear();
                    }
                }
                if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                        eventItems.clear();
                    }
                }
            }
        }

        if (DataCache.showMaleEvents() && !DataCache.showFemaleEvents()) {
            if (!currentPerson.getGender().equals("m")) {
                eventItems.clear();
            }
            else {
                if (DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    if (!currentPerson.getGender().equals("m")) {
                        eventItems.clear();
                    }
                }
                if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                    if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                        eventItems.clear();
                    }
                }
                if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                    if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                        eventItems.clear();
                    }
                }
            }
        }

        else if (DataCache.showMaleEvents()) {
            if (DataCache.isShowMotherSide() && !DataCache.isShowFatherSide()) {
                if (!findPerson(currentPerson, DataCache.getMotherSidePeople())) {
                    eventItems.clear();
                }
            }
            if (!DataCache.isShowMotherSide() && DataCache.isShowFatherSide()) {
                if (!findPerson(currentPerson, DataCache.getFatherSidePeople())) {
                    eventItems.clear();
                }
            }
        }

        else if (!DataCache.showFemaleEvents()) {
            eventItems.clear();
        }
        expandableListView.setAdapter(new ExpandableListAdapter(eventItems, personItems));
    }

    public boolean findPerson(Person currentPerson, List<Person> personList) {
        for (Person person : personList) {
            if (person.getPersonID().equals(currentPerson.getPersonID())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

    private class ExpandableListAdapter extends BaseExpandableListAdapter {
        private static final int EVENT_ITEMS_GROUP_POSITION = 0;
        private static final int PERSON_ITEMS_GROUP_POSITION = 1;

        private final List<Event> eventItems;
        private final List<Person> personItems;

        private ExpandableListAdapter(List<Event> eventItems, List<Person> personItems) {
            this.eventItems = eventItems;
            this.personItems = personItems;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_ITEMS_GROUP_POSITION:
                    return eventItems.size();
                case PERSON_ITEMS_GROUP_POSITION:
                    return personItems.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.expandablelist_groups, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_ITEMS_GROUP_POSITION:
                    titleView.setText(R.string.eventItemsTitle);
                    break;
                case PERSON_ITEMS_GROUP_POSITION:
                    titleView.setText(R.string.familyItemsTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            itemView = getLayoutInflater().inflate(R.layout.expandablelist_items, parent, false);
            initializeItemView(itemView, childPosition, groupPosition);



            return itemView;
        }

        private void initializeItemView(View eventFamilyItemView, final int childPosition, final int groupPosition) {

            TextView itemsTextView = eventFamilyItemView.findViewById(R.id.listItemTextView);
            ImageView imageView = eventFamilyItemView.findViewById(R.id.listItemImageView);
            String fatherText = null;
            String motherText = null;
            String spouseText = null;
            String childText = null;
            String eventText = null;

            if (groupPosition == 0) {

                //TODO: MUST SORT EVENT ITEMS IN CHRONOLOGICAL ORDER AND THEN ALPHABETICAL
                eventText = eventItems.get(childPosition).getEventType().toUpperCase() + ": "
                        + eventItems.get(childPosition).getCity() + ", " + eventItems.get(childPosition).getCountry() + " (" + eventItems.get(childPosition).getYear() + ")" +
                "\n\n" + DataCache.getCurrentPerson().getFirstName() + " " + DataCache.getCurrentPerson().getLastName();
            }
            else if (groupPosition == 1) {
                if (personItems.get(childPosition) == null && (childPosition == 0 || childPosition == 1)) {
                    fatherText = "Father" + "\n\n" + "N/A";
                    motherText = "Mother" + "\n\n" + "N/A";
                } else if (childPosition == 0 || childPosition == 1) {
                    fatherText = "Father" + "\n\n" + personItems.get(childPosition).getFirstName() + " " + personItems.get(childPosition).getLastName();
                    motherText = "Mother" + "\n\n" + personItems.get(childPosition).getFirstName() + " " + personItems.get(childPosition).getLastName();
                }

                if (personItems.get(childPosition) == null && (childPosition == 2)) {
                    spouseText = "Spouse" + "\n\n" + "N/A";
                } else if (childPosition == 2) {
                    spouseText = "Spouse" + "\n\n" + personItems.get(childPosition).getFirstName() + " " + personItems.get(childPosition).getLastName();
                }

                if (personItems.get(childPosition) == null && (childPosition == 3)) {
                    childText = "Child" + "\n\n" + "N/A";
                } else if (childPosition == 3) {
                    childText = "Child" + "\n\n" + personItems.get(childPosition).getFirstName() + " " + personItems.get(childPosition).getLastName();
                }
            }

            switch (groupPosition) {
                case EVENT_ITEMS_GROUP_POSITION:
                    itemsTextView.setText(eventText);
                    Drawable eventIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).
                            colorRes(R.color.teal_700).sizeDp(25);
                    imageView.setImageDrawable(eventIcon);

                    itemsTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(PersonActivity.this, "pressed item", Toast.LENGTH_LONG).show();
                            DataCache.setSearchSettingsHidden(true);
                            DataCache.setClickedEventFromPersonActivity(true);
                            DataCache.setCurrentEvent(eventItems.get(childPosition));

                            Intent intent = new Intent(PersonActivity.this, EventActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
                case PERSON_ITEMS_GROUP_POSITION:
                    String text;

                    switch (childPosition) {
                        case 0:
                            text = fatherText;
                            break;
                        case 1:
                            text = motherText;
                            break;
                        case 2:
                            text = spouseText;
                            break;
                        case 3:
                            text = childText;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected value: " + childPosition);
                    }

                    itemsTextView.setText(text);

                    if (personItems.get(childPosition) == null) {
                        Drawable times = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_times).
                                colorRes(R.color.black).sizeDp(25);
                        imageView.setImageDrawable(times);
                    }

                    else if (personItems.get(childPosition).getGender().equals("f")) {
                        Drawable femaleIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                                colorRes(R.color.female_icon).sizeDp(25);
                        imageView.setImageDrawable(femaleIcon);
                    }
                    else {
                        Drawable maleIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                                colorRes(R.color.male_icon).sizeDp(25);
                        imageView.setImageDrawable(maleIcon);
                    }

                    if (personItems.get(childPosition) != null) {
                        itemsTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PersonInfo personInfo = new PersonInfo();
                                try {
                                    personInfo.setCurrentEventsPersonsLists(personItems.get(childPosition));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent = new Intent(PersonActivity.this, PersonActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position");
            }
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {

            return true;
        }

    }
}