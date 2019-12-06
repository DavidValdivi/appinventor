package com.google.appinventor.components.runtime;

import org.osmdroid.util.GeoPoint;
import org.json.JSONObject;

import android.app.Activity;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.PropertyCategory;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.AsynchUtil;
import com.google.appinventor.components.runtime.util.ErrorMessages;
import com.google.appinventor.components.runtime.util.GeoJSONUtil;
import com.google.appinventor.components.runtime.util.JsonUtil;
import com.google.appinventor.components.runtime.util.MapFactory.MapFeatureContainer;
import com.google.appinventor.components.runtime.util.MapFactory.MapLineString;
import com.google.appinventor.components.runtime.Web;

import static com.google.appinventor.components.runtime.util.GeometryUtil.isValidLatitude;
import static com.google.appinventor.components.runtime.util.GeometryUtil.isValidLongitude;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ArrayList;

@DesignerComponent(version = YaVersion.NAVIGATION_COMPONENT_VERSION,
    category = ComponentCategory.MAPS,
    description = "Navigation",
    nonVisible = true)
@UsesPermissions(permissionNames = "android.permission.INTERNET")
@UsesLibraries(libraries = "json.jar")
@SimpleObject
public class Navigation extends AndroidNonvisibleComponent implements Component {

  private static final String TAG = "Navigation";

  public static final String OPEN_ROUTE_SERVICE_URL = "https://api.openrouteservice.org/v2/directions/%s/geojson/";
  private final Activity activity;
  private String apiKey;
  private GeoPoint startLocation;
  private GeoPoint endLocation;
  private TransportMethod method;
  private Web web;

  enum TransportMethod {
    DEFAULT ("foot-walking"),
    DRIVING ("driving-car"),
    CYCLING ("cycling-regular"),
    WALKING ("foot-walking"),
    WHEELCHAIR ("wheelchair");

    private final String method;
    
    TransportMethod(String method) {
      this.method = method;
    }
    
    private String method() { 
      return method;
    }
  }

  /**
   * Creates a Navigation component.
   *
   * @param container container, component will be placed in
   */
  public Navigation(ComponentContainer container) {
    super(container.$form());
    activity = container.$context();
    apiKey = "";
    startLocation = new GeoPoint(0, 0);
    endLocation = new GeoPoint(0, 0);
    method = TransportMethod.DEFAULT;
  }

  @SimpleFunction(description = "")
  public void RequestDirections() {
    AsynchUtil.runAsynchronously(new Runnable() {
      @Override
      public void run() {
        try {
          performRequest();
        } catch (IOException e) {
          form.dispatchErrorOccurredEvent(Navigation.this, "RequestDirections",
              ErrorMessages.ERROR_DEFAULT);
        } catch (JSONException je) {
          form.dispatchErrorOccurredEvent(Navigation.this, "RequestDirections",
              ErrorMessages.ERROR_DEFAULT);
        }
      }
    });
  }

