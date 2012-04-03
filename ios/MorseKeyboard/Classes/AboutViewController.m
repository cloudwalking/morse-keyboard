//
//  AboutViewController.m
//  MorseKeyboard
//
//  Created by David Patierno on 4/3/12.
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

#import "AboutViewController.h"

#import "UI.h"

@implementation AboutViewController

- (id)init {
  if ((self = [super init])) {
    self.title = @"About";
    self.modalPresentationStyle = UIModalPresentationFormSheet;
    self.modalTransitionStyle = UIModalTransitionStyleFlipHorizontal;
  }
  return self;
}

#pragma mark - View lifecycle

- (void)viewDidLoad {
  [super viewDidLoad];

  self.view.backgroundColor = [UIColor lightGrayColor];
  self.navigationController.navigationBar.tintColor = [UIColor blackColor];

  self.navigationItem.rightBarButtonItem =
      [UI doneBarButtonItemWithTitle:@"Done"
                              target:self
                              action:@selector(done)];

  NSString* license =
    @"\n"
    @"April Fools' from the Gmail team!"
    @"\n\n"
    @"This program is Copyright 2012 Google Inc. All Rights Reserved."
    @"\n\n"
    @"Licensed under the Apache License, Version 2.0 (the \"License\"); "
    @"You may not use this file except in compliance with the License. "
    @"You may obtain a copy of the License at" 
    @"\n\n"
    @"  http://www.apache.org/licenses/LICENSE-2.0"
    @"\n\n"
    @"Unless required by applicable law or agreed to in writing, software "
    @"distributed under the License is distributed on an \"AS IS\" BASIS, "
    @"WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. "
    @"See the License for the specific language governing permissions and "
    @"limitations under the License."
    @"";

  CGRect frame = CGRectMake(0.0,
                            0.0,
                            self.view.frame.size.width,
                            self.view.frame.size.height);
  UITextView* textView = [[[UITextView alloc] initWithFrame:frame] autorelease];
  textView.autoresizingMask = (UIViewAutoresizingFlexibleWidth |
                               UIViewAutoresizingFlexibleHeight);
  textView.editable = NO;
  textView.text = license;  
  [self.view addSubview:textView];
}

- (void)done {
  [self dismissModalViewControllerAnimated:YES];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:
    (UIInterfaceOrientation)toInterfaceOrientation {
  if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad)
    return YES;
  return toInterfaceOrientation == UIInterfaceOrientationPortrait;
}

@end
