//
//  NavigationController.m
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

#import "NavigationController.h"

#import "UI.h"

static const NSInteger kCustomImageTag = 6183746;

@implementation NavigationController

#pragma mark -
#pragma mark Appearance customizations

+ (void)customizeNavigationController:
    (UINavigationController*)navigationController {
  UINavigationBar* navigationBar = [navigationController navigationBar];
  UIToolbar* toolbar = [navigationController toolbar];
  [NavigationController customizeNavigationBar:navigationBar];
  [NavigationController customizeToolbar:toolbar];
}

+ (void)customizeNavigationBar:(UINavigationBar*)navigationBar {
  UIImage* image = [UIImage imageNamed:@"navbar.png"];
  SEL selector = @selector(setBackgroundImage:forBarMetrics:);
  if ([navigationBar respondsToSelector:selector]) {
    [navigationBar setBackgroundImage:image
                        forBarMetrics:UIBarMetricsDefault];
  } else {
    UIImageView* imageView =
        (UIImageView*)[navigationBar viewWithTag:kCustomImageTag];
    if (imageView == nil) {
      imageView = [[[UIImageView alloc] initWithImage:image] autorelease];
      imageView.frame = CGRectMake(0.0,
                                   0.0,
                                   navigationBar.frame.size.width,
                                   navigationBar.frame.size.height);
      imageView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
      imageView.contentMode = UIViewContentModeScaleToFill;
      imageView.tag = kCustomImageTag;
    }
    [navigationBar insertSubview:imageView atIndex:0];
    navigationBar.tintColor = [UIColor blackColor];
  }
}

+ (void)customizeToolbar:(UIToolbar*)toolbar {
  UIImage* image = [UIImage imageNamed:@"toolbar.png"];
  SEL selector = @selector(setBackgroundImage:forToolbarPosition:barMetrics:);
  if ([toolbar respondsToSelector:selector]) {
    [toolbar setBackgroundImage:image
             forToolbarPosition:UIToolbarPositionAny
                     barMetrics:UIBarMetricsDefault];
  } else {
    UIImageView* imageView =
        (UIImageView*)[toolbar viewWithTag:kCustomImageTag];
    if (imageView == nil) {
      imageView = [[[UIImageView alloc] initWithImage:image] autorelease];
      imageView.tag = kCustomImageTag;
    }
    [toolbar insertSubview:imageView atIndex:0];
    toolbar.tintColor = [UIColor blackColor];
  }
}

#pragma mark -
#pragma mark Object

- (id)init {
  if ((self = [super init])) {
    super.delegate = self;
    [NavigationController customizeNavigationController:self];
  }
  return self;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:
   (UIInterfaceOrientation)toInterfaceOrientation {
  return [self.visibleViewController
             shouldAutorotateToInterfaceOrientation:toInterfaceOrientation];
}

#pragma mark -
#pragma mark UINavigationController delegate

// Intercept stack events to inject our custom back button.
- (void)navigationController:(UINavigationController*)navigationController
      willShowViewController:(UIViewController*)viewController
                    animated:(BOOL)animated {
  if ([self.viewControllers count] > 1 &&
      !viewController.navigationItem.hidesBackButton) {
    NSUInteger parentIndex = [self.viewControllers count] - 2;
    UIViewController* parent = [self.viewControllers objectAtIndex:parentIndex];
    NSString* title = parent.title ? parent.title : @"Back";
    UIBarButtonItem* backButton =
        [UI backBarButtonItemWithTitle:title
                                  target:self
                                  action:@selector(popViewControllerAnimated)];
    viewController.navigationItem.leftBarButtonItem = backButton;
  }
}

- (void)navigationController:(UINavigationController *)navigationController
       didShowViewController:(UIViewController *)viewController
                    animated:(BOOL)animated {
  [NavigationController customizeNavigationBar:self.navigationBar];
}

- (void)popViewControllerAnimated {
  [self popViewControllerAnimated:YES];
}

@end
