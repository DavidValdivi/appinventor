//
//  ReplForm.swift
//  AIComponentKit
//
//  Created by Evan Patton on 9/17/16.
//  Copyright © 2016 MIT Center for Mobile Learning. All rights reserved.
//

import Foundation
import SchemeKit

public class ReplForm: Form {
  private var _httpdServer: AppInvHTTPD?

  public override func dispatchEvent(of component: Component, called componentName: String, with eventName: String, having args: [AnyObject]) -> Bool {
    NSLog("Delegating dispatch to YAIL")
    if let interpreter = _httpdServer?.interpreter {
      let result = interpreter.invokeMethod("dispatchEvent", withArgArray: [component, componentName, eventName, args])
      if (interpreter.exception != nil) {
        NSLog("Exception occurred in YAIL: \((interpreter.exception?.name.rawValue)!) (irritants: \((interpreter.exception)!))");
        return false
      }
      NSLog("dispatchEvent result = \(result)")
      if (result is Bool) {
        return result as! Bool
      } else if (result is NSNumber) {
        return (result as! NSNumber).boolValue
      } else {
        return false
      }
    } else {
      NSLog("No HTTPD server running?")
    }
    return false
  }
  
  public func startHTTPD(secure: Bool) {
    if _httpdServer == nil {
      _httpdServer = AppInvHTTPD(port: 8001, rootDirectory: "", secure: secure, for: self)
    }
  }
  
  public var interpreter: SCMInterpreter? {
    get {
      return _httpdServer?.interpreter
    }
  }
}
