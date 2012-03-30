//
//  AppDelegate.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/21/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import "AppDelegate.h"

#import "NavigationController.h"
#import "MainViewController.h"

@implementation AppDelegate

@synthesize window = window_;

- (void)dealloc {
  [window_ release];
  [super dealloc];
}

- (BOOL)application:(UIApplication*)application
    didFinishLaunchingWithOptions:(NSDictionary*)launchOptions {
  MainViewController* vc = [[[MainViewController alloc] init] autorelease];
  NavigationController* nvc =
      [[[NavigationController alloc] initWithRootViewController:vc]
          autorelease];
  window_ = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
  window_.rootViewController = nvc;
  [window_ makeKeyAndVisible];
  return YES;
}

@end