  private void performRequest() throws IOException, JSONException {
    final String finalURL = String.format(OPEN_ROUTE_SERVICE_URL, this.method.method());
    URL url = new URL(finalURL);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Authorization", apiKey);
    if (connection != null) {
      try {
        String coords = "{\"coordinates\": " + JsonUtil.getJsonRepresentation(getCoordinates()) + "}";
        byte[] postData = coords.getBytes(StandardCharsets.UTF_8);
        connection.setFixedLengthStreamingMode(postData.length);
        BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());
        try {
          out.write(postData, 0, postData.length);
          out.flush();
        } finally {
          out.close();
        }
        
        final String geoJson = getResponseContent(connection);
        final String coordinates = getCoordinatesFromGeoJson(geoJson);

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            GotDirections(coordinates);
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        connection.disconnect();
      }
    }
  }

  private static String getResponseContent(HttpURLConnection connection) throws IOException {
    String encoding = connection.getContentEncoding();
    if (encoding == null) {
      encoding = "UTF-8";
    }
    Log.d(TAG, Integer.toString(connection.getResponseCode()));
    InputStreamReader reader = new InputStreamReader(connection.getInputStream(), encoding);
    try {
      int contentLength = connection.getContentLength();
      StringBuilder sb = (contentLength != -1)
          ? new StringBuilder(contentLength)
          : new StringBuilder();
      char[] buf = new char[1024];
      int read;
      while ((read = reader.read(buf)) != -1) {
        sb.append(buf, 0, read);
      }
      return sb.toString();
    } finally {
      reader.close();
    }
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_STRING,
      defaultValue = "")
  @SimpleProperty
  public void ApiKey(String key) {
    apiKey = key;
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LATITUDE,
      defaultValue = "0.0")
  @SimpleProperty
  public void StartLatitude(double latitude) {
    if (isValidLatitude(latitude)) {
      startLocation.setLatitude(latitude);
    } else {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "StartLatitude",
          ErrorMessages.ERROR_INVALID_LATITUDE, latitude);
    }
  }

  @SimpleProperty(category = PropertyCategory.APPEARANCE,
      description = "The latitude of the start location.")
  public double StartLatitude() {
    return startLocation.getLatitude();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LONGITUDE,
      defaultValue = "0.0")
  @SimpleProperty
  public void StartLongitude(double longitude) {
    if (isValidLongitude(longitude)) {
      startLocation.setLongitude(longitude);
    } else {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "StartLongitude",
          ErrorMessages.ERROR_INVALID_LONGITUDE, longitude);
    }
  }

  @SimpleProperty(category = PropertyCategory.APPEARANCE,
      description = "The longitude of the start location.")
  public double StartLongitude() {
    return startLocation.getLongitude();
  }

  @SimpleFunction(description = "Set the start location.")
  public void SetStartLocation(double latitude, double longitude) {
    if (!isValidLatitude(latitude)) {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "SetStartLocation",
          ErrorMessages.ERROR_INVALID_LATITUDE, latitude);
    } else if (!isValidLongitude(longitude)) {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "SetStartLocation",
          ErrorMessages.ERROR_INVALID_LONGITUDE, longitude);
    } else {
      startLocation.setCoords(latitude, longitude);
    }
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LATITUDE,
      defaultValue = "0.0")
  @SimpleProperty
  public void EndLatitude(double latitude) {
    if (isValidLatitude(latitude)) {
      endLocation.setLatitude(latitude);
    } else {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "EndLatitude",
          ErrorMessages.ERROR_INVALID_LATITUDE, latitude);
    }
  }

  @SimpleProperty(category = PropertyCategory.APPEARANCE,
      description = "The latitude of the end location.")
  public double EndLatitude() {
    return endLocation.getLatitude();
  }

  @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LONGITUDE,
      defaultValue = "0.0")
  @SimpleProperty
  public void EndLongitude(double longitude) {
    if (isValidLongitude(longitude)) {
      endLocation.setLongitude(longitude);
    } else {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "EndLongitude",
          ErrorMessages.ERROR_INVALID_LONGITUDE, longitude);
    }
  }

  @SimpleProperty(category = PropertyCategory.APPEARANCE,
      description = "The longitude of the end location.")
  public double EndLongitude() {
    return endLocation.getLongitude();
  }

  @SimpleFunction(description = "Set the end location.")
  public void SetEndLocation(double latitude, double longitude) {
    if (!isValidLatitude(latitude)) {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "SetEndLocation",
          ErrorMessages.ERROR_INVALID_LATITUDE, latitude);
    } else if (!isValidLongitude(longitude)) {
      getDispatchDelegate().dispatchErrorOccurredEvent(this, "SetEndLocation",
          ErrorMessages.ERROR_INVALID_LONGITUDE, longitude);
    } else {
      endLocation.setCoords(latitude, longitude);
    }
  }

  private Double[][] getCoordinates() {
    Double[][] coords = new Double[2][2];
    coords[0][0] = startLocation.getLongitude();
    coords[0][1] = startLocation.getLatitude();
    coords[1][0] = endLocation.getLongitude();
    coords[1][1] = endLocation.getLatitude();
    return coords;
  }

  private String getCoordinatesFromGeoJson(String geoJson) {
    String coordString = "";
    try {
      ArrayList<Object> o = (ArrayList<Object>) JsonUtil.getObjectFromJson(geoJson);
      for (Object x : o) {
        ArrayList<Object> entry = (ArrayList<Object>) x;
        if (entry.get(0).toString().equals("features")) {
          ArrayList<Object> feature = (ArrayList<Object>) ((ArrayList<Object>) entry.get(1)).get(0);
          for (Object y : feature) {
            ArrayList<Object> attr = (ArrayList<Object>) y;
            if (attr.get(0).toString().equals("geometry")) {
              ArrayList<Object> geometry = (ArrayList<Object>) attr.get(1);
              for (Object z : geometry) {
                attr = (ArrayList<Object>) z;
                if (attr.get(0).toString().equals("coordinates")) {
                  ArrayList<Object> coordList = (ArrayList<Object>) attr.get(1);
                  coordString = JsonUtil.getJsonRepresentation(coordList);
                }
                break;
              }
              break;
            }
          }
          break;
        }
      }
    } finally {
      return coordString;
    }
  }

  // @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_LONGITUDE,
  //     defaultValue = "0")
  // @SimpleProperty
  // public void TransportationMethod(String method) {

  // }

  // public String TransportationMethod() {
  //   return method;
  // }

  /**
   * Event indicating that a request has finished and has returned data (translation).
   *
   * @param responseCode the response code from the server
   * @param directions the response content from the server
   */
  @SimpleEvent(description = "Event triggered when the Yandex.Translate service returns the " +
      "translated text. This event also provides a response code for error handling. If the " +
      "responseCode is not 200, then something went wrong with the call, and the translation will " +
      "not be available.")
  public void GotDirections(String directions) {
    Log.d(TAG, "GotDirections");
    EventDispatcher.dispatchEvent(this, "GotDirections", directions);
  }
}