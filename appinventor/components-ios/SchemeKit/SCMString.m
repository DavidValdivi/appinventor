// -*- mode: objective-c; c-basic-offset: 2; -*-
// Copyright © 2020 Massachusetts Institute of Technology, All rights reserved.

#import "SCMString-Private.h"
#import "SCMInterpreter-Private.h"

@implementation SCMString {
  /// TODO: Rewrite to use pic_value as the ground truth
  NSString *_store;
}

+ (instancetype)stringFromPicString:(pic_value)string inInterpreter:(SCMInterpreter *)interpreter {
  return [[self alloc] initWithPicString:string fromInterpreter:interpreter];
}

+ (instancetype)stringWithString:(NSString *)string inInterpreter:(SCMInterpreter *)interpreter {
  return [[self alloc] initWithString:string inInterpreter:interpreter];
}

- (instancetype)initWithString:(NSString *)string inInterpreter:(SCMInterpreter *)interpreter {
  if (self = [super init]) {
    _store = [string copy];
    interpreter_ = interpreter;
    value_ = pic_cstr_value(interpreter.state, [string cStringUsingEncoding:NSUTF8StringEncoding]);
  }
  return self;
}

- (instancetype)initWithPicString:(pic_value)str fromInterpreter:(SCMInterpreter *)interpreter {
  const char *content = pic_str(interpreter.state, str);
  if (self = [super init]) {
    _store = [NSString stringWithUTF8String:content];
    interpreter_ = interpreter;
    value_ = str;
  }
  return self;
}

/// MARK: NSString Implementation

- (NSUInteger)length {
  return _store.length;
}

- (unichar)characterAtIndex:(NSUInteger)index {
  return [_store characterAtIndex:index];
}

/// MARK: SCMValue Implementation

@synthesize value = value_;

- (BOOL)isNil {
  return NO;
}

- (BOOL)isBool {
  return NO;
}

- (BOOL)isNumber {
  return NO;
}

- (BOOL)isString {
  return YES;
}

- (BOOL)isList {
  return NO;
}

- (BOOL)isDictionary {
  return NO;
}

- (BOOL)isCons {
  return NO;
}

- (BOOL)isExact {
  return NO;
}

- (BOOL)isSymbol {
  return NO;
}

- (BOOL)isComponent {
  return NO;
}

- (BOOL)isPicEqual:(pic_value)other {
  if (pic_str_p(interpreter_.state, other)) {
    pic_str_cmp(interpreter_.state, value_, other);
  }
  return NO;
}

/// MARK: SCMObject Implementation

@synthesize interpreter = interpreter_;

- (nonnull instancetype)initWithInterpreter:(nonnull SCMInterpreter *)interpreter {
  if (self = [super init]) {
    interpreter_ = interpreter;
    value_ = pic_str_value(interpreter.state, "", 0);
  }
  return self;
}

/// MARK: NSCopying Implementation

- (nonnull id)copyWithZone:(nullable NSZone *)zone {
  return [super copyWithZone:zone];
}

- (nonnull id)mutableCopyWithZone:(nullable NSZone *)zone {
  return [super mutableCopyWithZone:zone];
}

/// MARK: NSCoding Implementation

- (void)encodeWithCoder:(nonnull NSCoder *)coder {
  [super encodeWithCoder:coder];
}

@end
