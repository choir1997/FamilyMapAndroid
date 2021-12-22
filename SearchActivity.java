package edu.byu.cs240.familymap.familymapclient;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;

import DataModel.Event;
import DataModel.Person;
import Service.PersonService;

public class SearchActivity extends AppCompatActivity {
    private EditText searchText;
    private ImageView clearIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setTitle("Family Map: Search");

        ImageView searchIcon = findViewById(R.id.SearchBarIcon);
        Drawable search = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_search).
                colorRes(R.color.grey).sizeDp(25);
        searchIcon.setImageDrawable(search);

        searchText = findViewById(R.id.SearchBar);

        clearIcon = findViewById(R.id.ClearIcon);
        Drawable clear = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_times_circle).
                colorRes(R.color.grey).sizeDp(25);
        clearIcon.setImageDrawable(clear);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                List<Event> filteredEventList = new ArrayList<>();
                List<Person> filteredPersonList = new ArrayList<>();

                clearIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchText.setText("");
                    }
                });
                String currentSearchString = searchText.getText().toString().toLowerCase();

                if (searchText.getText().toString().length() != 0) {
                    Calculations calculations = new Calculations();
                    filteredEventList = calculations.searchEventFilter(currentSearchString);
                    filteredPersonList = calculations.searchPersonFilter(currentSearchString);
                }

                RecyclerView recyclerView = findViewById(R.id.RecyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                SearchAdapter adapter = new SearchAdapter(filteredEventList, filteredPersonList);
                recyclerView.setAdapter(adapter);
            }
        };

        searchText.addTextChangedListener(textWatcher);
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
        private final List<Event> eventList;
        private final List<Person> personList;

        SearchAdapter(List<Event> eventList, List<Person> personList) {
            this.eventList = eventList;
            this.personList = personList;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            view = getLayoutInflater().inflate(R.layout.expandablelist_items, parent, false);

            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if (position < personList.size()) {
                holder.bind(personList.get(position));
            }
            else {
                holder.bind(eventList.get(position - personList.size()));
            }
        }

        @Override
        public int getItemCount() { return eventList.size() + personList.size(); }

        private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView textView;
            private ImageView itemIcon;
            private Event event;
            private Person person;

            SearchViewHolder(View view, int viewType) {
                super(view);

                itemView.setOnClickListener(this);

                itemIcon = itemView.findViewById(R.id.listItemImageView);
                textView = itemView.findViewById(R.id.listItemTextView);
            }

            private void bind(Person person) {
                this.person = person;
                 if (person.getGender().equals("f")) {
                    Drawable femaleIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                            colorRes(R.color.female_icon).sizeDp(25);
                    itemIcon.setImageDrawable(femaleIcon);
                }
                else {
                    Drawable maleIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                            colorRes(R.color.male_icon).sizeDp(25);
                    itemIcon.setImageDrawable(maleIcon);
                }

                String personString = person.getFirstName() + " " + person.getLastName();
                textView.setText(personString);

                textView.setClickable(true);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //DataCache.setCurrentPerson(person);

                        PersonInfo personInfo = new PersonInfo();
                        try {
                            personInfo.setCurrentEventsPersonsLists(person);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(SearchActivity.this, PersonActivity.class);
                        startActivity(intent);
                    }
                });
            }

            private void bind(Event event) {
                List<Person> peopleList = DataCache.getPeopleList();
                Person currentPerson = new Person();
                for (Person person : peopleList) {
                    if (person.getPersonID().equals(event.getPersonID())) {
                        currentPerson = person;
                    }
                }

                this.event = event;
                String eventString = event.getEventType().toUpperCase() + ": "
                        + event.getCity() + ", " + event.getCountry() + " (" + event.getYear() + ")" +
                        "\n\n" + currentPerson.getFirstName() + " " + currentPerson.getLastName();

                textView.setText(eventString);
                Drawable eventIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).
                        colorRes(R.color.teal_700).sizeDp(25);
                itemIcon.setImageDrawable(eventIcon);

                textView.setClickable(true);

                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DataCache.setCurrentEvent(event);
                        DataCache.setClickedEventFromPersonActivity(true);
                        Intent intent = new Intent(SearchActivity.this, EventActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onClick(View view) {
                Toast.makeText(SearchActivity.this, "clicked on item", LENGTH_SHORT).show();
            }
        }
    }
}