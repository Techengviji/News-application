/*
 * MIT License
 *
 * Copyright (c) 2018 Soojeong Shin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.android.newsfeed;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener, DatePickerDialog.OnDateSetListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference numOfItems = findPreference(getString(R.string.settings_number_of_items_key));

            bindPreferenceSummaryToValue(numOfItems);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));

            bindPreferenceSummaryToValue(orderBy);

            Preference orderDate = findPreference(getString(R.string.settings_order_date_key));

            bindPreferenceSummaryToValue(orderDate);


            Preference colorTheme = findPreference(getString(R.string.settings_color_key));

            bindPreferenceSummaryToValue(colorTheme);

            Preference textSize = findPreference(getString(R.string.settings_text_size_key));

            bindPreferenceSummaryToValue(textSize);


            Preference fromDate = findPreference(getString(R.string.settings_from_date_key));
            setOnPreferenceClick(fromDate);

            bindPreferenceSummaryToValue(fromDate);
        }

        private void setOnPreferenceClick(Preference preference) {
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    String key = preference.getKey();
                    if (key.equalsIgnoreCase(getString(R.string.settings_from_date_key))) {
                        showDatePicker();
                    }
                    return false;
                }
            });
        }

        private void showDatePicker() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getActivity(),
                   this, year, month, dayOfMonth).show();
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {

            month = month + 1;

            String selectedDate = year + "-" + month + "-" + dayOfMonth;

            String formattedDate = formatDate(selectedDate);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(getString(R.string.settings_from_date_key), formattedDate).apply();

            Preference fromDatePreference = findPreference(getString(R.string.settings_from_date_key));
            bindPreferenceSummaryToValue(fromDatePreference);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {

            preference.setOnPreferenceChangeListener(this);

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        private String formatDate(String dateString) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
            Date dateObject = null;
            try {
                dateObject = simpleDateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat df= new SimpleDateFormat("yyyy-MM-dd");
            return df.format(dateObject);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
