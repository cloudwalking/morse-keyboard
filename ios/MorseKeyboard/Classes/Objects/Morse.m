//
//  Morse.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/22/12.
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

#import "Morse.h"

static NSInteger const kDotLength = 100;
static NSInteger const kDashMultiplier = 3;
static NSInteger const kLetterMultiplier = 3;
static NSInteger const kWordMultiplier = 7;

@interface Morse ()
@property(nonatomic, retain) NSDate* beginDate;
@property(nonatomic, retain) NSTimer* letterTimer;
@property(nonatomic, retain) NSTimer* wordTimer;

- (void)invalidateLetterAndWordTimers;
- (void)beginLetterAndWordTimers;
- (NSString*)signalForBeginDate:(NSDate*)beginDate
                        endDate:(NSDate*)endDate;
- (NSString*)letterForSignals:(NSString*)signals;
@end

@implementation Morse

@synthesize delegate = delegate_;
@synthesize resultText = resultText_;
@synthesize pendingSignals = pendingSignals_;
@synthesize signalTable = signalTable_;
@synthesize letterTimer = letterTimer_;
@synthesize wordTimer = wordTimer_;
@synthesize beginDate = beginDate_;

#pragma mark -
#pragma mark Init and dealloc

- (id)initWithDelegate:(id<MorseDelegate>)delegate {
  if ((self = [super init])) {
    delegate_ = delegate;
    [self reset];

    self.signalTable =
        [NSDictionary dictionaryWithObjectsAndKeys:@"A", @"•—",
                                                   @"B", @"—•••",
                                                   @"C", @"—•—•",
                                                   @"D", @"—••",
                                                   @"E", @"•",
                                                   @"F", @"••—•",
                                                   @"G", @"——•",
                                                   @"H", @"••••",
                                                   @"I", @"••",
                                                   @"J", @"•———",
                                                   @"K", @"—•—",
                                                   @"L", @"•—••",
                                                   @"M", @"——",
                                                   @"N", @"—•",
                                                   @"O", @"———",
                                                   @"P", @"•——•",
                                                   @"Q", @"——•—",
                                                   @"R", @"•—•",
                                                   @"S", @"•••",
                                                   @"T", @"—",
                                                   @"U", @"••—",
                                                   @"V", @"•••—",
                                                   @"W", @"•——",
                                                   @"X", @"—••—",
                                                   @"Y", @"—•——",
                                                   @"1", @"•————",
                                                   @"2", @"••———",
                                                   @"3", @"•••——",
                                                   @"4", @"••••—",
                                                   @"5", @"•••••",
                                                   @"6", @"—••••",
                                                   @"7", @"——•••",
                                                   @"8", @"———••",
                                                   @"9", @"————•",
                                                   @"0", @"—————",
                                                   nil];
  }
  return self;
}

- (void)dealloc {
  [self invalidateLetterAndWordTimers];
  delegate_ = nil;
  [resultText_ release];
  [pendingSignals_ release];
  [signalTable_ release];
  [letterTimer_ release];
  [wordTimer_ release];
  [beginDate_ release];
  [super dealloc];
}

#pragma mark -
#pragma mark Public

- (NSString*)signalsFromText:(NSString*)text {
  NSString* signals = @"";

  NSArray* objects = [self.signalTable allValues];
  NSArray* keys = [self.signalTable allKeys];

  // Convert each character into a string of signals.
  for (int i=0; i < [text length]; i++) {
    NSString* c =
        [NSString stringWithFormat:@"%C", [text characterAtIndex:i]];

    if ([objects containsObject:c]) {
      NSUInteger index = [objects indexOfObject:c];
      signals = [signals stringByAppendingFormat:@"%@ ",
                    [keys objectAtIndex:index]];
    }
    else {
      signals = [signals stringByAppendingString:@" "];
    }
  }

  return signals;
}

- (void)reset {
  [self invalidateLetterAndWordTimers];
  self.resultText = @"";
  self.pendingSignals = @"";
  self.beginDate = nil;
  [delegate_ morse:self updatedResultText:resultText_];
  [delegate_ morse:self updatedPendingSignals:pendingSignals_];
}

- (void)tapBegin {
  // Avoid inserting letter or word boundaries while holding a tap.
  [self invalidateLetterAndWordTimers];

  // Save a timestamp so we can determine the duration of tap in -tapEnd.
  self.beginDate = [NSDate date];
}

- (void)tapEnd {
  // Begin the timer to watch for letter and word boundaries.
  [self beginLetterAndWordTimers];

  // Update the pending signals.
  NSString* text = [self signalForBeginDate:beginDate_ endDate:[NSDate date]];
  self.pendingSignals = [self.pendingSignals stringByAppendingString:text];
  [delegate_ morse:self updatedPendingSignals:pendingSignals_];
}

#pragma mark -
#pragma mark Timers

- (void)invalidateLetterAndWordTimers {
  [letterTimer_ invalidate];
  [wordTimer_ invalidate];
}

- (void)beginLetterAndWordTimers {
  [self invalidateLetterAndWordTimers];

  // |kDotLength| is in ms, so multiply and convert to seconds.
  NSTimeInterval interval = (kDotLength * kLetterMultiplier) / 1000.0;
  self.letterTimer =
      [NSTimer scheduledTimerWithTimeInterval:interval
                                       target:self
                                     selector:@selector(insertLetter)
                                     userInfo:nil
                                      repeats:NO];

  // |kDotLength| is in ms, so multiply and convert to seconds.
  interval = (kDotLength * kWordMultiplier) / 1000.0;
  self.wordTimer =
      [NSTimer scheduledTimerWithTimeInterval:interval
                                       target:self
                                     selector:@selector(insertWordBoundary)
                                     userInfo:nil
                                      repeats:NO];
}

- (void)insertLetter {
  // Translate the letter here.
  NSString* letter = [self letterForSignals:pendingSignals_];

  if (letter) {
    self.resultText = [self.resultText stringByAppendingString:letter];
  }

  // Clear pending signals and notify our delegate.
  self.pendingSignals = @"";
  [delegate_ morse:self updatedResultText:resultText_];
  [delegate_ morse:self updatedPendingSignals:pendingSignals_];
}

- (void)insertWordBoundary {
  // Add a space and notify our delegate.
  self.resultText = [self.resultText stringByAppendingString:@" "];
  [delegate_ morse:self updatedResultText:resultText_];
}

#pragma mark -
#pragma mark Private

- (NSString*)signalForBeginDate:(NSDate*)beginDate
                        endDate:(NSDate*)endDate {
  NSString* text = @"—";
  NSTimeInterval duration = ([endDate timeIntervalSince1970] -
                             [beginDate timeIntervalSince1970]) * 1000;
  if (duration < kDotLength * kDashMultiplier) {
    text = @"•";
  }
  return text;
}

- (NSString*)letterForSignals:(NSString*)signals {
  return [signalTable_ objectForKey:signals];
}

@end
