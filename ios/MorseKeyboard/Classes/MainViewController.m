//
//  MainViewController.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/21/12.
//  Copyright (c) 2012 Google Inc. All rights reserved.
//

#import "MainViewController.h"

#import <QuartzCore/QuartzCore.h>

#import "UI.h"
#import "MorseDemo.h"

typedef enum {
  SheetTagSettings = 1,
  SheetTagMessage = 2
} SheetTag;

@implementation MainViewController

@synthesize morse = morse_;
@synthesize textView = textView_;
@synthesize singleButton = singleButton_;

- (id)init {
  if ((self = [super init])) {
    self.title = @"Morse Keyboard";
    morse_ = [[Morse alloc] initWithDelegate:self];
  }
  return self;
}

- (void)dealloc {
  [morse_ release];
  [textView_ release];
  [singleButton_ release];
  [super dealloc];
}

- (void)viewDidLoad {
  [super viewDidLoad];
  self.view.backgroundColor =
      [UIColor colorWithPatternImage:[UIImage imageNamed:@"paper.png"]];
  self.navigationController.navigationBar.tintColor = [UIColor blackColor];

  // Navigation item buttons.
  self.navigationItem.leftBarButtonItem =
      [UI settingsBarButtonItemWithTarget:self
                                     action:@selector(showSettings)];
  self.navigationItem.rightBarButtonItem =
      [UI barButtonItemWithImage:[UIImage imageNamed:@"send.png"]
                            target:self
                            action:@selector(sendMessage)];
  [UI enableBarButtonItem:self.navigationItem.rightBarButtonItem enabled:NO];

  // Giant text field.
  CGRect textViewFrame = CGRectMake(0.0,
                                    0.0,
                                    self.view.frame.size.width,
                                    roundf(self.view.frame.size.height / 2.0));
  textView_ = [[UITextView alloc] initWithFrame:textViewFrame];
  textView_.text = @"";
  textView_.editable = NO;
  textView_.backgroundColor = [UIColor clearColor];
  textView_.font = [UIFont fontWithName:@"Courier-Bold" size:28.0];
  textView_.textColor = [UIColor blackColor];

  textView_.layer.shadowColor = [UIColor whiteColor].CGColor;
  textView_.layer.shadowOffset = CGSizeMake(0.0, 1.0);
  textView_.layer.shadowOpacity = 1.0;
  textView_.layer.shadowRadius = 0.0;
  textView_.autoresizingMask = (UIViewAutoresizingFlexibleHeight |
                                 UIViewAutoresizingFlexibleWidth |
                                 UIViewAutoresizingFlexibleBottomMargin);
  [self.view addSubview:textView_];

  // Giant button.
  CGRect singleButtonFrame =
      CGRectMake(0.0,
                 textViewFrame.size.height,
                 self.view.frame.size.width,
                 self.view.frame.size.height - textViewFrame.size.height);
  singleButton_ = [[UIButton alloc] initWithFrame:singleButtonFrame];
  UIImage* image = [UIImage imageNamed:@"button.png"];
  image = [image stretchableImageWithLeftCapWidth:25 topCapHeight:22];
  [singleButton_ setBackgroundImage:image forState:UIControlStateNormal];
  singleButton_.autoresizingMask = (UIViewAutoresizingFlexibleHeight |
                                 UIViewAutoresizingFlexibleWidth |
                                 UIViewAutoresizingFlexibleTopMargin);
  [singleButton_ addTarget:self
                    action:@selector(tapSingleButtonBegin)
          forControlEvents:UIControlEventTouchDown];
  [singleButton_ addTarget:self
                    action:@selector(tapSingleButtonEnd)
          forControlEvents:UIControlEventTouchUpInside];
  [self.view addSubview:singleButton_];
}

- (void)viewDidUnload {
  [super viewDidUnload];
  self.textView = nil;
  self.singleButton = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:
    (UIInterfaceOrientation)toInterfaceOrientation {
  if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad)
    return YES;
  return toInterfaceOrientation == UIInterfaceOrientationPortrait;
}

#pragma mark -
#pragma mark Navigation bar button events

- (void)clearText {
  [morse_ reset];

  // Disable the clear button.
  [UI enableBarButtonItem:self.navigationItem.leftBarButtonItem enabled:NO];
}

