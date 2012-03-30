//
//  AppDelegate.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/21/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
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
