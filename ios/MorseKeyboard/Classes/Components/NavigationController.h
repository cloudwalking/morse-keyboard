//
//  NavigationController.h
//  MorseKeyboard
//
//  Created by David Patierno on 1/31/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  You may not use this file except in compliance with the License.
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

#import <UIKit/UIKit.h>

@interface NavigationController : UINavigationController
    <UINavigationControllerDelegate>

// Appearance customizations. +customizeNavigationController: is a simple
// wrapper around both +customizeNavigationBar: and +customizeToolbar:.
+ (void)customizeNavigationController:
    (UINavigationController*)navigationController;
+ (void)customizeNavigationBar:(UINavigationBar*)navigationBar;
+ (void)customizeToolbar:(UIToolbar*)toolbar;

@end
