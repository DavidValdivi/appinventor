package com.google.appinventor.components.runtime;

import android.os.Environment;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.PropertyTypeConstants;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import android.Manifest;

@DesignerComponent(version = YaVersion.FILE_COMPONENT_VERSION,
        description = "To be updated",
        category = ComponentCategory.STORAGE,
        nonVisible = true,
        iconName = "images/file.png")
@SimpleObject
@UsesPermissions(permissionNames = "android.permission.WRITE_EXTERNAL_STORAGE, android.permission.READ_EXTERNAL_STORAGE")
public class CSVFile extends AndroidNonvisibleComponent {
    private String sourceFile;

    private YailList rows;
    private YailList columns;
    private YailList columnNames; // Elements of the first column

    // private Thread readThread; // Async thread for parsing CSV
    private ExecutorService threadRunner;

    /**
     * Creates a new CSVFile component.
     *
     * @param form the container that this component will be placed in
     */
    public CSVFile(Form form) {
        super(form);

        rows = new YailList();
        columns = new YailList();

        threadRunner = Executors.newSingleThreadExecutor();
    }

    // Reads from stored file. To be integrated
//    private void parseCSVFromSource(final String filename) {
//        form.askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionResultHandler() {
//            @Override
//            public void HandlePermissionResponse(String permission, boolean granted) {
//                if (granted) {
//                    try {
//                        String path = Environment.getExternalStorageDirectory().getPath() + filename;;
//
//                        byte[] bytes = FileUtil.readFile(path);
//                        String csvTable = new String(bytes);
//
//                        rows = CsvUtil.fromCsvTable(csvTable);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        rows = YailList.makeList(Collections.singletonList(e.getMessage()));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        rows = YailList.makeList(Collections.singletonList(e.getMessage()));
//                    }
//                } else {
//                    form.dispatchPermissionDeniedEvent(CSVFile.this, "ReadFrom", permission);
//                }
//            }
//        });
//    }

    /**
     * Parses the CSV contents of the provided file.
     *
     * @param filename  name of the file. Single slash (/) indicates Android file,
     *                  double slash (//) indicates media file.
     */
    private void parseCSVFromSource(final String filename) {
        form.askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionResultHandler() {
            @Override
            public void HandlePermissionResponse(String permission, boolean granted) {
                if (granted) {
                    try {
                        // TODO: Establish path properly (like in File class)

                        // Open asset file
                        final InputStream inputStream = form.openAsset(filename);

                        threadRunner.execute(new Runnable() {
                            @Override
                            public void run() {
                                readCSV(inputStream);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    form.dispatchPermissionDeniedEvent(CSVFile.this, "ReadFrom", permission);
                }
            }
        });
    }

    /**
     * Parses the CSV contents of the provided InputStream.
     *
     * @param inputStream  InputStream to parse CSV from.
     */
    private void readCSV(InputStream inputStream) {
        try {
            // TODO: Taken form File.java. To be replaced to reduce redundancy.
            InputStreamReader input = new InputStreamReader(inputStream);
            StringWriter output = new StringWriter();
            char [] buffer = new char[4096];
            int offset = 0;
            int length = 0;
            while ((length = input.read(buffer, offset, 4096)) > 0) {
                output.write(buffer, 0, length);
            }

            final String result = output.toString().replaceAll("\r\n", "\n");

            // Parse rows from the result
            rows = CsvUtil.fromCsvTable(result);

            // Construct column lists from rows
            constructColumnsFromRows();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Rows property getter method
     *
     * @return a YailList representing the parsed rows of the CSV file.
     */
    @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Returns a list of rows corresponding" +
            " to the CSV file's content.")
    public YailList Rows() {
        return rows;
    }


    /**
     * Columns property getter method
     *
     * @return a YailList representing the parsed columns of the CSV file.
     */
    @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Returns a list of columns corresponding" +
            " to the CSV file's content.")
    public YailList Columns() {
        return columns;
    }

    /**
     * Column names property getter method.
     * The intended use case of the method is for CSV files which contain
     * the column names in the first row.
     *
     * @return  a YailList containing the elements of the first row.
     */
    @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Returns the elements of the first row" +
            " of the CSV contents.")
    public YailList ColumnNames() {
        return columnNames;
    }

    /**
     * Sets the source file to parse CSV from, and then parses the CSV
     * file asynchronously.
     * The results are stored in the Columns, Rows and ColumnNames properties.
     *
     * @param source  Source file name
     */
    @SimpleProperty(category = PropertyCategory.BEHAVIOR, description = "Indicates source file to load data from." +
            "Prefix the filename with / to read from a specific file on the SD card. " +
            "for instance /myFile.txt will read the file /sdcard/myFile.txt. To read " +
            "assets packaged with an application (also works for the Companion) start " +
            "the filename with // (two slashes). If a filename does not start with a " +
            "slash, it will be read from the applications private storage (for packaged " +
            "apps) and from /sdcard/AppInventor/data for the Companion.")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET,
        defaultValue = "")
    public void SourceFile(String source) {
        this.sourceFile = source;

        // Parse CSV after setting source
        parseCSVFromSource(sourceFile);
    }

    /**
     * Waits until CSV Parsing is done by blocking the calling thread.
     *
     * The method should be called asynchronously to prevent freezing of
     * the main thread.
     *
     * TODO: This could return a value instead to prevent some async ordering issues.
     */
    public void waitUntilReadingDone() {
        try {
            // Submit an empty runnable, and wait for it
            // to finish, which effectively waits for the
            // tasks in the Single Threaded Runner queue
            // to finish.
            threadRunner.submit(new Runnable() {
                @Override
                public void run() {

                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the specified column's elements as a YailList.
     *
     * @param column  name of column
     * @return YailList of elements in the column
     */
    public YailList getColumn(String column) {
        // Get the index of the column (first row - column names)
        // 1 is subtracted from the index since YailList indexOf
        // returns an index that is 1-based.
        int index = columnNames.indexOf(column) - 1;

        // Column not found
        if (index < 0) {
            return null;
        }

        return (YailList)columns.getObject(index);
    }

    /**
     * Instantiates the columns list after CSV parsing to rows
     * has been processed.
     */
    private void constructColumnsFromRows() {
        // Store columns separately (first row indicates column names)
        columnNames = (YailList)rows.getObject(0);

        // Get the size of the row. The row size
        // indicates the number of columns.
        int rowSize = columnNames.size();

        // Construct each column separately, and add it
        // to the resulting list.
        ArrayList<YailList> columnList = new ArrayList<YailList>();

        for (int i = 0; i < rowSize; ++i) {
            columnList.add(getColumn(i));
        }

        columns = YailList.makeList(columnList);
    }

    /**
     * Constructs and returns a column from the rows, given
     * the index of the needed column.
     *
     * @param index  the index of the column to construct
     * @return  YailList column representation of the specified index
     */
    private YailList getColumn(int index) {
        List<String> entries = new ArrayList<String>();

        for (int i = 0; i < rows.size(); ++i) {
            // Get the i-th row
            YailList row = (YailList) rows.getObject(i); // Safe cast

            // index-th entry in the row is the required column value
            entries.add((row.getString(index)));
        }

        return YailList.makeList(entries);
    }
}
