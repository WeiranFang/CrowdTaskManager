package com.example.weiranfang.crowdtaskmanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "CreateTaskActivity";

    private UserLocalStore userLocalStore;

    private Button createButton;
    private EditText titleEditText, participantsEditText, awardEditText, contentEditText, deadlineEditText;
    private Spinner categorySpinner, durationSpinner;
    private DatePickerDialog deadlineDialog;
    private DateFormat dateFormat;
    private AutoCompleteTextView locationAutoCompleteTextView;
    private GoogleApiClient googleApiClient;

    private LocationAutocompleteAdapter locationAutocompleteAdapter;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        setContentView(R.layout.activity_create_task);

        userLocalStore = new UserLocalStore(this);

        findViews();
        setDeadlineDialog();
        setAutoCompleteView();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        deadlineEditText.setText(dateFormat.format(new Date()));
        deadlineEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    deadlineDialog.show();
                }
            }
        });
        deadlineEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadlineDialog.show();
            }
        });



    }

    private void setAutoCompleteView() {
        locationAutocompleteAdapter = new LocationAutocompleteAdapter(this, googleApiClient, BOUNDS_GREATER_SYDNEY, null);
        locationAutoCompleteTextView.setAdapter(locationAutocompleteAdapter);
        locationAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
                final AutocompletePrediction item = locationAutocompleteAdapter.getItem(position);
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);

                Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(updatePlaceDetailsCallback);

                Toast.makeText(getApplicationContext(), "Clicked: " + primaryText,
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
            }
        });
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> updatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            latLng = place.getLatLng();

            // Format details of the place for display and show it in a TextView.
//            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
//                    place.getId(), place.getAddress(), place.getPhoneNumber(),
//                    place.getWebsiteUri()));

            // Display the third party attributions if set.
//            final CharSequence thirdPartyAttribution = places.getAttributions();
//            if (thirdPartyAttribution == null) {
//                mPlaceDetailsAttribution.setVisibility(View.GONE);
//            } else {
//                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
//                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
//            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private void setDeadlineDialog() {
        Calendar newCalendar = Calendar.getInstance();
        deadlineDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                deadlineEditText.setText(dateFormat.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void findViews() {
        createButton = (Button) findViewById(R.id.createButton);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        participantsEditText = (EditText) findViewById(R.id.participantsEditText);
        awardEditText = (EditText) findViewById(R.id.awardEditText);
        contentEditText = (EditText) findViewById(R.id.contentEditText);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        durationSpinner = (Spinner) findViewById(R.id.durationSpinner);
        deadlineEditText = (EditText) findViewById(R.id.deadlineEditText);
        locationAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.locationAutoCompleteTextView);
    }

    public void clickCreateButton(View view) {
        String title = titleEditText.getText().toString().trim();
        if (title == null || title.equals("")) {
            showErrorMessage("Empty title!");
            return;
        }

        String category = categorySpinner.getSelectedItem().toString();
        if (category == null || category.equals("select")) {
            showErrorMessage("Please choose category!");
            return;
        }

        int duration = durationSpinner.getSelectedItemPosition() - 1;
        if (duration < 0) {
            showErrorMessage("Please choose time cost!");
            return;
        }

        String deadLine = deadlineEditText.getText().toString().trim();

        int participants = 0;
        try {
            participants = Integer.parseInt(participantsEditText.getText().toString());
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid participants!");
            return;
        }
        if (participants <= 0) {
            showErrorMessage("Invalid participants!");
            return;
        }

        int award = 0;
        try {
            award = Integer.parseInt(awardEditText.getText().toString());
        } catch (NumberFormatException e) {
            showErrorMessage("Invalid award!");
            return;
        }
        if (award < 0) {
            showErrorMessage("Invalid award!");
            return;
        }


        String content = contentEditText.getText().toString().trim();
        if (content == null || content.equals("")) {
            showErrorMessage("Empty content!");
            return;
        }

        if (latLng == null) {
            showErrorMessage("Invalid location!");
            return;
        }

        User currentUser = userLocalStore.getLoggedInUser();
        int creatorId = currentUser.userId;
//        Task newTask = new Task(award, participants, creatorId, duration, title, content, category, deadLine);
        Task newTask = new Task(award, participants, creatorId, duration, title, content, category, deadLine, latLng.latitude, latLng.longitude);
        uploadTask(newTask);

    }

    private void uploadTask(Task task) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeTaskInBackground(task, new GetTaskCallBack() {
            @Override
            public void done(Task returnedTask) {
                startActivity(new Intent(CreateTaskActivity.this, MainActivity.class));
            }
        });
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(CreateTaskActivity.this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
