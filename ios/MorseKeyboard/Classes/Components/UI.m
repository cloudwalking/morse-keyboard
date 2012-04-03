//
//  UI.m
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

#import "UI.h"

static CGFloat const kButtonImageCapSize = 21.0;

static CGFloat const kBackBarButtonMarginX = -5.0;
static CGFloat const kBackBarButtonTitleOffset = 18.0;

static CGFloat const kBarButtonFontSize = 12.0;
static CGFloat const kBarButtonHeight = 40.0;
static CGFloat const kBarButtonImagePadding = 7.0;
static CGFloat const kBarButtonMarginX = -3.0;
static CGFloat const kBarButtonTextPadding = 13.0;

static CGFloat const kButtonFontSize = 16.0;
static CGFloat const kButtonHeight = 50.0;
static CGFloat const kButtonPadding = 20.0;
static CGFloat const kButtonTitleColor = 0.65;

static CGFloat const kBarButtonDisabledAlpha = 0.4;
static CGFloat const kMaxBarButtonWidth = 120.0;

static CGFloat const kSpinnerSize = 21.0;

@implementation UI

+ (UIBarButtonItem*)backBarButtonItemWithTitle:(NSString*)title
                                        target:(id)target
                                        action:(SEL)action {
  UIView* view = [[[UIView alloc] initWithFrame:CGRectZero] autorelease];
  UIImage* image_normal = [UIImage imageNamed:@"back_normal.png"];
  UIImage* image_pressed = [UIImage imageNamed:@"back_pressed.png"];
  image_normal =
      [image_normal stretchableImageWithLeftCapWidth:kButtonImageCapSize
                                        topCapHeight:kButtonImageCapSize];
  image_pressed =
      [image_pressed stretchableImageWithLeftCapWidth:kButtonImageCapSize
                                         topCapHeight:kButtonImageCapSize];

  // Create the custom button with stretchable background image.
  UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
  [button setContentMode:UIViewContentModeScaleToFill];
  [button setBackgroundImage:image_normal forState:UIControlStateNormal];
  [button setBackgroundImage:image_pressed forState:UIControlStateHighlighted];
  [button.titleLabel setFont:[UIFont boldSystemFontOfSize:kBarButtonFontSize]];
  [button.titleLabel setShadowOffset:CGSizeMake(0.0, -1.0)];
  [button setTitleColor:[UIColor colorWithWhite:kButtonTitleColor alpha:1.0]
               forState:UIControlStateNormal];
  [button setTitleShadowColor:[UIColor blackColor]
                     forState:UIControlStateNormal];
  [button setContentHorizontalAlignment:
      UIControlContentHorizontalAlignmentLeft];
  [button setTitle:title forState:UIControlStateNormal];
  [button setTitleEdgeInsets:UIEdgeInsetsMake(0.0,
                                              kBackBarButtonTitleOffset,
                                              0.0,
                                              0.0)];
  [button addTarget:target
             action:action
   forControlEvents:UIControlEventTouchUpInside];

  // Adjust everything around for proper margins, etc.
  [UI adjustSpacingForBackButton:button];
  [UI adjustContainerView:view forButton:button];
  [view addSubview:button];

  return [[[UIBarButtonItem alloc] initWithCustomView:view] autorelease];
}