- (void)showSettings {
  NSString* title = @"Enable Predictive Mode";
  if ([morse_ isKindOfClass:[MorseDemo class]]) {
    title = @"Disable Predictive Mode";
  }

  UIActionSheet* sheet =
      [[[UIActionSheet alloc] initWithTitle:nil
                                  delegate:self
                         cancelButtonTitle:@"Cancel"
                    destructiveButtonTitle:@"Clear"
                         otherButtonTitles:title,
                                           nil]
          autorelease];
  sheet.tag = SheetTagSettings;
  [sheet showInView:self.view];
}

- (void)sendMessage {
  if (![MFMessageComposeViewController canSendText]) {
    [self sendEmail];
    return;
  }

  UIActionSheet* sheet =
      [[[UIActionSheet alloc] initWithTitle:@"Send message as..."
                                   delegate:self
                          cancelButtonTitle:@"Cancel"
                     destructiveButtonTitle:nil
                          otherButtonTitles:@"Email",
                                            @"Text Message",
                                            nil]
           autorelease];
  sheet.tag = SheetTagMessage;
  [sheet showInView:self.view];
}

- (void)sendEmail {
  NSString* text = [NSString stringWithFormat:@"%@\n\n%@",
                       [morse_ signalsFromText:textView_.text],
                       textView_.text];

  MFMailComposeViewController* vc =
      [[[MFMailComposeViewController alloc] init] autorelease];
  vc.modalPresentationStyle = UIModalPresentationFormSheet;
  [vc setMailComposeDelegate:self];
  [vc setMessageBody:text isHTML:NO];
  [self presentModalViewController:vc animated:YES];
}

- (void)sendSMS {
  NSString* text = [NSString stringWithFormat:@"%@\n\n%@",
                       [morse_ signalsFromText:textView_.text],
                       textView_.text];

  MFMessageComposeViewController* vc =
      [[[MFMessageComposeViewController alloc] init] autorelease];
  vc.modalPresentationStyle = UIModalPresentationFormSheet;
  [vc setMessageComposeDelegate:self];
  [vc setBody:text];
  [self presentModalViewController:vc animated:YES];
}

#pragma mark -
#pragma mark Action sheet delegate

- (void)actionSheet:(UIActionSheet *)actionSheet
    clickedButtonAtIndex:(NSInteger)buttonIndex {
  if (buttonIndex == actionSheet.numberOfButtons - 1) {
    return;
  }

  if (actionSheet.tag == SheetTagSettings) {
    if (buttonIndex == 1) {
      Class MorseClass = [MorseDemo class];

      if ([morse_ isKindOfClass:[MorseDemo class]]) {
        MorseClass = [Morse class];
      }

      self.morse = [[[MorseClass alloc] initWithDelegate:self] autorelease];
    }

    [morse_ reset];
  } else {
    if (buttonIndex == 0) {
      [self sendEmail];
    } else {
      [self sendSMS];
    }
  }
}

#pragma mark -
#pragma mark Tapping button events

- (void)tapSingleButtonBegin {
  [morse_ tapBegin];
}

- (void)tapSingleButtonEnd {
  [morse_ tapEnd];
}

#pragma mark -
#pragma mark MessageUI delegate

- (void)mailComposeController:(MFMailComposeViewController*)controller
          didFinishWithResult:(MFMailComposeResult)result
                        error:(NSError*)error {
  [self dismissModalViewControllerAnimated:YES];

  if (result == MFMailComposeResultSaved || result == MFMailComposeResultSent) {
    [morse_ reset];
  }
}

- (void)messageComposeViewController:(MFMessageComposeViewController*)controller
                 didFinishWithResult:(MessageComposeResult)result {
  [self dismissModalViewControllerAnimated:YES];

  if (result == MessageComposeResultSent) {
    [morse_ reset];
  }
}

#pragma mark -
#pragma mark Morse delegate

- (void)morse:(Morse*)morse updatedResultText:(NSString*)resultText {
  textView_.text = resultText;

  // Enable the clear button.
  [UI enableBarButtonItem:self.navigationItem.rightBarButtonItem
                    enabled:resultText.length > 0];

  // Scroll to the bottom.
  [textView_ scrollRangeToVisible:NSMakeRange(resultText.length, 0)];
}

- (void)morse:(Morse*)morse updatedPendingSignals:(NSString *)pendingSignals {
  self.title = [pendingSignals length] ? pendingSignals : @"Morse Keyboard";
}

@end
