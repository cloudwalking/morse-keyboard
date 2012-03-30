//
//  UI.h
//  MorseKeyboard
//
//  Created by David Patierno on 2/3/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
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
