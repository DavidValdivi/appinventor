// -*- mode: swift; swift-mode:basic-offset: 2; -*-
// Copyright © 2020 Massachusetts Institute of Technology, All rights reserved.

import Foundation

extension Array {
  /**
   * Converts the contents of this array into a new `[String]`. If the receiver is already
   * a `[String]`, this will make a shallow copy.
   *
   * - Returns: a new `Array` containing `String` representations of the receiver's elements
   */
  public func toStringArray() -> [String] {
    var copy = [String]()
    var first = true
    for el in self {
      if first && el is SCMSymbol {  // skip *list* header
        first = false
      } else if let x = el as? String {
        copy.append(x)
      } else {
        copy.append(String(describing: el))
      }
    }
    return copy
  }
}
