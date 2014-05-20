// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.components.runtime;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;
import android.util.Log;


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

// TODO(halabelson): Get rid of the use of android.provder.Contacts (deprecated) and
// replace by android.provider.contactsContract and associated methods
// Update(niki): Should be finished

public class EmailAddressAdapter extends ResourceCursorAdapter {

  private static final boolean DEBUG = false;
  private static final String TAG = "EmailAddressAdapter";


  private static final String SORT_ORDER = Data.TIMES_CONTACTED + " DESC, " + Data.DISPLAY_NAME;
  private ContentResolver contentResolver;

  private Context context;

  private static final String [] PROJECTION = {
    Data._ID,
    Data.DISPLAY_NAME,
    Email.ADDRESS,
    Data.MIMETYPE
  };
  
  
  public EmailAddressAdapter(Context context) {
    super(context, android.R.layout.simple_dropdown_item_1line, null);
    contentResolver = context.getContentResolver();
    this.context = context;
  }

  @Override
  public final String convertToString(Cursor cursor) {
    
    int NAME_INDEX = cursor.getColumnIndex(Data.DISPLAY_NAME);
    int EMAIL_INDEX = cursor.getColumnIndex(Email.ADDRESS);
    
    String name = cursor.getString(NAME_INDEX);
    String address = cursor.getString(EMAIL_INDEX);

    return new Rfc822Token(name, address, null).toString();
  }

  private final String makeDisplayString(Cursor cursor) {
    
    int NAME_INDEX = cursor.getColumnIndex(Data.DISPLAY_NAME);
    int EMAIL_INDEX = cursor.getColumnIndex(Email.ADDRESS);
    StringBuilder s = new StringBuilder();
    boolean flag = false;
    
    String name = cursor.getString(NAME_INDEX);
    String address = cursor.getString(EMAIL_INDEX);

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
    android.net.Uri db = Data.CONTENT_URI;

    StringBuilder s = new StringBuilder();
    s.append("(" + Data.MIMETYPE + "='" + Email.CONTENT_ITEM_TYPE + "')");
    if (constraint != null) {
      String filter = DatabaseUtils.sqlEscapeString(constraint.toString() + '%');
      
      s.append(" AND ");
      s.append("(display_name LIKE ");
      s.append(filter);
     // s.append(") OR (display_name LIKE ");
     // s.append(filter);
      s.append(")");
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
    
    return contentResolver.query(db, PROJECTION,
        where, null, SORT_ORDER);
  }
}

