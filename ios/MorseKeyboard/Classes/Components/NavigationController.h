//
//  NavigationController.h
//  MorseKeyboard
//
//  Created by David Patierno on 1/31/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
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
