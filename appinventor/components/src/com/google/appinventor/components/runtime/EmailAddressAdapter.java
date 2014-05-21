// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.util.Log;

import com.google.appinventor.components.runtime.util.EclairUtil;
import com.google.appinventor.components.runtime.util.SdkLevel;

/**
 * EmailAddressAdapter provides email address completion from contacts,
 * for use as a list adapter.
 *
 * <p>Note that most of this code was copied from
 * partner/google/apps/Gmail/src/com/google/android/gm/EmailAddressAdapter.java
 *
 * @author sharon@google.com (Sharon Perl)
 * @author hal@mit.edu (Hal Abelson)
 */

public class EmailAddressAdapter extends ResourceCursorAdapter {

  private static final boolean DEBUG = false;
  private static final String TAG = "EmailAddressAdapter";

  public static final int OLD_NAME_INDEX = 1;
  public static final int OLD_DATA_INDEX = 2;

  private static String SORT_ORDER;

  private ContentResolver contentResolver;

  private Context context;

  private static final String[] OLD_PROJECTION = {
    ContactMethods._ID,    // 0
    ContactMethods.NAME,   // 1
    ContactMethods.DATA,   // 2
  };

  private static final String[] NEW_PROJECTION = EclairUtil.getEmailAdapterProjection();

  public EmailAddressAdapter(Context context) {
    super(context, android.R.layout.simple_dropdown_item_1line, null);
    contentResolver = context.getContentResolver();
    this.context = context;
    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
      SORT_ORDER = EclairUtil.getTimesContacted() + " DESC, " + EclairUtil.getDisplayName();
    } else {
      SORT_ORDER = People.TIMES_CONTACTED + " DESC, " + People.NAME;
    }
  }

  @Override
  public final String convertToString(Cursor cursor) {

    int NEW_NAME_INDEX = cursor.getColumnIndex(EclairUtil.getDisplayName());
    int NEW_EMAIL_INDEX = cursor.getColumnIndex(EclairUtil.getEmailAddress());
    String name = "";
    String address = "";

    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
      name = cursor.getString(NEW_NAME_INDEX);
      address = cursor.getString(NEW_EMAIL_INDEX);
    } else {
      name = cursor.getString(OLD_NAME_INDEX);
      address = cursor.getString(OLD_DATA_INDEX);
    }

    return new Rfc822Token(name, address, null).toString();
  }

  private final String makeDisplayString(Cursor cursor) {

    int NEW_NAME_INDEX = cursor.getColumnIndex(EclairUtil.getDisplayName());
    int NEW_EMAIL_INDEX = cursor.getColumnIndex(EclairUtil.getEmailAddress());
    StringBuilder s = new StringBuilder();
    boolean flag = false;
    String name = "";
    String address = "";

    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
      name = cursor.getString(NEW_NAME_INDEX);
      address = cursor.getString(NEW_EMAIL_INDEX);
    } else {
      name = cursor.getString(OLD_NAME_INDEX);
      address = cursor.getString(OLD_DATA_INDEX);
    }

    if (!TextUtils.isEmpty(name)) {
      s.append(name);
      flag = true;
    }

    if (flag) {
      s.append(" <");
    }

    s.append(address);

    if (flag) {
      s.append(">");
    }

    return s.toString();
  }

  @Override
  public final void bindView(View view, Context context, Cursor cursor) {
    ((TextView) view).setText(makeDisplayString(cursor));
  }

  @Override
  public Cursor runQueryOnBackgroundThread(CharSequence constraint) {

    String where = null;
    android.net.Uri db = null;
    StringBuilder s = new StringBuilder();

    if (constraint != null) {
      String filter = DatabaseUtils.sqlEscapeString(constraint.toString() + '%');

      if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
        db = EclairUtil.getDataContentUri();
        s.append("(" + EclairUtil.getDataMimeType() + "='" + EclairUtil.getEmailType() + "')");
        s.append(" AND ");
        s.append("(display_name LIKE ");
        s.append(filter);
        s.append(")");
      } else {
        db = ContactMethods.CONTENT_EMAIL_URI;
        s.append("(name LIKE ");
        s.append(filter);
        s.append(") OR (display_name LIKE ");
        s.append(filter);
        s.append(")");
      }
    }
    where = s.toString();

    // Note(hal): This lists the column names in the table being accessed, since they aren't
    // obvious to me from the documentation
    if (DEBUG) {
      Cursor c = context.getContentResolver().query(db, null, null, null, null, null);
      Log.d(TAG, "listing columns");
      for (int i = 0; i<c.getColumnCount(); i++) {
        Log.d(TAG, "column " + i + "=" + c.getColumnName(i));
      }
    }

    if (SdkLevel.getLevel() >= SdkLevel.LEVEL_ECLAIR) {
      return contentResolver.query(db, NEW_PROJECTION,
          where, null, SORT_ORDER);
    } else {
      return contentResolver.query(db, OLD_PROJECTION,
          where, null, SORT_ORDER);
    }
  }
}

