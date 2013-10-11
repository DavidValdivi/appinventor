// Copyright 2011-2013 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/appinventor-sources/master/mitlicense.txt

package com.google.appinventor.blocklyeditor;
import java.io.IOException;
import com.google.appinventor.blocklyeditor.BlocklyTestUtils;
import com.google.appinventor.common.testutils.TestUtils;
import junit.framework.TestCase;

/**
 * Tests the App Inventor Blockly blocks evaluation of various YAIL code.
 *
 * TODO(andrew.f.mckinney): More tests needed!
 *
 * @author andrew.f.mckinney@gmail.com (Andrew.F.McKinney)
 */


public class BlocklyEvalTest extends TestCase {

  public static final String testpath = TestUtils.APP_INVENTOR_ROOT_DIR + "/blocklyeditor";

  public void testBackgroundColor() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/backgroundColorTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testMoleMash() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/moleMashTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testPaintPot() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/paintPotTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testHelloPurr() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/helloPurrTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testMakeQuiz() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/makeQuizTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testPictureCycle() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/pictureCycleTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testSensor() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/sensorTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testClock() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/clockTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testCamcorder() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/camcorderTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testCopyCat() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/copyCatTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

  public void testProductLookup() throws Exception {

    String[] params =
      { "phantomjs",
        testpath + "/tests/com/google/appinventor/blocklyeditor/productLookupTest.js" };
    String result = "";

    try {
      result = CodeBlocksProcessHelper.exec(params, true).trim();
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("true", result.toString());
  }

    // The following tests are unit tests for the Yail generation of individual blocks.
    //  See the comments in
    // /tests/com/google/appinventor/generators_unit/listsCreateWithTest.js
    // for an explanation of how to create these tests

  public void testListsCreateList() throws Exception {
    String result = BlocklyTestUtils.generatorTest(
        testpath + "/tests/com/google/appinventor/generators_unit/listsCreateWithTest.js");
    assertEquals("true", result.toString());
  }

  public void testListsAddItems() throws Exception {
    String result = BlocklyTestUtils.generatorTest(
        testpath + "/tests/com/google/appinventor/generators_unit/listsAddItemsTest.js");
    assertEquals("true", result.toString());
  }

  public void testListsSelectItem() throws Exception {
    String result = BlocklyTestUtils.generatorTest(
        testpath + "/tests/com/google/appinventor/generators_unit/listsSelectItemTest.js");
    assertEquals("true", result.toString());
  }

    // add more unit tests here


}

