//
//  MainViewController.h
//  MorseKeyboard
//
//  Created by David Patierno on 2/21/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import <MessageUI/MessageUI.h>
#import <UIKit/UIKit.h>

#import "Morse.h"

// Displays a text view and a button. Communicates with Morse to handle user
// input and display translated output.
@interface MainViewController : UIViewController
    <MFMailComposeViewControllerDelegate,
     MFMessageComposeViewControllerDelegate,
     MorseDelegate,
     UIActionSheetDelegate>
@property(nonatomic, retain) Morse* morse;
@property(nonatomic, retain) UITextView* textView;
@property(nonatomic, retain) UIButton* singleButton;

- (void)sendEmail;
- (void)sendSMS;
@end
