//
//  MorseDemo.m
//  MorseKeyboard
//
//  Created by David Patierno on 2/23/12.
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

#import "MorseDemo.h"

static NSUInteger speedMultiplier = 5;

@interface Morse ()
- (void)insertLetter;
- (void)insertWordBoundary;
@end

@implementation MorseDemo

@synthesize demoText = demoText_;
@synthesize demoSignals = demoSignals_;
@synthesize currentIndex = currentIndex_;

- (id)initWithDelegate:(id<MorseDelegate>)delegate {
  if ((self = [super initWithDelegate:delegate])) {
    demoText_ = @"IVE BEEN MEANING TO TELL YOU FOR SOME TIME ABOUT MY STRONG "
                @"FEELINGS FOR YOU THAT TIME WE WENT TO GET TACOS WAS A "
                @"WONDERFUL NIGHT THAT CREEPS INTO MY MEMORIES FREQUENTLY "
                @"APRIL FOOLS";
    demoSignals_ = [[self signalsFromText:demoText_] retain];
    currentIndex_ = 0;
  }
  return self;
}

- (void)dealloc {
  [demoText_ release];
  [demoSignals_ release];
  [super dealloc];
}

- (void)reset {
  [super reset];
  currentIndex_ = 0;
}

- (void)tapBegin {
  // Do nothing.
}

- (void)tapEnd {
  for (int i=0; i<speedMultiplier; i++) {
    // Loop the message forever.
    if (currentIndex_ >= [demoSignals_ length]) {
      currentIndex_ = 0;
      [self insertWordBoundary];
      if (i != 0) {
        break;
      }
    }

    // Update the pending signals.
    NSRange range = NSMakeRange(currentIndex_++, 1);
    NSString* signal = [demoSignals_ substringWithRange:range];
    if ([signal isEqual:@" "]) {
      if ([self.pendingSignals length]) {
        [self insertLetter];
      } else {
        [self insertWordBoundary];
      }
    }
    else {
      self.pendingSignals =
          [self.pendingSignals stringByAppendingString:signal];
      [self.delegate morse:self updatedPendingSignals:self.pendingSignals];
    }
  }
}

@end
