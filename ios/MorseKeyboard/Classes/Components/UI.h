//
//  UI.h
//  MorseKeyboard
//
//  Created by David Patierno on 2/3/12.
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

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface UI : NSObject
// Custom styled bar button items for navigation bars.
+ (UIBarButtonItem*)backBarButtonItemWithTitle:(NSString*)title
                                        target:(id)target
                                        action:(SEL)action;

+ (UIBarButtonItem*)barButtonItemWithTitle:(NSString*)title
                                    target:(id)target
                                    action:(SEL)action;

+ (UIBarButtonItem*)barButtonItemWithImage:(UIImage*)image
                                    target:(id)target
                                    action:(SEL)action;

+ (UIBarButtonItem*)settingsBarButtonItemWithTarget:(id)target
                                             action:(SEL)action;

+ (UIBarButtonItem*)doneBarButtonItemWithTitle:(NSString*)title
                                        target:(id)target
                                        action:(SEL)action;

// Toggles opacity and enabled status on a bar button item.
+ (void)enableBarButtonItem:(UIBarButtonItem*)item enabled:(BOOL)enabled;

+ (void)adjustSpacingForBackButton:(UIButton*)button;
+ (void)adjustSpacingForButton:(UIButton*)button;
+ (void)adjustContainerView:(UIView*)view forButton:(UIButton*)button;

@end