+ (UIBarButtonItem*)barButtonItemWithTitle:(NSString*)title
                                    target:(id)target
                                    action:(SEL)action {
  UIView* view = [[[UIView alloc] initWithFrame:CGRectZero] autorelease];
  UIImage* image_normal = [UIImage imageNamed:@"standard_normal.png"];
  UIImage* image_pressed = [UIImage imageNamed:@"standard_pressed.png"];
  image_normal =
      [image_normal stretchableImageWithLeftCapWidth:kButtonImageCapSize
                                        topCapHeight:kButtonImageCapSize];
  image_pressed =
      [image_pressed stretchableImageWithLeftCapWidth:kButtonImageCapSize
                                         topCapHeight:kButtonImageCapSize];

  // Create the custom button with stretchable background image.
  UIButton* button = [UIButton buttonWithType:UIButtonTypeCustom];
  [button setContentMode:UIViewContentModeScaleToFill];
  [button setBackgroundImage:image_normal forState:UIControlStateNormal];
  [button setBackgroundImage:image_pressed forState:UIControlStateHighlighted];
  [button.titleLabel setFont:[UIFont boldSystemFontOfSize:kBarButtonFontSize]];
  [button.titleLabel setShadowOffset:CGSizeMake(0.0, -1.0)];
  [button setTitleColor:[UIColor colorWithWhite:kButtonTitleColor alpha:1.0]
               forState:UIControlStateNormal];
  [button setTitleShadowColor:[UIColor blackColor]
                     forState:UIControlStateNormal];
  [button setTitle:title forState:UIControlStateNormal];
  [button addTarget:target
             action:action
   forControlEvents:UIControlEventTouchUpInside];

  // Adjust everything around for proper margins, etc.
  [UI adjustSpacingForButton:button];
  [UI adjustContainerView:view forButton:button];
  [view addSubview:button];

  return [[[UIBarButtonItem alloc] initWithCustomView:view] autorelease];
}

+ (UIBarButtonItem*)barButtonItemWithImage:(UIImage*)image
                                    target:(id)target
                                    action:(SEL)action {
  UIBarButtonItem* item = [self barButtonItemWithTitle:nil
                                                target:target
                                                action:action];
  UIView* view = item.customView;
  UIButton* button = [view.subviews objectAtIndex:0];
  [button setImage:image forState:UIControlStateNormal];

  // Adjust everything around for proper margins, etc.
  [button sizeToFit];

  // Buttons with images have smaller margins.
  CGRect frame = button.frame;
  frame.size.width += kBarButtonImagePadding * 2.0;
  button.frame = frame;

  [UI adjustContainerView:view forButton:button];

  return item;
}

+ (UIBarButtonItem*)settingsBarButtonItemWithTarget:(id)target
                                             action:(SEL)action {
  UIImage* image = [UIImage imageNamed:@"settings.png"];
  return [UI barButtonItemWithImage:image target:target action:action];
}

+ (UIBarButtonItem*)doneBarButtonItemWithTitle:(NSString*)title
                                        target:(id)target
                                        action:(SEL)action {
  UIBarButtonItem* item = [self barButtonItemWithTitle:title
                                                target:target
                                                action:action];
  UIButton* button = [item.customView.subviews objectAtIndex:0];
  [button setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
  return item;
}

+ (void)enableBarButtonItem:(UIBarButtonItem*)item enabled:(BOOL)enabled {
  UIButton* button = [item.customView.subviews objectAtIndex:0];
  button.alpha = enabled ? 1.0 : kBarButtonDisabledAlpha;
  button.enabled = enabled;
}

+ (void)adjustSpacingForBackButton:(UIButton*)button {
  UILabel* label = button.titleLabel;
  [label sizeToFit];
  [button setFrame:CGRectMake(kBackBarButtonMarginX,
                              0.0,
                              fmin(kMaxBarButtonWidth,
                                   label.frame.size.width +
                                   kBackBarButtonTitleOffset +
                                   kBarButtonTextPadding),
                              kBarButtonHeight)];
}

+ (void)adjustSpacingForButton:(UIButton*)button {
  UILabel* label = button.titleLabel;
  [label sizeToFit];
  [button setFrame:
      CGRectMake(kBarButtonMarginX,
                 0.0,
                 label.frame.size.width + kBarButtonTextPadding * 2.0,
                 kBarButtonHeight)];
}

+ (void)adjustContainerView:(UIView*)view forButton:(UIButton*)button {
  CGRect viewFrame = button.frame;
  viewFrame.origin.x = 0.0;
  viewFrame.origin.y = 0.0;
  viewFrame.size.width += kBarButtonMarginX * 2.0;
  view.frame = viewFrame;
}

@end
