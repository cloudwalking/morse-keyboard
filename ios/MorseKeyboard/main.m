//
//  main.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/21/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "AppDelegate.h"

int main(int argc, char *argv[]) {
  NSAutoreleasePool* pool = [[NSAutoreleasePool alloc] init];
  int retVal = UIApplicationMain(argc,
                                 argv,
                                 nil,
                                 NSStringFromClass([AppDelegate class]));
  [pool release];
  return retVal;
}
