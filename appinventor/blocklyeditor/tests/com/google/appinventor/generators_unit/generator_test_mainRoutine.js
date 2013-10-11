// Copyright 2011-2013 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/appinventor-sources/master/mitlicense.txt

/**
 * Main function for creating usit tests for yail generators
 *
 * Author: Hal Abelson (hal@mit.edu)
 */

// This is the main function for execution of generator tests; it can be 'require'd in other tests
// by using exports to make it available there.
// This function should be called in a context in which the arguments array exists.

exports.execute =  function (){

  // Open the actual page and load all the JavaScript in it
  // if success is true, all went well
  page.open('blocklyeditor/src/demos/yail/yail_testing_index.html', function(status) {
    if (status !== 'success') {
      console.log('load of yail_testing_index.html unsuccessful');
      phantom.exit();
      return false;
    }

    var passed = page.evaluate(
        function(){
          var expected = arguments[0];
          var generator = arguments[1]();
          var blockName = arguments[2];
          var doesReturn = arguments[3];

          var generatedYail = "";
          var yailForBlock = generator.call(new Blockly.Block(Blockly.mainWorkspace, blockName));
            if (doesReturn) {
               if ((yailForBlock.length !== 2) || (yailForBlock[1] !== Blockly.Yail.ORDER_ATOMIC) ) {
                 return false;
                } else {
                generatedYail = yailForBlock[0];
               }
            } else {
              generatedYail = yailForBlock;
            }
          // Uncomment these for debugging "expected" in making new tests
          // console.log(generatedYail);
          // console.log(expected);
          return doesContain(generatedYail, expected);
          },
        expected,
        delayedGenerator,
        blockName,
        doesReturn);

    //This is the actual result of the test
    console.log(passed);

    //Exit the phantom process
    phantom.exit();
  });
};
