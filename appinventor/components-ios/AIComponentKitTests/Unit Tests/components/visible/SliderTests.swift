// -*- mode: swift; swift-mode:basic-offset: 2; -*-
// Copyright © 2016-2018 Massachusetts Institute of Technology, All rights reserved.

import Foundation
import XCTest
@testable import AIComponentKit

class SliderTests: XCTestCase {
  var slider: Slider = Slider(Form())

  override func setUp() {
    slider = Slider(Form())
  }

  func testDefaultSliderPosition() {
    baseTest()
  }

  func testSettingAndGettingThumbPosition() {
    testPosition(kSliderMaxValue - 10)
    testPosition(kSliderMinValue + 10)
  }

  func testSettingAndGettingThumbPositionWithOverflow() {
    testPosition(kSliderMinValue - 10)
    testPosition(kSliderMaxValue + 10)
  }

  func testUpdateMaxValueAndThumbPosition() {
    baseTest(maxDiff:  10)
    baseTest(maxDiff: -10)
  }

  func testUpdateMinValueAndThumbPosition() {
    baseTest(minDiff: -10)
    baseTest(minDiff:  10)
  }

  func testUpdateMaxMinValueAndThumbPosition() {
    baseTest(minDiff: -10, maxDiff: 10)
    baseTest(minDiff: 10, maxDiff: 10)
    baseTest(minDiff: 10, maxDiff: -10)
    baseTest(minDiff: -10, maxDiff: -10)
  }

  func testMinValueOverflow() {
    slider.MinValue = kSliderMaxValue + 10
    XCTAssertEqual(slider.MaxValue, kSliderMaxValue + 10, "Slider does not update MaxValue when MinValue is larger than MaxValue")
  }

  func testMaxValueOverflow() {
    slider.MaxValue = kSliderMinValue - 10
    XCTAssertEqual(slider.MinValue, kSliderMinValue - 10, "Slider does not update MinValue when MinValue is larger than MinValue")
  }

  fileprivate func baseTest(minDiff: Float32? = 0, maxDiff: Float32? = 0) {
    let min = kSliderMinValue + (minDiff ?? 0)
    let max = kSliderMaxValue + (maxDiff ?? 0)
    slider.MinValue = min
    slider.MaxValue = max
    let expected = (max - min) / 2 + min

    XCTAssertEqual(slider.ThumbPosition, expected, "ThumbPosition \(slider.ThumbPosition) does not match the expected value \(expected)")
  }

  fileprivate func testPosition(_ position: Float32) {
    var expected = position
    if position > slider.MaxValue  {
      expected = slider.MaxValue
    } else if position < slider.MinValue {
      expected = slider.MinValue
    }
    slider.ThumbPosition = position
    XCTAssertEqual(slider.ThumbPosition, expected, "Slider ThumbPosition does not match expected value")

  }
}
